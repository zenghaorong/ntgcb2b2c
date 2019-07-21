package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.alipay.modules.models.AliPayFromQO;
import com.aebiz.app.alipay.modules.models.AlipayConfig;
import com.aebiz.app.alipay.modules.service.AlipayService;
import com.aebiz.app.goods.modules.models.Goods_product;
import com.aebiz.app.goods.modules.services.GoodsProductService;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.models.Member_Integral_Detail;
import com.aebiz.app.integral.modules.services.MemberIntegralDetailService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.order.modules.models.Order_goods;
import com.aebiz.app.order.modules.models.Order_main;
import com.aebiz.app.order.modules.models.em.*;
import com.aebiz.app.order.modules.services.OrderGoodsService;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.web.commons.utils.CalculateUtils;
import com.aebiz.app.web.commons.utils.HttpRequestUtil;
import com.aebiz.app.web.commons.utils.TcpipUtil;
import com.aebiz.app.web.commons.utils.WXPayUtil;
import com.aebiz.app.wx.modules.models.WxGetPayInfoQO;
import com.aebiz.app.wx.modules.services.WxConfigService;
import com.aebiz.app.wx.modules.services.WxPayService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.redis.RedisService;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Action;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/22  9:48
 * @Description: 支付相关控制器
 */
@Controller
@RequestMapping("/open/h5/pay")
public class PayH5Controller {

    private static final Log log = Logs.get();

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private RedisService redisService;

    private static final String WexinAccessToken="WexinAccessToken_key";

    @Autowired
    private PropertiesProxy config;

    @Autowired
    private WxConfigService wxConfigService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private GoodsProductService goodsProductService;

    @Autowired
    private MemberIntegralService memberIntegralService;

    @Autowired
    private MemberIntegralDetailService memberIntegralDetailService;


    /***
     * 微信H5支付调用控制层
     */
    @RequestMapping("/weiXinH5Pay.html")
    @SJson
    public Result weiXinH5Pay(String orderId,HttpServletRequest request){
        try {
            //查询订单信息
            Order_main order_main = orderMainService.fetch(orderId);
            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
            wxGetPayInfoQO.setOut_trade_no(orderId);
            wxGetPayInfoQO.setProductBody("黏土商城产品-订单支付");
            wxGetPayInfoQO.setProductId(order_main.getAccountId());
            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));
            String json=wxPayService.wxGetPayInfoH5(wxGetPayInfoQO);
            return Result.success("ok",json);
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail");
        }
    }

    /***
     * 微信jsapi支付调用控制层
     */
    @RequestMapping("/weiXinJsapiPay.html")
    @SJson
    public Result weiXinJsapiPay(String orderId,HttpServletRequest request){
        try {
            //查询订单信息
            Order_main order_main = orderMainService.fetch(orderId);
            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
            wxGetPayInfoQO.setOut_trade_no(orderId);
            wxGetPayInfoQO.setProductBody("body");
            wxGetPayInfoQO.setProductId(order_main.getAccountId());
            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));

            String wx_openId = (String) request.getSession().getAttribute("user_openId");
            wxGetPayInfoQO.setOpenId(wx_openId);

            String json = wxPayService.wxGetPayInfo(wxGetPayInfoQO);
            return Result.success("ok",json);
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail");
        }
    }

    /**
     * 进入微信jsapi获取code页；
     * @return
     */
    @RequestMapping("/index.html")
    public String index(String code,String orderId,HttpServletRequest request) {
        String appId = config.get("wx.pay.AppID");
        request.setAttribute("appId",appId);
        request.setAttribute("code",code);
        request.setAttribute("orderId",orderId);
        if(!Strings.isEmpty(code)) {
            String user_openId=(String) request.getSession().getAttribute("user_openId");
            if(user_openId==null) {
                String wxJson = wxConfigService.getWxApiAccessToken(true, code);
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(wxJson);
                String access_token = jsonObject.getString("access_token");
                String openId = jsonObject.getString("openid");
//            if(access_token==null){
//                wxConfigService.getWxApiAccessToken(true,code);
//            }
                log.info("获取到token-openId接口返回：" + access_token + "openId:" + openId);
                request.getSession().setAttribute("user_openId", openId);
            }
        }
        return "pages/front/h5/niantu/wxJsApiPay";
    }

    /***
     * 支付宝支付
     */
    @RequestMapping("/aliPay.html")
    public Result aliPay(String orderId, HttpServletRequest request, HttpServletResponse response){
        try {
            //查询订单信息
            Order_main order_main = orderMainService.fetch(orderId);
            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
            wxGetPayInfoQO.setOut_trade_no(orderId);
            wxGetPayInfoQO.setProductBody("body");
            wxGetPayInfoQO.setProductId(order_main.getAccountId());
            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));
            AliPayFromQO aliPayFromQO = new AliPayFromQO();
            aliPayFromQO.setOut_trade_no(orderId);
            aliPayFromQO.setProductBody("寻创意商品");
            aliPayFromQO.setSubject("寻创意商品订单");
            double orderPayMoney = order_main.getPayMoney();
            double payMoney = CalculateUtils.div(orderPayMoney,100,2);
            aliPayFromQO.setTotal_amount(payMoney+"");
            String form = alipayService.aliGetPayFrom(aliPayFromQO);
            response.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail");
        }
        return null;
    }

    /***
     * 支付宝H5支付回调通知地址
     */
    @RequestMapping("/aliNotify.html")
    @SJson
    public String aliNotify(HttpServletRequest request){
        try {
            //获取支付宝POST过来反馈信息
            Map<String,String> params = new HashMap<String,String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }
            log.info("支付宝回调所接收参数："+ JSON.toJSONString(params));
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            //支付金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
            //计算得出通知验证结果
            //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
            boolean verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

            //1.校验验签
            if(verify_result){//验证成功
                //请在这里加上商户的业务逻辑程序代码
                //2.查询订单明细
                Order_main order_main = orderMainService.fetch(out_trade_no);
                if(order_main==null){
                    log.error("订单不存在："+out_trade_no);
                    return "fail";
                }
                //3.校验支付金额
//
//                if(total_amount){
//
//                }
                //4.校验订单状态
                if(OrderPayStatusEnum.NO.getKey()!=order_main.getPayStatus()){
                    log.error("当前订单状态非待支付状态："+out_trade_no);
                    return "fail";
                }

                //5.更新订单数据
                order_main.setPayType(OrderPayTypeEnum.ALIPAY.getKey());
                order_main.setPayStatus(OrderPayStatusEnum.PAYALL.getKey());
                order_main.setOrderStatus(OrderStatusEnum.WAITVERIFY.getKey());
                order_main.setGetStatus(OrderGetStatusEnum.NONE.getKey());
                order_main.setPayAt((int)WXPayUtil.getCurrentTimestamp());
                if(OrderTypeEnum.video_order_type.getKey().equals(order_main.getOrderType()) ||
                        OrderTypeEnum.monthly_order_type.getKey().equals(order_main.getOrderType()) ){
                    order_main.setGetStatus(OrderGetStatusEnum.ALL.getKey());
                    order_main.setFeedStatus(0);
                    order_main.setOrderStatus(OrderStatusEnum.FINISH.getKey());
                    order_main.setDeliveryStatus(OrderDeliveryStatusEnum.ALL.getKey());
                }
                orderMainService.update(order_main);
                try {
                    double orderPayMoney = order_main.getPayMoney();
                    double payMoney = CalculateUtils.div(orderPayMoney, 100, 2);
                    memberIntegralService.addMemberIntegral(order_main.getAccountId(), "1", "2", payMoney);
                }catch (Exception e){
                    log.error("回调给会员添加积分异常",e);
                }
                try {
                    Cnd proCnd = Cnd.NEW();
                    proCnd.and("orderId","=",order_main.getId());
                    List<Order_goods> goods = orderGoodsService.query(proCnd);
                    for (Order_goods good: goods
                         ) {
                        Goods_product gp = goodsProductService.fetch(good.getProductId());
                        if(gp!=null) {
                            Integer num = gp.getSaleNumMonth();
                            if (num == null) {
                                num = 0;
                            }
                            num = num + good.getBuyNum();
                            gp.setSaleNumMonth(num);
                            goodsProductService.update(gp);
                        }
                    }

                }catch (Exception e){
                    log.error("更新商品销量异常",e);
                }

                //扣除积分
//                try{
//                    minusPoints(order_main);
//                }catch (Exception e){
//                    log.error("下单扣除积分时异常",e);
//                }

                return "success";	//请不要修改或删除
            }else{//验证失败
                log.error("支付宝回调验签失败:"+out_trade_no);
                return "fail";
            }
        } catch (Exception e) {
            log.error("回调时发生异常",e);
            return "fail";
        }
    }


    /***
     * 微信H5支付回调通知地址
     */
    @RequestMapping("/wxNotify.html")
    @SJson
    public String wxNotify(HttpServletRequest request){
        try {
            //查询订单信息
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            String resultxml = new String(outSteam.toByteArray(), "utf-8");

            log.info("微信支付回调请求参数："+resultxml);
            JSONObject jsonObject = WXPayUtil.xml2Json(resultxml);
            outSteam.close();
            inStream.close();

            String result_code = jsonObject.getString("result_code");
            String out_trade_no = jsonObject.getString("out_trade_no");

            String success = "<xml>\n" +
                            "\n" +
                            "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                            "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                            "</xml>";
            String fail = "<xml>\n" +
                            "\n" +
                            "  <return_code><![CDATA[FAIL]]></return_code>\n" +
                            "  <return_msg><![CDATA[NO]]></return_msg>\n" +
                            "</xml>";

            if("SUCCESS".equals(result_code)){
                //2.查询订单明细
                Order_main order_main = orderMainService.fetch(out_trade_no);
                if(order_main==null){
                    log.error("订单不存在："+out_trade_no);
                    return fail;
                }
                //4.校验订单状态
                if(OrderPayStatusEnum.NO.getKey()!=order_main.getPayStatus()){
                    log.error("当前订单状态非待支付状态："+out_trade_no);
                    return fail;
                }
                //5.更新订单数据
                order_main.setPayType(OrderPayTypeEnum.WEIXINPAY.getKey());
                order_main.setPayStatus(OrderPayStatusEnum.PAYALL.getKey());
                order_main.setOrderStatus(OrderStatusEnum.WAITVERIFY.getKey());
                order_main.setGetStatus(OrderGetStatusEnum.NONE.getKey());
                order_main.setPayAt((int)WXPayUtil.getCurrentTimestamp());
                if(OrderTypeEnum.video_order_type.getKey().equals(order_main.getOrderType()) ||
                        OrderTypeEnum.monthly_order_type.getKey().equals(order_main.getOrderType()) ){
                    order_main.setGetStatus(OrderGetStatusEnum.ALL.getKey());
                    order_main.setFeedStatus(0);
                    order_main.setOrderStatus(OrderStatusEnum.FINISH.getKey());
                    order_main.setDeliveryStatus(OrderDeliveryStatusEnum.ALL.getKey());
                }
                orderMainService.update(order_main);
                try {
                    double orderPayMoney = order_main.getPayMoney();
                    double payMoney = CalculateUtils.div(orderPayMoney, 100, 2);
                    memberIntegralService.addMemberIntegral(order_main.getAccountId(), "1", "2", payMoney);
                }catch (Exception e){
                    log.error("回调给会员添加积分异常",e);
                }

                try {
                    Cnd proCnd = Cnd.NEW();
                    proCnd.and("orderId","=",order_main.getId());
                    List<Order_goods> goods = orderGoodsService.query(proCnd);
                    for (Order_goods good: goods
                    ) {
                        Goods_product gp = goodsProductService.fetch(good.getProductId());
                        if(gp!=null) {
                            Integer num = gp.getSaleNumMonth();
                            if (num == null) {
                                num = 0;
                            }
                            num = num + good.getBuyNum();
                            gp.setSaleNumMonth(num);
                            goodsProductService.update(gp);
                        }
                    }

                }catch (Exception e){
                    log.error("更新商品销量异常",e);
                }

                //扣除积分
//                try{
//                    minusPoints(order_main);
//                }catch (Exception e){
//                    log.error("下单扣除积分时异常",e);
//                }

                return success;
            }else {
                return fail;
            }

        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail").toString();
        }
    }

    //        支付完后回调扣除积分
    private void minusPoints(Order_main order_main){
        int im=0;
        if(order_main.getMinusPoints()!=null){
            im = order_main.getMinusPoints();
        }
        if(im>0){
            Cnd cnd3 = Cnd.NEW();
            cnd3.and("delFlag", "=", false);
            cnd3.and("customerUuid","=",order_main.getAccountId());
            List<Member_Integral> list=memberIntegralService.query(cnd3);
            if(list!=null&&list.size()>0){
                Member_Integral memIntegral = list.get(0);
                memIntegral.setUseAbleIntegral(memIntegral.getUseAbleIntegral()-im);
                memberIntegralService.update(memIntegral);
                Member_Integral_Detail mid = new Member_Integral_Detail();
                mid.setIntegralDesc("购物减积分");
                mid.setIntegralType(4);
                mid.setCustomerUuid(order_main.getAccountId());
                mid.setAddIntegral(im);
                memberIntegralDetailService.insert(mid);
            }
            log.info("回调积分扣除记录添加成功");
        }
    }


    /***
     * 查询微信支付请求订单状态
     */
    @RequestMapping("/queryWeiXinOrderStatus.html")
    @SJson
    public Result queryWeiXinOrderStatus(String orderId){
        try {
            Order_main order_main = orderMainService.fetch(orderId);
            if(order_main.getPayStatus()==OrderPayStatusEnum.PAYALL.getKey()){
                return Result.success("ok");
            }
            String status = wxPayService.wxQueryOrderStatus(orderId);
            if("SUCCESS".equals(status)){
                order_main.setPayType(OrderPayTypeEnum.WEIXINPAY.getKey());
                order_main.setPayStatus(OrderPayStatusEnum.PAYALL.getKey());
                order_main.setOrderStatus(OrderStatusEnum.WAITVERIFY.getKey());
                order_main.setGetStatus(OrderGetStatusEnum.NONE.getKey());
                order_main.setPayAt((int)WXPayUtil.getCurrentTimestamp());
                if(OrderTypeEnum.video_order_type.getKey().equals(order_main.getOrderType()) ||
                        OrderTypeEnum.monthly_order_type.getKey().equals(order_main.getOrderType()) ){
                    order_main.setGetStatus(OrderGetStatusEnum.ALL.getKey());
                    order_main.setFeedStatus(0);
                    order_main.setOrderStatus(OrderStatusEnum.FINISH.getKey());
                    order_main.setDeliveryStatus(OrderDeliveryStatusEnum.ALL.getKey());
                }
                orderMainService.update(order_main);
                return Result.success("ok",status);
            }
            return Result.error("fail");
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail");
        }
    }



}
