package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_info;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.cms.modules.models.Cms_link;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.shop.modules.models.Shop_adv_main;
import com.aebiz.app.shop.modules.services.ShopAdvMainService;
import com.aebiz.app.wx.modules.services.WxConfigService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.nutz.dao.Cnd;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/23  20:55
 * @Description:
 */
@Controller
@RequestMapping("/open/h5/niantu")
public class NianTuIndexController {


    private static final Log log = Logs.get();

    @Autowired
    private ShopAdvMainService shopAdvMainService;

    @Autowired
    private PropertiesProxy config;

    @Autowired
    private WxConfigService wxConfigService;

    @Autowired
    private AccountInfoService accountInfoService;

    /**
     * 进入首页
     * @return
     */
    @RequestMapping("/index.html")
    public String index(String code,HttpServletRequest request) {
        String appId = config.get("wx.pay.AppID");
        request.setAttribute("appId",appId);
        request.setAttribute("code",code);
        if(!Strings.isEmpty(code)) {
            String wxJson = wxConfigService.getWxApiAccessToken(false,code);
            JSONObject jsonObject = JSON.parseObject(wxJson);
            String access_token = jsonObject.getString("access_token");
//            if(access_token==null){
//                wxConfigService.getWxApiAccessToken(true,code);
//            }
            log.info("获取到token-openId接口返回："+access_token);
        }
        return "pages/front/h5/niantu/index";
    }


    /**
     * 进入个人中心页
     */
    @RequestMapping("userCenter.html")
    public String userCenter(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();

        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        Account_info info = accountInfoService.fetch(accountUser.getAccountId());
        request.setAttribute("info",info);
        request.setAttribute("accountUser",accountUser);
        return "pages/front/h5/niantu/userCenter";
    }

    /**
     * 进入创意美术
     */
    @RequestMapping("creativeArt.html")
    public String creativeArt() {
        return "pages/front/h5/niantu/creativeArt";
    }

    /**
     * 进入达人秀场
     */
    @RequestMapping("talentShow.html")
    public String talentShow(String accountId,HttpServletRequest request) {
        if(Strings.isEmpty(accountId)){
            request.setAttribute("accountId","");
        }else{
            request.setAttribute("accountId",accountId);
        }


        return "pages/front/h5/niantu/talentShow";
    }

    /**
     * 进入黏土教程
     */
    @RequestMapping("tutorial.html")
    public String tutorial() {
        return "pages/front/h5/niantu/tutorial";
    }

    /**
     * 进入黏土教程详情页
     */
    @RequestMapping("tutorialDetails.html")
    public String tutorialDetails(String id, HttpServletRequest req) {
        req.setAttribute("id",id);
        return "pages/front/h5/niantu/tutorialDetails";
    }

    /**
     * 获取首页轮播图
     */
    @RequestMapping("getAdv.html")
    @SJson
    public Result getAdv() {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", 0 );
            cnd.desc("location");
            List<Shop_adv_main> list=shopAdvMainService.query(cnd);
            return Result.success("ok",list);
        } catch (Exception e) {
            log.error("获取视频列表异常",e);
            return Result.error("fail");
        }
    }

}
