package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.cms.modules.models.Cms_article;
import com.aebiz.app.member.modules.models.Member_address;
import com.aebiz.app.member.modules.services.MemberAddressService;
import com.aebiz.app.shop.modules.models.Shop_area;
import com.aebiz.app.shop.modules.services.ShopAreaService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @Auther: zenghaorong
 * @Date: 2019/3/31  23:19
 * @Description: 收货地址接口
 */
@Controller
@RequestMapping("/open/h5/address")
public class AddressH5Controller{

    private static final Log log = Logs.get();

    @Autowired
    private MemberAddressService memberAddressService;

    @Autowired
    private ShopAreaService shopAreaService;

    /**
     * 进入收货地址列表页
     */
    @RequestMapping("goAddress.html")
    public String goAddress(String productList,HttpServletRequest request){
        request.setAttribute("productList",productList);
        return "pages/front/h5/niantu/address";
    }

    /**
     * 进入收货地址编辑页
     */
    @RequestMapping("goAddressUp.html")
    public String goAddressUp(String id,String productList, HttpServletRequest request){
        request.setAttribute("id",id);
        request.setAttribute("productList",productList);
        return "pages/front/h5/niantu/addressUp";
    }


    /**
     * 进入收货地址添加页
     */
    @RequestMapping("goAddressAdd.html")
    public String goAddressAdd(String productList,HttpServletRequest request){
        request.setAttribute("productList",productList);
        return "pages/front/h5/niantu/addressAdd";
    }


    /**
     * 查询收货地址列表
     */
    @RequestMapping("addressList.html")
    @SJson
    public Result addressList(){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            Cnd cnd = Cnd.NEW();
            cnd.and("accountId", "=", accountUser.getAccountId());
            List<Member_address> list = memberAddressService.query(cnd);
            return Result.success("ok",list);
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }
    @RequestMapping("getProvinces.html")
    @SJson
    public Result getProvinces(){
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("parentId", "=", "aj1da2wcuqzsa2g51bc7192fd60china");
            List<Shop_area> list = shopAreaService.query(cnd);
            return Result.success("ok",list);
        } catch (Exception e) {
            log.error("查询省列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 获取市
     * @param request
     * @return
     */
    @RequestMapping("getCitys.html")
    @SJson
    public Result getCitys(HttpServletRequest request){
        try {
            String pid = (String)request.getParameter("provinceId");
            Cnd cnd = Cnd.NEW();
            cnd.and("parentId", "=", pid);
            List<Shop_area> list = shopAreaService.query(cnd);
            return Result.success("ok",list);
        } catch (Exception e) {
            log.error("查询省列表异常",e);
            return Result.error("fail");
        }
    }
    /**
     * 查询默认收货地址
     */
    @RequestMapping("defaultAddress.html")
    @SJson
    public Result defaultAddress(){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();

            Cnd cnd1 = Cnd.NEW();
            cnd1.and("accountId", "=", accountUser.getAccountId());
            cnd1.and("defaultValue", "=", 1);
            Member_address memberAddress = memberAddressService.fetch(cnd1);
            if(memberAddress != null){
                return Result.success("ok",memberAddress);
            }else {
                Cnd cnd = Cnd.NEW();
                cnd.and("accountId", "=", accountUser.getAccountId() );
                List<Member_address> list = memberAddressService.query(cnd);
                Member_address member_address = null;
                if(list!=null && list.size()>0){
                    member_address = list.get(0);
                }
                return Result.success("ok",member_address);
            }
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 设置当前收货地址为默认收货地址
     */
    @RequestMapping("setDefaultAddress.html")
    @SJson
    public Result setDefaultAddress(String id){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            memberAddressService.updateDefault(id,accountUser.getAccountId());
            return Result.success("ok");
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 添加收货地址
     */
    @RequestMapping("addAddress.html")
    @SJson
    public Result addAddress(String name,String mobile,String address,String province,String city,String area){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            Member_address member_address =new Member_address();
            member_address.setAccountId(accountUser.getAccountId());
            member_address.setAddress(address);
            member_address.setFullName(name);
            member_address.setMobile(mobile);
            member_address.setProvince(province);
            member_address.setCity(city);
            member_address.setCounty(area);
            memberAddressService.insert(member_address);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 编辑收货地址
     */
    @RequestMapping("updateAddress.html")
    @SJson
    public Result updateAddress(String id,String name,String mobile,String address,String province,String city,String area){
        try {
            Member_address member_address =new Member_address();
            member_address.setId(id);
            member_address.setAddress(address);
            member_address.setFullName(name);
            member_address.setMobile(mobile);
            member_address.setProvince(province);
            member_address.setCity(city);
            member_address.setCounty(area);
            memberAddressService.updateIgnoreNull(member_address);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 根据id查询收货地址
     */
    @RequestMapping("addressbyId.html")
    @SJson
    public Result addressbyId(String id){
        try {

            Member_address member_address = memberAddressService.fetch(id);
            return Result.success("ok",member_address);
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 根据id删除收货地址
     */
    @RequestMapping("addressDelete.html")
    @SJson
    public Result addressDelete(String id){
        try {
            memberAddressService.delete(id);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }


}
