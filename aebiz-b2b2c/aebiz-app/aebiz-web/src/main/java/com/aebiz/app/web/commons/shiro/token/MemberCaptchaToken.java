package com.aebiz.app.web.commons.shiro.token;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Created by wizzer on 2017/1/11.
 */
public class MemberCaptchaToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 4676958151524148624L;
    private String captcha;
    private boolean quickLogin;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public MemberCaptchaToken(String username, String password, boolean rememberMe, String host, String captcha) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }

    public MemberCaptchaToken(String username, String password, boolean rememberMe, String host, String captcha, boolean quickLogin) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
        this.quickLogin = quickLogin;
    }
}
