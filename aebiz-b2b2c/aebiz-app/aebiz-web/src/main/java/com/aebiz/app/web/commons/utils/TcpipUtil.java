package com.aebiz.app.web.commons.utils;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.search.suggest.completion.RegexOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

/**
 * tcp/ip 工具类
* Description: 
* @ClassName: TcpipUtil 
* @author Jalf
* @since 2016年5月31日 上午9:20:28 
* Copyright  foxtail All right reserved.
 */
public class TcpipUtil {
    protected static final Logger logger = LoggerFactory.getLogger(TcpipUtil.class);
	/**
	* Description:获得ip地址
	* @Title: getIpAddr  
	* @author Jalf
	* @since 2016年5月31日 上午9:20:45
	* @param request
	* @return
	* Copyright  foxtail All right reserved.
	 */
	public static String getClientRealIp(HttpServletRequest request){
		Enumeration<String> enumeration = request.getHeaderNames();
		String ip = request.getHeader("X-Forwarded-For");
		logger.info("支付调用请求ip-X-Forwarded-For："+ip);
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			logger.info("支付调用请求ip-Proxy-Client-IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			logger.info("支付调用请求ip-WL-Proxy-Client-IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			logger.info("支付调用请求ip-HTTP_CLIENT_IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			logger.info("支付调用请求ip-HTTP_X_FORWARDED_FOR："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
			logger.info("支付调用请求ip-X-Real-IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			logger.info("支付调用请求ip-request.getRemoteAddr()："+ip);
		}
		if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
			//多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				ip = ip.substring(0, index);
			}
		}
		logger.info("支付调用结果ip ："+ip);
		if("127.0.0.1".equals(ip)){
			ip = "0:0:0:0:0:0:0:1";
		}
		logger.info("支付调用结果ip2 ："+ip);
		return ip;
	}


	/**
	 * 获取本机IP
	 *
	 * @param request
	 * @return
	 */
	public static String getLocalIpv4(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getHeaderNames();
		StringBuffer stringBuffer = new StringBuffer();
		while (enumeration.hasMoreElements()) {
			String headName = enumeration.nextElement();
			stringBuffer.append(headName);
			stringBuffer.append(":");
			stringBuffer.append(request.getHeader(headName));
			stringBuffer.append(";");
		}

		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		// 获取 真实的IP地址
		if (ip != null && ip.contains(",")) {
			String[] ips = ip.split(",");
			int len = ips.length;
			if (len > 1) {
				String ip1 = ips[0];
				String ip2 = ips[len - 1];
				if (ip1.startsWith("10.") || ip1.startsWith("172.16.") || ip1.startsWith("192.168.")) {
					ip = ip2;
				} else {
					ip = ip1;
				}
			} else if (len == 1) {
				ip = ips[0];
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null && (ip.startsWith("10.") || ip.startsWith("172.16.") || ip.startsWith("192.168."))) {
			ip = request.getRemoteAddr();
		}
		return ip.replaceAll(" ","");
	}

}
