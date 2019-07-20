package com.aebiz.app.web.commons.quartz.job;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.models.Member_Integral_Detail;
import com.aebiz.app.integral.modules.services.MemberIntegralDetailService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.member.modules.models.Member_coupon;
import com.aebiz.app.member.modules.services.MemberCouponService;
import com.aebiz.app.order.modules.models.Order_main;
import com.aebiz.app.order.modules.models.em.OrderPayStatusEnum;
import com.aebiz.app.order.modules.models.em.OrderTypeEnum;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.sys.modules.services.SysTaskService;
import com.aebiz.commons.utils.DateUtil;
import com.aebiz.commons.utils.StringUtil;
import org.apache.logging.log4j.util.Strings;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Auther: zenghaorong
 * @Date: 2019/6/10  20:32
 * @Description: 订单超时关闭
 */
@Component("orderTimeoutJob")
public class OrderTimeoutJob{

    private static final Log log = Logs.get();

    @Autowired
    private SysTaskService sysTaskService;

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private MemberIntegralDetailService memberIntegralDetailService;

    @Autowired
    private MemberCouponService memberCouponService;

    @Autowired
    private MemberIntegralService memberIntegralService;



    public void orderTimeout()  {
        try{
            log.debug("订单超时关闭定时任务开始执行---------------");
            //处理超时未支付的订单
            Cnd cndM = Cnd.NEW();
            cndM.and("payStatus", "=",0);
            cndM.and("delFlag", "=", 0);
            List<Order_main> orderMainList = orderMainService.query(cndM);
            if(orderMainList != null){
                for(Order_main order_main:orderMainList){
                    //判断订单是否超时两小时
                    Integer orderTime =  order_main.getOrderAt();
                    Integer nowTime = DateUtil.getTime(new Date());
                    if((nowTime - orderTime) > 60*60*2){
                        try{
                            order_main.setDelFlag(true);
                            orderMainService.update(order_main);
                            //更新优惠劵状态
                            try{
                                //待支付状态
                                if(order_main.getPayStatus()==0){
                                    if(Strings.isNotEmpty(order_main.getMemberCouponId())){
                                        Member_coupon member_coupon = memberCouponService.fetch(order_main.getMemberCouponId());
                                        member_coupon.setStatus(0);
                                        memberCouponService.update(member_coupon);
                                    }
                                }
                            }catch (Exception e){
                                log.error("定时任务删除订单更新优惠劵状态异常"+order_main.getId(),e);
                            }

                            //判断如果使用积分了则积分退回
                            try{
                                //待支付状态
                                if(order_main.getPayStatus()==0){
                                    //判断是否使用了积分
                                    if(order_main.getMinusPoints()!=null){
                                        Cnd iCnd = Cnd.NEW();
                                        iCnd.and("customerUuid" ,"=",order_main.getAccountId());
                                        List<Member_Integral> list2 = memberIntegralService.query(iCnd);
                                        Member_Integral m = list2.get(0);
                                        m.setCustomerUuid(order_main.getAccountId());
                                        m.setUseAbleIntegral(m.getUseAbleIntegral()+order_main.getMinusPoints());
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
                                log.error("定时任务如果使用积分了则积分退回异常"+order_main.getId(),e);
                            }
                        }catch (Exception e){
                            log.error("订单："+order_main.getId()+"定时关闭订单执行失败",e);
                        }
                        StringBuilder note = new StringBuilder("订单号："+order_main.getId() +" 支付时间已经过期了");
                        log.debug(note);
                    }
                }
            }


        }catch (Exception e){
            sysTaskService.update(Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行失败").add("nextAt", DateUtil.getTime()));
        }finally {
            log.debug("订单超时关闭定时任务执行结束---------------");
        }

    }


}
