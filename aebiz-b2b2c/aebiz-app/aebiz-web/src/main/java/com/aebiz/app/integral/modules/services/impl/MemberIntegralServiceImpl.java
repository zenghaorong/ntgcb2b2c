package com.aebiz.app.integral.modules.services.impl;

import com.aebiz.app.integral.modules.models.Integral_Rule;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.models.Member_Integral_Detail;
import com.aebiz.app.integral.modules.services.IntegralRuleService;
import com.aebiz.app.integral.modules.services.MemberIntegralDetailService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.baseframework.base.service.BaseServiceImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MemberIntegralServiceImpl extends BaseServiceImpl<Member_Integral> implements MemberIntegralService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }
    @Autowired
    private MemberIntegralDetailService memberIntegralDetailService;
    @Autowired
    private IntegralRuleService integralRuleService;
    @Override
    public void addMemberIntegral(String userId, String integralRuleType, String times, Double payMoney) {
       //购物送积分
        Cnd ruleCnd = Cnd.NEW();
        if(integralRuleType=="1") {
            ruleCnd.and("ruleCode", "=", "buyIntegral");
        }
        if(integralRuleType=="2") {
            ruleCnd.and("ruleCode", "=", "registerIntegral");
        }
        if(integralRuleType=="3") {
            ruleCnd.and("ruleCode", "=", "commentIntegral");
        }
        List<Integral_Rule> ruleList = integralRuleService.query(ruleCnd);
        Integral_Rule integral_rule = new Integral_Rule();
        if(ruleList!=null&&ruleList.size()>0) {
            integral_rule = ruleList.get(0);
        }
        if(integralRuleType=="1"){
            Cnd iCnd = Cnd.NEW();
            iCnd.and("customerUuid" ,"=",userId);
            List<Member_Integral> list2 = this.query(iCnd);
            if(list2!=null&&list2.size()>0){
                Member_Integral mi = list2.get(0);
                payMoney=payMoney*100;
                mi.setUseAbleIntegral(mi.getUseAbleIntegral()+integral_rule.getIntegralCount()*payMoney.intValue());
                mi.setTotalIntegral(mi.getTotalIntegral()+integral_rule.getIntegralCount()*payMoney.intValue());
                this.update(mi);
                Member_Integral_Detail md = new Member_Integral_Detail();
                md.setAddIntegral(integral_rule.getIntegralCount()*payMoney.intValue());
                md.setCustomerUuid(userId);
                md.setIntegralDesc("购物送积分");
                md.setIntegralType(1);
                memberIntegralDetailService.insert(md);
            }else {
                    Member_Integral m = new Member_Integral();
                    m.setCustomerUuid(userId);
                    payMoney=payMoney*100;
                    m.setTotalIntegral(integral_rule.getIntegralCount()*payMoney.intValue());
                    m.setUseAbleIntegral(integral_rule.getIntegralCount()*payMoney.intValue());
                    this.insert(m);
                    Member_Integral_Detail md = new Member_Integral_Detail();
                    md.setAddIntegral(integral_rule.getIntegralCount()*payMoney.intValue());
                    md.setCustomerUuid(userId);
                    md.setIntegralDesc("购物送积分");
                    md.setIntegralType(1);
                    memberIntegralDetailService.insert(md);
            }
        }
        //注册送积分
        if(integralRuleType=="2"){
            Cnd cnd = Cnd.NEW();
            cnd.and("customerUuid" ,"=",userId);
            cnd.and("integralType" ,"=",2);
            List<Member_Integral_Detail> list = memberIntegralDetailService.query(cnd);
            if(list==null || list.size()==0){
                Member_Integral m = new Member_Integral();

                    m.setCustomerUuid(userId);
                    m.setTotalIntegral(integral_rule.getIntegralCount());
                    m.setUseAbleIntegral(integral_rule.getIntegralCount());
                    this.insert(m);
                    Member_Integral_Detail md = new Member_Integral_Detail();
                    md.setAddIntegral(integral_rule.getIntegralCount());
                    md.setCustomerUuid(userId);
                    md.setIntegralDesc("注册送积分");
                    md.setIntegralType(2);
                    memberIntegralDetailService.insert(md);

            }
        }
        if(integralRuleType=="3"){
            Cnd iCnd = Cnd.NEW();
            iCnd.and("customerUuid" ,"=",userId);
            List<Member_Integral> list2 = this.query(iCnd);
            if(list2!=null&&list2.size()>0){
                Member_Integral mi = list2.get(0);
                mi.setUseAbleIntegral(mi.getUseAbleIntegral()+integral_rule.getIntegralCount());
                mi.setTotalIntegral(mi.getTotalIntegral()+integral_rule.getIntegralCount());
                this.update(mi);
                Member_Integral_Detail md = new Member_Integral_Detail();
                md.setAddIntegral(integral_rule.getIntegralCount());
                md.setCustomerUuid(userId);
                md.setIntegralDesc("评论送积分");
                md.setIntegralType(3);
                memberIntegralDetailService.insert(md);
            }else {
                Member_Integral m = new Member_Integral();
                m.setCustomerUuid(userId);
                m.setTotalIntegral(integral_rule.getIntegralCount());
                m.setUseAbleIntegral(integral_rule.getIntegralCount());
                this.insert(m);
                Member_Integral_Detail md = new Member_Integral_Detail();
                md.setAddIntegral(integral_rule.getIntegralCount());
                md.setCustomerUuid(userId);
                md.setIntegralDesc("评论送积分");
                md.setIntegralType(3);
                memberIntegralDetailService.insert(md);
            }
        }
    }
}
