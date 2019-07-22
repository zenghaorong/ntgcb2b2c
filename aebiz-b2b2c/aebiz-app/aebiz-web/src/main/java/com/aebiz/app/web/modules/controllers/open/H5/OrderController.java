package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.cms.modules.models.Cms_video;
import com.aebiz.app.cms.modules.services.CmsVideoService;
import com.aebiz.app.goods.modules.models.Goods_image;
import com.aebiz.app.goods.modules.models.Goods_main;
import com.aebiz.app.goods.modules.models.Goods_product;
import com.aebiz.app.goods.modules.services.GoodsImageService;
import com.aebiz.app.goods.modules.services.GoodsProductService;
import com.aebiz.app.goods.modules.services.GoodsService;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.models.Member_Integral_Detail;
import com.aebiz.app.integral.modules.services.MemberIntegralDetailService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.member.modules.models.Member_address;
import com.aebiz.app.member.modules.models.Member_cart;
import com.aebiz.app.member.modules.models.Member_coupon;
import com.aebiz.app.member.modules.services.MemberAddressService;
import com.aebiz.app.member.modules.services.MemberCartService;
import com.aebiz.app.member.modules.services.MemberCouponService;
import com.aebiz.app.order.modules.models.Order_goods;
import com.aebiz.app.order.modules.models.Order_goods_feedback;
import com.aebiz.app.order.modules.models.Order_main;
import com.aebiz.app.order.modules.models.Order_pay_refunds;
import com.aebiz.app.order.modules.models.em.OrderPayStatusEnum;
import com.aebiz.app.order.modules.models.em.OrderStatusEnum;
import com.aebiz.app.order.modules.models.em.OrderTypeEnum;
import com.aebiz.app.order.modules.services.OrderGoodsService;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.order.modules.services.OrderPayRefundsService;
import com.aebiz.app.sales.modules.models.Sales_coupon;
import com.aebiz.app.sales.modules.services.SalesCouponService;
import com.aebiz.app.sys.modules.models.Sys_dict;
import com.aebiz.app.sys.modules.services.SysDictService;
import com.aebiz.app.web.commons.utils.CalculateUtils;
import com.aebiz.app.web.commons.utils.WXPayUtil;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author chengzhuming
 * @date 2019/4/1 19:52
 */
@Controller
@RequestMapping("/open/h5/order")
public class OrderController {
    private static final Log log = Logs.get();
    @Autowired
    private MemberAddressService memberAddressService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsImageService goodsImageService;

    @Autowired
    private GoodsProductService goodsProductService;

    @Autowired
    private SysDictService sysDictService;

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private CmsVideoService cmsVideoService;

    @Autowired
    private MemberCartService memberCartService;

    @Autowired
    private OrderPayRefundsService orderPayRefundsService;

    @Autowired
    private MemberCouponService memberCouponService;

    @Autowired
    private MemberIntegralService memberIntegralService;

    @Autowired
    private MemberIntegralDetailService memberIntegralDetailService;

    @Autowired
    private SalesCouponService salesCouponService;


    /**
     * 进入订单确认页
     */
    @RequestMapping("/orderConfirmation.html")
    public String orderConfirmation(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = new Account_user();
        try {
             accountUser = (Account_user) subject.getPrincipal();
            if (accountUser == null) {
                return "pages/front/h5/niantu/login";
            }
        }catch (Exception e){
            return "pages/front/h5/niantu/login";
        }
        String productList = request.getParameter("productList");
        String cartIds = request.getParameter("cartIds");
//        try{
//            String[] ids = StringUtils.split(cartIds, ";");
//
//            for (int i = 0 ; i<ids.length;i++){
//                Member_cart cart = memberCartService.fetch(ids[i]);
//                cart.setDelFlag(true);
//                memberCartService.update(cart);
//            }
//        }catch (Exception e){
//
//        }

//        List<Map<String,Object>> list = (List<Map<String, Object>>) JSON.parse(productList);
//        String productIds = request.getParameter("productIds");
//        String num = request.getParameter("num");
//        String videoId = request.getParameter("videoId");
//
//        request.setAttribute("productIds",productIds);
//        request.setAttribute("num",num);
        request.setAttribute("productList",productList);
        request.setAttribute("cartIds",cartIds);
        Cnd cnda = Cnd.NEW();
        cnda.and("accountId", "=", accountUser.getAccountId() );
        cnda.and("defaultValue","=",true);
        List<Member_address> list = memberAddressService.query(cnda);
        Member_address member_address = new Member_address();
        String freight = sysDictService.getNameByCode("freight2");
        if(list!=null&&list.size()>0){
            member_address=list.get(0);
            List<Sys_dict> freight1 = sysDictService.getSubListByCode("freight");
            for (Sys_dict f:freight1
                 ) {
                if(f.getName().equals(member_address.getProvince())){
                    freight=f.getCode();
                    break;
                }
            }
        }
        String IntegralRate = sysDictService.getNameByCode("IntegralRate");

        BigDecimal b1 = new BigDecimal(freight);
        Double freightMoney = b1.doubleValue();

        BigDecimal b2 = new BigDecimal(IntegralRate);
        Double IR = b2.doubleValue();

        request.setAttribute("IntegralRate",IR);
        request.setAttribute("freightMoney",freightMoney);
        return "pages/front/h5/niantu/orderConfirmation";
    }

    /**
     * 获取订单信息
     */
    @RequestMapping("/getOrderInfo.html")
    @SJson
    public Result getOrderInfo(HttpServletRequest request) {
        String id = request.getParameter("id");
        request.setAttribute("orderId",id);
        try {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if(accountUser==null){
            return Result.error(-1,"请先登录");
        }
        //查询收获地址
        Cnd cnd = Cnd.NEW();
        cnd.and("accountId", "=", accountUser.getAccountId() );
        cnd.and("defaultValue","=",true);
        List<Member_address> list = memberAddressService.query(cnd);


        //查询商品信息
        Cnd pCnd = Cnd.NEW();

        Order_main order = orderMainService.fetch(id);
        if(list!=null&&list.size()>0){
            order.setAddresses(list.get(0));
        }
        Cnd ogCnd = Cnd.NEW();
        ogCnd.and("orderId","=",order.getId());
            List<Order_goods> ogList = orderGoodsService.query(ogCnd);
            for (Order_goods og :
                    ogList) {
                Cnd imgCnd = Cnd.NEW();
                imgCnd.and("goodsId","=",og.getGoodsId());
                List<Goods_image> imgList = goodsImageService.query(imgCnd);
                og.setImgUrl(imgList.get(0).getImgAlbum());
            }
            order.setGoodsList(ogList);
        return Result.success("ok",order);
        }catch (Exception e){
            log.error("获取订单信息失败",e);
            return Result.error("fail");
        }
    }

    /**
     * 进入我的订单列表页
     * @param status 订单状态
     * @return
     */
    @RequestMapping("/goOrderList.html")
    public String goOrderList(String status,HttpServletRequest request) {
        request.setAttribute("status",status);
        return "pages/front/h5/niantu/orderList";
    }

    /**
     * 进入订单详情页
     */
    @RequestMapping("/goOrderInfo.html")
    public String goOrderInfo(String orderId,HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        try {
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if (accountUser == null) {
                return "pages/front/h5/niantu/login";
            }
        }catch (Exception e){
            return "pages/front/h5/niantu/login";
        }
        request.setAttribute("orderId",orderId);
        Order_main order_main = orderMainService.fetch(orderId);
        request.setAttribute("order",order_main);
        if(order_main!=null&&order_main.getMinusPoints()!=null&&order_main.getMinusPoints()>0){
            request.setAttribute("points",order_main.getMinusPoints());
        }
        if(order_main!=null&&order_main.getMemberCouponId()!=null){
            Member_coupon member_coupon = memberCouponService.fetch(order_main.getMemberCouponId());
            Sales_coupon sales_coupon = salesCouponService.fetch(member_coupon.getCouponId());
            request.setAttribute("sales_coupon",sales_coupon);

        }
        Cnd orderrefCnd = Cnd.NEW();
        orderrefCnd.and("orderId","=",orderId);
        orderrefCnd.and("delFlag","=",false);
        List<Order_pay_refunds> oprList = orderPayRefundsService.query(orderrefCnd);
        if(oprList!=null&&oprList.size()>0){
            request.setAttribute("refunds",oprList.get(0));
        }else {
            request.setAttribute("refunds",null);
        }
        double money = order_main.getPayMoney();
        double payMoney = CalculateUtils.div(money,100,2);
        request.setAttribute("payMoney",payMoney);
        return "pages/front/h5/niantu/orderInfo";
    }

    /**
     * 进入订单详情页
     */
    @RequestMapping("/goOrderPayInfo.html")
    public String goOrderPayInfo(String orderId,HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        try {
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if (accountUser == null) {
                return "pages/front/h5/niantu/login";
            }
        }catch (Exception e){
            return "pages/front/h5/niantu/login";
        }
        request.setAttribute("orderId",orderId);
        Order_main order_main = orderMainService.fetch(orderId);
        request.setAttribute("order",order_main);
        double money = order_main.getPayMoney();
        double payMoney = CalculateUtils.div(money,100,2);
        request.setAttribute("payMoney",payMoney);
        return "pages/front/h5/niantu/orderPayInfo";
    }


    /**
     * 进入收银台
     */
    @RequestMapping("/checkoutCounter.html")
    public String checkoutCounter(HttpServletRequest request,String productList,String addressId,String couponId,String integralMoney,String cartIds) {

        List<Map<String,Object>> list = (List<Map<String, Object>>) JSON.parse(productList);

        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        //查询收货信息
        Member_address member_address = memberAddressService.fetch(addressId);
        String freight = sysDictService.getNameByCode("freight2");
        List<Sys_dict> freight1 = sysDictService.getSubListByCode("freight");
            for (Sys_dict f:freight1
                    ) {
                if(f.getName().equals(member_address.getProvince())){
                    freight=f.getCode();
                    break;
                }
            }
//        Integer.parseInt(freight) * 100; //运费
        Double v = Double.parseDouble(freight)* 100;
        int freightMoney =v.intValue();
        int freeMoney = 0;
        int totalMoney =0; //单位是分
        int totalNum=0;
        Order_main order_main = new Order_main();
        order_main.setAccountId(accountUser.getAccountId());
        order_main.setGoodsFreeMoney(0);
        order_main.setFreeMoney(0);
        order_main.setPayStatus(0);
        order_main.setOrderStatus(0);
        order_main.setOrderAt(DateUtil.getTime(new Date()));
        order_main.setDeliveryName(member_address.getFullName());

        order_main.setDeliveryProvince(member_address.getProvince());
        order_main.setDeliveryCity(member_address.getCity());
        order_main.setDeliveryCounty(member_address.getCounty());
        order_main.setDeliveryAddress(member_address.getAddress());
        order_main.setDeliveryMobile(member_address.getMobile());
        order_main.setOrderType(OrderTypeEnum.product_order_type.getKey());
        Order_main order = orderMainService.insert(order_main);
        for (Map<String,Object> map:list
                ) {
            String id = (String) map.get("productId");
            String num = (String) map.get("num");
            int thisNum = Integer.parseInt(num);
            Goods_main good = goodsService.fetch(id);

            Order_goods order_goods = new Order_goods();


            Cnd proCnd = Cnd.NEW();
            proCnd.and("goodsId", "=", good.getId());
            List<Goods_product> gpList = goodsProductService.query(proCnd);
            if (gpList != null && gpList.size() > 0) {
                Goods_product goods_product = gpList.get(0);
                Integer salePrice = goods_product.getSalePrice(); //单位是分
                //判断取批发价
                if(goods_product.getWholesaleNum()!=null && thisNum>=goods_product.getWholesaleNum()){
                    Integer price =   goods_product.getWholesalePrice();
                    salePrice = price;
                }


                int n = Integer.parseInt(num);
                totalMoney += salePrice * n;
                totalNum+=n;
                order_main.setStoreId(good.getStoreId());
                order_goods.setOrderId(order.getId());
                order_goods.setAccountId(order.getAccountId());
                order_goods.setGoodsId(good.getId());
                order_goods.setStoreId(good.getStoreId());
                order_goods.setGoodsName(good.getName());
                order_goods.setProductId(goods_product.getId());
                order_goods.setSku(goods_product.getSku());
                order_goods.setName(goods_product.getName());
                order_goods.setBuyNum(n);
                order_goods.setSalePrice(goods_product.getSalePrice());
                order_goods.setBuyPrice(goods_product.getSalePrice() - freeMoney);
                order_goods.setTotalMoney(goods_product.getSalePrice() * n);
                order_goods.setFreeMoney(freeMoney);
                Cnd imgCnd = Cnd.NEW();
                imgCnd.and("goodsId","=",order_goods.getGoodsId());
                List<Goods_image> imgList = goodsImageService.query(imgCnd);
                if(imgList!=null&&imgList.size()>0){
                    order_goods.setImgUrl(imgList.get(0).getImgAlbum());
                }

                order_goods.setPayMoney(goods_product.getSalePrice() * n - freeMoney);
                order_goods.setOrderType(OrderTypeEnum.product_order_type.getKey());
                orderGoodsService.insert(order_goods);
                int i = goods_product.getStock() - n;
                if(i<0){
                    i=0;
                }

                goods_product.setStock(i);
                goodsService.update(goods_product);

            }
        }
        int im=0;
        if(StringUtils.isNotEmpty(integralMoney)){
             im = Integer.parseInt(integralMoney);


        }
        Map<String, Double> money = this.calCouponMoney(accountUser, couponId, totalMoney, freightMoney, totalNum,im,order);
        order.setGoodsMoney(money.get("totalMoney").intValue());
        order.setPayMoney(money.get("totalMoney").intValue() +money.get("freightMoney").intValue() );
//                order_main.setPayMoney(1); //先写死一个测试金额
        order.setFreightMoney(money.get("freightMoney").intValue());
        order.setMinusPoints(im);//下单扣除积分
        orderMainService.update(order);
        request.setAttribute("order", order);

        //清楚购物车
        if(Strings.isNotEmpty(cartIds)) {
            try {
                String[] ids = StringUtils.split(cartIds, ";");
                for (int i = 0; i < ids.length; i++) {
                    memberCartService.delete(ids[i]);
                }
            } catch (Exception e) {
                log.error("购物车清除异常", e);
            }
        }

        return "pages/front/h5/niantu/checkoutCounter";
    }

    /**
     * 订单详情页 待支付继续支付 进入收银台
     */
    @RequestMapping("/orderInfoPayCheckoutCounter.html")
    public String checkoutCounter(HttpServletRequest request,String orderId) {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        Order_main order_main = orderMainService.fetch(orderId);
        request.setAttribute("order", order_main);
        return "pages/front/h5/niantu/checkoutCounter";
    }


    /**
     * 视频下单进入订单确认页
     */
    @RequestMapping("/videoOrderConfirmation.html")
    public String videoOrderConfirmation(HttpServletRequest request,String videoId,String type,Integer num) {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if (accountUser == null) {
            return "pages/front/h5/niantu/login";
        }
        request.setAttribute("videoId",videoId);
        request.setAttribute("type",type);
        request.setAttribute("num",num);

        //计算包月价格
        String monthlyPrice = sysDictService.getNameByCode("Monthly_price");
        BigDecimal b1 = new BigDecimal(monthlyPrice);
        Double payMoney = CalculateUtils.mul(b1.doubleValue(), num); //会员包月价格
        request.setAttribute("payMoney", payMoney);
        request.setAttribute("monthlyPrice", monthlyPrice);
        String IntegralRate = sysDictService.getNameByCode("IntegralRate");
        BigDecimal b2 = new BigDecimal(IntegralRate);
        Double IR = b2.doubleValue();

        request.setAttribute("IntegralRate",IR);
        return "pages/front/h5/niantu/videoOrderConfirmation";
    }

    /**
     * 视频下单进入收银台
     * @param request
     * @param videoId
     * @param couponId
     * @param type 2:单个购买  3:包月
     * @return
     */
    @RequestMapping("/videoCheckoutCounter.html")
    public String videoCheckoutCounter(HttpServletRequest request,String videoId,String couponId,String type,Integer monthlyNum,String integralMoney) {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        float integralToMoney=0;
        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        Cms_video cms_video=cmsVideoService.fetch(videoId);
        int totalMoney = (int)CalculateUtils.mul(cms_video.getPrice(),100); //转为分

        Order_main order_main = new Order_main();

        /**
         * 计算优惠劵抵扣金额
         */
        if(couponId!=null) {
            Cnd cndCoupon = Cnd.NEW();
            cndCoupon.and("accountId", "=", accountUser.getAccountId());
            cndCoupon.and("status", "=", 0);//未使用
            cndCoupon.and("couponId", "=", couponId);//未使用
            List<Member_coupon> member_couponList = memberCouponService.query(cndCoupon);
            if (member_couponList != null && member_couponList.size() > 0) {
                Member_coupon member_coupon = member_couponList.get(0);
                Sales_coupon sales_coupon = salesCouponService.fetch(member_coupon.getCouponId());
                order_main.setMemberCouponId(member_coupon.getId());
                //判断优惠劵状态
                if (!sales_coupon.isDisabled()) {
                    int time = (int) WXPayUtil.getCurrentTimestamp();
                    //判断是否过期
                    if (sales_coupon.getStartTime() < time || sales_coupon.getEndTime() > time) {
                        double payMoney = CalculateUtils.div(totalMoney,100,2);//转换为元
                        if ("1".equals(sales_coupon.getType())) { //满减
                            if (sales_coupon.getConditionAmount() != null) {
                                if (payMoney >= sales_coupon.getConditionAmount()) {
                                    payMoney = CalculateUtils.sub(payMoney,sales_coupon.getDeductibleAmount());
                                    totalMoney = (int)CalculateUtils.mul(payMoney,100); //转化为分

                                    member_coupon.setStatus(1);
                                    memberCouponService.update(member_coupon);

                                }
                            }
                        }
                    }

                }
            }
        }
        int i1 = 0;
        if(StringUtils.isNotEmpty(integralMoney)){
            i1 = Integer.parseInt(integralMoney);
//            下单扣除积分
            if(i1>0){
                Cnd cnd3 = Cnd.NEW();
                cnd3.and("delFlag", "=", false);
                cnd3.and("customerUuid","=",accountUser.getAccountId());
                List<Member_Integral> list=memberIntegralService.query(cnd3);
                if(list!=null&&list.size()>0){
                    Member_Integral memIntegral = list.get(0);
                    if(memIntegral.getUseAbleIntegral()<i1){

                    }else {
                        String itm = sysDictService.getNameByCode("integralToMoney");
                        int i = Integer.parseInt(itm);
                        float fi =i1;
                        float imoney = (fi/i);
                        float total=totalMoney-imoney*100;
                        integralToMoney=imoney*100;
                        if(total<=0){
//                        break;
                        }else {
                            totalMoney=(int)total;
                            memIntegral.setUseAbleIntegral(memIntegral.getUseAbleIntegral()-i1);
                            memberIntegralService.update(memIntegral);
                            Member_Integral_Detail  mid = new Member_Integral_Detail();
                            mid.setIntegralDesc("购物减积分");
                            mid.setIntegralType(4);
                            mid.setCustomerUuid(accountUser.getAccountId());
                            mid.setAddIntegral(i1);
                            memberIntegralDetailService.insert(mid);
                        }

                    }

                }
            }
        }
        Order_goods order_goods = new Order_goods();
        order_main.setAccountId(accountUser.getAccountId());
        order_main.setStoreId(cms_video.getStoreId());
        order_main.setGoodsFreeMoney(0);


        order_main.setFreightMoney(0);
        order_main.setFreeMoney(0);
        order_main.setPayStatus(0);
        order_main.setOrderStatus(0);
        order_main.setOrderAt(DateUtil.getTime(new Date()));
        if("3".equals(type)) {
            order_main.setOrderType(OrderTypeEnum.monthly_order_type.getKey());
            String monthlyPrice = sysDictService.getNameByCode("Monthly_price");
            BigDecimal b1 = new BigDecimal(monthlyPrice);
            int payMoney = (int)CalculateUtils.mul(b1.doubleValue(),100); //会员包月价格
            payMoney = payMoney * monthlyNum; //乘上月数
            if(integralToMoney>0){
                int itmoney =(int) integralToMoney;
                payMoney = payMoney-itmoney;
            }
            order_main.setPayMoney(payMoney); //单位是分
            order_main.setMonthlyNum(monthlyNum);
            order_main.setGoodsMoney(payMoney);
        }else {
            order_main.setOrderType(OrderTypeEnum.video_order_type.getKey());
            order_main.setPayMoney(totalMoney); //单位是分
            //视频和会员包月价格转换为分
            Double goodsMoney = CalculateUtils.mul(cms_video.getPrice(),100);
            order_main.setGoodsMoney(goodsMoney.intValue());
        }
        order_main.setVideoId(videoId);
        order_main.setMinusPoints(i1);//下单扣除积分
        Order_main order = orderMainService.insert(order_main);
        order_goods.setOrderId(order.getId());
        order_goods.setAccountId(order.getAccountId());
        order_goods.setGoodsId(cms_video.getId());
        order_goods.setStoreId(order_main.getStoreId());
        order_goods.setGoodsName(cms_video.getVideoTitle());
        order_goods.setProductId(cms_video.getId());
        order_goods.setSku(cms_video.getId());
        order_goods.setName(cms_video.getVideoTitle());
        order_goods.setBuyNum(1);
        Double price = CalculateUtils.mul(cms_video.getPrice(),100);
        order_goods.setSalePrice(price.intValue());
        order_goods.setBuyPrice(totalMoney);
        if("3".equals(type)) {
            order_main.setOrderType(OrderTypeEnum.monthly_order_type.getKey());
            order_goods.setGoodsName("会员包月（"+monthlyNum+"个月）");
            order_goods.setName("会员包月（"+monthlyNum+"个月）");
            order_goods.setOrderType(OrderTypeEnum.monthly_order_type.getKey());
        }else {
            order_main.setOrderType(OrderTypeEnum.video_order_type.getKey());
            order_goods.setOrderType(OrderTypeEnum.video_order_type.getKey());
        }
        order_goods.setImgUrl(cms_video.getImageUrl());

        orderGoodsService.insert(order_goods);
        request.setAttribute("order",order);
        return "pages/front/h5/niantu/checkoutCounter";
    }

    @RequestMapping("/submitOrder.html")
    @SJson
    public Result submitOrder(HttpServletRequest request){
        Subject subject = SecurityUtils.getSubject();
        try {
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if (accountUser == null) {
                return Result.error("请登录！");
            }
        }catch (Exception e){
            return Result.error("系统异常！");
        }
        List<Map<String,Object>> pList = new ArrayList<>();
        String cartIds = request.getParameter("cartIds");
        try{
            String[] ids = StringUtils.split(cartIds, ";");

            for (int i = 0 ; i<ids.length;i++){
                Map<String,Object> p = new HashMap<>();
                Member_cart cart = memberCartService.fetch(ids[i]);
                p.put("productId",cart.getGoodsId());
                p.put("num",cart.getNum().toString());
                Cnd proCnd = Cnd.NEW();
                proCnd.and("goodsId", "=", cart.getGoodsId());
                List<Goods_product> gpList = goodsProductService.query(proCnd);
                if (gpList != null && gpList.size() > 0) {
                    Goods_product goods_product = gpList.get(0);
                    if(goods_product.getStock()<cart.getNum()){
                        return Result.error(-1,"库存不足！");
                    }
                }

                //判断库存
                Goods_main goods_main = goodsService.fetch(cart.getGoodsId());
                if(goods_main!=null){
                    if(!goods_main.isSale()){
                        return Result.error(-1,goods_main.getName()+"，已下架");
                    }
                }

                pList.add(p);
            }
        }catch (Exception e){

        }
        return Result.success("ok",JSONArray.toJSONString(pList));
    }


    /**
     * 获得我的订单列表
     * @return
     */
    @RequestMapping("getMyOrderList.html")
    @SJson
    public Result getMyOrderList(HttpServletRequest request){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            Cnd cndMain = Cnd.NEW();
            String status = request.getParameter("status");
            if(status!=null) {
                if ("1".equals(status)) {
                    cndMain.and("payStatus", "=", 0);
                }
                if ("2".equals(status)) {
                    cndMain.and("payStatus", "=", 3);
                    cndMain.and("deliveryStatus", "=", 0);
                }
                if ("3".equals(status)) {
                    cndMain.and("payStatus", "=", 3);
                    cndMain.and("orderStatus", "<", 5);
                    cndMain.and("deliveryStatus", "=", 3);
                    cndMain.and("getStatus", "=", 0);
                }
                if ("4".equals(status)) {
                    cndMain.and("payStatus", "=", 3);
                    cndMain.and("orderStatus", "=", 5);
                    cndMain.and("feedStatus", "=", 0);
                    cndMain.and("getStatus", "=", 1);

                }
                if ("5".equals(status)) {
                    cndMain.and("payStatus", "in", OrderPayStatusEnum.REFUNDWAIT.getKey() + "," + OrderPayStatusEnum.REFUNDALL.getKey());
                }
            }
//            cndMain.and("orderType", "=", OrderTypeEnum.product_order_type.getKey());
            cndMain.and("accountId", "=", accountUser.getAccountId());
            cndMain.orderBy("orderAt","desc");
            cndMain.and("delFlag","=",false);
            List<Order_main> order_mainList = orderMainService.query(cndMain);

            List<Order_goods> order_goodsList = new ArrayList<>();
            for (Order_main o:order_mainList){
                Cnd cndOrder = Cnd.NEW();
                cndOrder.and("orderId","=",o.getId());
                List<Order_goods> order_goods = orderGoodsService.query(cndOrder);
                //商品订单
                if(OrderTypeEnum.product_order_type.getKey().equals(o.getOrderType())) {
                    if (order_goods != null && order_goods.size() > 0) {
                        for (int i = 0; i < order_goods.size(); i++) {
                            Cnd imgCnd = Cnd.NEW();
                            Order_goods good = order_goods.get(i);
                            imgCnd.and("goodsId", "=", good.getGoodsId());
                            List<Goods_image> imgList = goodsImageService.query(imgCnd);
                            if (imgList != null && imgList.size() > 0) {
                                good.setImgUrl(imgList.get(0).getImgAlbum());
                            }
                        }
                    }
                }
                //视频订单
                if(OrderTypeEnum.video_order_type.getKey().equals(o.getOrderType())) {
                    if (order_goods != null && order_goods.size() > 0) {
                        for (int i = 0; i < order_goods.size(); i++) {
                            Order_goods good = order_goods.get(i);
                            Cms_video cms_video = cmsVideoService.fetch(good.getProductId());
                            good.setImgUrl(cms_video.getImageUrl());
                        }

                    }
                }
                o.setGoodsList(order_goods);
                String date = DateUtil.getDate(o.getOrderAt());
                o.setOrderTime(date);

            }

            return Result.success("ok",order_mainList);
        } catch (Exception e) {
            log.error("获取视频列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 获得我的订单数量
     * @return
     */
    @RequestMapping("getMyOrderCount.html")
    @SJson
    public Result getMyOrderCount(HttpServletRequest request){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();

            Map<String,Integer> returnMap = new HashedMap();

            //总订单数
            Cnd cndMain = Cnd.NEW();
            cndMain.and("accountId", "=", accountUser.getAccountId());
            cndMain.and("delFlag","=",false);
            int num = orderMainService.count(cndMain);
            returnMap.put("num",num);

            //代付款订单数
            Cnd cndMain1 = Cnd.NEW();
            cndMain1.and("payStatus", "=", 0);
            cndMain1.and("accountId", "=", accountUser.getAccountId());
            cndMain1.and("delFlag","=",false);
            int num1 = orderMainService.count(cndMain1);
            returnMap.put("num1",num1);


            //待发货订单数
            Cnd cndMain2 = Cnd.NEW();
            cndMain2.and("payStatus", "=", 3);
            cndMain2.and("deliveryStatus", "=", 0);
            cndMain2.and("accountId", "=", accountUser.getAccountId());
            cndMain2.and("delFlag","=",false);
            int num2 = orderMainService.count(cndMain2);
            returnMap.put("num2",num2);

            //待收货订单数
            Cnd cndMain3 = Cnd.NEW();
            cndMain3.and("payStatus", "=", 3);
            cndMain3.and("deliveryStatus", "=", 3);
            cndMain3.and("getStatus", "=", 0);
            cndMain3.and("orderStatus", "<", 5);
            cndMain3.and("accountId", "=", accountUser.getAccountId());
            cndMain3.and("delFlag","=",false);
            int num3 = orderMainService.count(cndMain3);
            returnMap.put("num3",num3);

            //待评价
            Cnd cndMain4 = Cnd.NEW();
            cndMain4.and("payStatus", "=", 3);
            cndMain4.and("getStatus", "=", 1);
            cndMain4.and("orderStatus", "=", 5);
            cndMain4.and("feedStatus", "=", 0);
            cndMain4.and("accountId", "=", accountUser.getAccountId());
            cndMain4.and("delFlag","=",false);
            int num4 = orderMainService.count(cndMain4);
            returnMap.put("num4",num4);

            //售后退款
            Cnd cndMain5 = Cnd.NEW();
            cndMain5.and("payStatus", "in", "4,6");
            cndMain5.and("accountId", "=", accountUser.getAccountId());
            cndMain5.and("delFlag","=",false);
            int num5 = orderMainService.count(cndMain5);
            returnMap.put("num5",num5);

//            cndMain.and("orderType", "=", OrderTypeEnum.product_order_type.getKey());



            return Result.success("ok",returnMap);
        } catch (Exception e) {
            log.error("获取视频列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 根据订单编号查询订单组商品列表
     */
    @RequestMapping("getOrderProductList.html")
    @SJson
    public Result getOrderProductList(String orderId){
        try {

            Order_main order_main = orderMainService.fetch(orderId);

            Cnd cndOrder = Cnd.NEW();
            cndOrder.and("orderId","=",orderId);
            List<Order_goods> order_goods = orderGoodsService.query(cndOrder);

            for(Order_goods o:order_goods) {
                if("1".equals(order_main.getOrderType())) {
                    Cnd imgCnd = Cnd.NEW();
                    imgCnd.and("goodsId", "=", o.getGoodsId());
                    List<Goods_image> imgList = goodsImageService.query(imgCnd);
                    if (imgList != null && imgList.size() > 0) {
                        o.setImgUrl(imgList.get(0).getImgAlbum());
                    }
                }
                if("2".equals(order_main.getOrderType())) {
                    Cms_video cms_video = cmsVideoService.fetch(o.getGoodsId());
                    o.setImgUrl(cms_video.getImageUrl());
                }
            }


            return Result.success("ok",order_goods);
        } catch (Exception e) {
            log.error("获取视频列表异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 进入退款申请
     */
    @RequestMapping("goRefundApplication.html")
    public String goRefundApplication(HttpServletRequest request,String orderId) {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if (accountUser == null) {
            return "pages/front/h5/niantu/login";
        }
        request.setAttribute("orderId",orderId);
        return "pages/front/h5/niantu/videoOrderConfirmation";
    }

    /**
     * 确认收货
     * @param request
     * @param orderId
     * @return
     */
    @RequestMapping("confirmReceipt.html")
    @SJson
    public Result confirmReceipt(HttpServletRequest request,String orderId) {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if (accountUser == null) {
            return Result.success("fail","未登录！");
        }
        Order_main order_main = orderMainService.fetch(orderId);
        order_main.setGetStatus(1);
        order_main.setOrderStatus(5);
        orderMainService.update(order_main);
        return Result.success("ok");
    }
    /**
     * 删除订单
     * @param request
     * @param orderId
     * @return
     */
    @RequestMapping("delOrder.html")
    @SJson
    public Result delOrder(HttpServletRequest request,String orderId) {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if (accountUser == null) {
            return Result.success("fail","未登录！");
        }
        Order_main order_main = orderMainService.fetch(orderId);
        order_main.setOrderStatus(OrderStatusEnum.DEAD.getKey());
        order_main.setDelFlag(true);
        orderMainService.update(order_main);

        //更新优惠劵状态
        try{
            //待支付状态
            if(order_main.getPayStatus()==OrderPayStatusEnum.NO.getKey()){
               if(Strings.isNotEmpty(order_main.getMemberCouponId())){
                   Member_coupon member_coupon = memberCouponService.fetch(order_main.getMemberCouponId());
                   member_coupon.setStatus(0);
                   memberCouponService.update(member_coupon);
               }
            }
        }catch (Exception e){
            log.error("删除订单更新优惠劵状态异常",e);
        }

        //判断如果使用积分了则积分退回
        try{
            //待支付状态
            if(order_main.getPayStatus()==OrderPayStatusEnum.NO.getKey()){
                //判断是否使用了积分
                if(order_main.getMinusPoints()!=null && order_main.getMinusPoints()!=0){
                    Cnd iCnd = Cnd.NEW();
                    iCnd.and("customerUuid" ,"=",order_main.getAccountId());
                    List<Member_Integral> list2 = memberIntegralService.query(iCnd);
                    Member_Integral m = list2.get(0);
                    m.setCustomerUuid(order_main.getAccountId());
                    m.setUseAbleIntegral(m.getUseAbleIntegral()+order_main.getMinusPoints());
//                    int jf = m.getUseAbleIntegral()+order_main.getMinusPoints();
//                    m.setUseAbleIntegral(jf);
                    memberIntegralService.update(m);
                    Member_Integral_Detail md = new Member_Integral_Detail();
                    md.setAddIntegral(order_main.getMinusPoints());
                    md.setCustomerUuid(order_main.getAccountId());
                    md.setIntegralDesc("未支付订单积分退回");
                    md.setIntegralType(5);
                    memberIntegralDetailService.insert(md);
                }
            }
        }catch (Exception e){
            log.error("如果使用积分了则积分退回异常",e);
        }


        return Result.success("ok");
    }

    /**
     * 退款申请
     */
    @RequestMapping("refundApplication.html")
    @SJson
    public Result refundApplication(String orderId,String body){
        try {
            Order_main order_main = orderMainService.fetch(orderId);
            if(order_main!=null && OrderPayStatusEnum.PAYALL.getKey()==order_main.getPayStatus()){
                Order_pay_refunds order_pay_refunds =new Order_pay_refunds();
                orderPayRefundsService.insert(order_pay_refunds);
            }else {
                return Result.success("fail","订单状态错误");
            }
            return Result.success("ok");
        } catch (Exception e) {
            log.error("获取视频列表异常",e);
            return Result.error("fail");
        }
    }
    @RequestMapping("/checkStock.html")
    @SJson
    public Result checkStock(HttpServletRequest request){
        Subject subject = SecurityUtils.getSubject();
        try {
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if (accountUser == null) {
                return Result.error("请登录！");
            }
        }catch (Exception e){
            return Result.error("系统异常！");
        }
        try{
            String productList = request.getParameter("productList");
            List<Map<String,Object>> list = (List<Map<String, Object>>) JSON.parse(productList);
            for (Map<String,Object> map:list
                    ) {
                String id = (String) map.get("productId");
                String num = (String) map.get("num");
                Cnd proCnd = Cnd.NEW();
                proCnd.and("goodsId", "=", id);
                List<Goods_product> gpList = goodsProductService.query(proCnd);
                if (gpList != null && gpList.size() > 0) {
                    Goods_product goods_product = gpList.get(0);
                    if(goods_product.getStock()<Integer.parseInt(num)){
                        return Result.error(-1,"库存不足！");
                    }
                }
            }
        }catch (Exception e){

        }
        return Result.success();
    }
    private Map<String,Double> calCouponMoney(Account_user accountUser,String couponId,double totalMoney,double freightMoney,int totalNum,int integralMoney
    ,Order_main order_main){
        /**
         * 计算优惠劵抵扣金额
         */

        Map<String,Double> money = new HashMap<>();
        money.put("totalMoney",totalMoney);
        money.put("freightMoney",freightMoney);
        if(StringUtils.isNotEmpty(couponId)) {
            Cnd cndCoupon = Cnd.NEW();
            cndCoupon.and("accountId", "=", accountUser.getAccountId());
            cndCoupon.and("status", "=", 0);//未使用
            cndCoupon.and("couponId", "=", couponId);//未使用
            List<Member_coupon> member_couponList = memberCouponService.query(cndCoupon);
            if (member_couponList != null && member_couponList.size() > 0) {
                Member_coupon member_coupon = member_couponList.get(0);
                Sales_coupon sales_coupon = salesCouponService.fetch(member_coupon.getCouponId());
                order_main.setMemberCouponId(member_coupon.getId());
                //判断优惠劵状态
                if (!sales_coupon.isDisabled()) {
                    int time = (int) WXPayUtil.getCurrentTimestamp();
                    //判断是否过期
                    if (sales_coupon.getStartTime() < time || sales_coupon.getEndTime() > time) {
                        double payMoney = CalculateUtils.div(totalMoney,100,2);//转换为元
                        if ("1".equals(sales_coupon.getType())) { //满减
                            if (sales_coupon.getConditionAmount() != null) {
                                if (payMoney >= sales_coupon.getConditionAmount()) {
                                    payMoney = CalculateUtils.sub(payMoney,sales_coupon.getDeductibleAmount());
                                    totalMoney = (int)CalculateUtils.mul(payMoney,100); //转化回分
                                    money.put("totalMoney",totalMoney);
                                    order_main.setFreeMoney((int)CalculateUtils.mul(sales_coupon.getDeductibleAmount(),100));
                                    member_coupon.setStatus(1);
                                    memberCouponService.update(member_coupon);
                                }
                            }
                        }
                        if ("2".equals(sales_coupon.getType())) { //免运费劵
                            if(sales_coupon.getProductQuantityRule()!=null) {
                                if (totalNum >= sales_coupon.getProductQuantityRule()) {
                                    freightMoney = 0;
                                    money.put("freightMoney",freightMoney);
                                    order_main.setFreeMoney((int)CalculateUtils.mul(freightMoney,100));
                                    member_coupon.setStatus(1);
                                    memberCouponService.update(member_coupon);
                                }
                            }
                        }
                        if("3".equals(sales_coupon.getType())){ //折扣劵
                            if(sales_coupon.getProductQuantityRule()!=null) {
                                if (totalNum >= sales_coupon.getProductQuantityRule()) {
                                    int yPayMoney = (int)CalculateUtils.mul(payMoney,100);;
                                    payMoney = CalculateUtils.mul(payMoney,sales_coupon.getDiscount());
                                    totalMoney = (int)CalculateUtils.mul(payMoney,100); //转化回分
                                    money.put("totalMoney",totalMoney);
                                    order_main.setFreeMoney(yPayMoney-(int)CalculateUtils.mul(payMoney,100));
                                    member_coupon.setStatus(1);
                                    memberCouponService.update(member_coupon);
                                }
                            }
                        }
                    }

                }
            }
        }
//        下单扣除积分
        if(integralMoney>0){
            Cnd cnd3 = Cnd.NEW();
            cnd3.and("delFlag", "=", false);
            cnd3.and("customerUuid","=",accountUser.getAccountId());
            List<Member_Integral> list=memberIntegralService.query(cnd3);
            if(list!=null&&list.size()>0){
                Member_Integral memIntegral = list.get(0);
                if(memIntegral.getUseAbleIntegral()<integralMoney){
                    return money;
                }
                String itm = sysDictService.getNameByCode("integralToMoney");
                int i = Integer.parseInt(itm);
                float fi =integralMoney;
                float imoney = (fi/i);
                totalMoney=totalMoney-imoney*100;
                if(totalMoney<=0){
                    return money;
                }
                memIntegral.setUseAbleIntegral(memIntegral.getUseAbleIntegral()-integralMoney);
                memberIntegralService.update(memIntegral);
                Member_Integral_Detail  mid = new Member_Integral_Detail();
                mid.setIntegralDesc("购物减积分");
                mid.setIntegralType(4);
                mid.setCustomerUuid(accountUser.getAccountId());
                mid.setAddIntegral(integralMoney);
                memberIntegralDetailService.insert(mid);
            }
            money.put("totalMoney",totalMoney);
        }
        return money;
    }

}
