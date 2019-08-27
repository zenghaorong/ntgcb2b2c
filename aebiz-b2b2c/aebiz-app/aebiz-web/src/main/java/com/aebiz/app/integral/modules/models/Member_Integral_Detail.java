package com.aebiz.app.integral.modules.models;

import com.aebiz.baseframework.base.model.BaseModel;
import com.aebiz.commons.utils.DateUtil;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * 用户积分明细表
 * @author chengzhuming
 * @date 2019/4/16 9:13
 */
@Table("Member_Integral_Detail")
public class Member_Integral_Detail extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("会员UUID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String customerUuid;


    @Column
    @Comment("增加积分")
    @ColDefine(type = ColType.INT, width = 11)
    private int addIntegral;


    @Column
    @Comment("积分类型")
    @ColDefine(type = ColType.INT, width = 11)
    private int integralType;//1.购物积分 2. 注册积分 3. 评论积分 5.未支付订单积分退回 //6.支付订单退款退积分

    @Column
    @Comment("描述")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String integralDesc;

    private String integralTime;

    public String getIntegralTime() {
        integralTime = DateUtil.getDate(this.getOpAt());
        return integralTime;
    }

    public void setIntegralTime(String integralTime) {
        this.integralTime = integralTime;
    }

    public String getIntegralDesc() {
        return integralDesc;
    }

    public void setIntegralDesc(String integralDesc) {
        this.integralDesc = integralDesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public void setCustomerUuid(String customerUuid) {
        this.customerUuid = customerUuid;
    }

    public int getAddIntegral() {
        return addIntegral;
    }

    public void setAddIntegral(int addIntegral) {
        this.addIntegral = addIntegral;
    }

    public int getIntegralType() {
        return integralType;
    }

    public void setIntegralType(int integralType) {
        this.integralType = integralType;
    }
}
