package com.hjlc.util.utils;

import java.io.File;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 路径工具类
 */
public class PathUtil {
	public static final String FTL_PATH = "G:/scsTemplateAndHtml/ftl/";
	public static final String HTML_PATH = "G:/scsTemplateAndHtml/html/userIndex/" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/";
	/**
	 * 图片访问路径
	 * 
	 * @param pathType
	 *            图片类型 visit-访问；save-保存
	 * @param pathCategory
	 *            图片类别，如：话题图片-topic、话题回复图片-reply、商家图片
	 * @return
	 */
	public static String getPicturePath(String pathType, String pathCategory) {
		String strResult = "";
		//HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
		//		.getRequestAttributes()).getRequest();
		StringBuffer strBuf = new StringBuffer();
		if ("visit".equals(pathType)) {
		} else if ("save".equals(pathType)) {
			String projectPath = getPorjectPath().replaceAll("\\\\","/");
			projectPath = splitString(projectPath, "bin/");

			strBuf.append(projectPath);
			strBuf.append("webapps/ROOT/");
		}
		strResult = strBuf.toString();
		return strResult;
	}

	private static String splitString(String str, String param) {
		String result = str;
		if (str.contains(param)) {
			int start = str.indexOf(param);
			result = str.substring(0, start);
		}
		return result;
	}
	
	/*
	 * 获取classpath1
	 */
	public static String getClasspath(){
		String path = (String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../").replaceAll("file:/", "").replaceAll("%20", " ").trim();	
		if(path.indexOf(":") != 1){
			path = File.separator + path;
		}
		return path;
	}
	
	/*
	 * 获取classpath2
	 */
	public static String getClassResources(){
		String path =  (String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))).replaceAll("file:/", "").replaceAll("%20", " ").trim();	
		if(path.indexOf(":") != 1){
			path = File.separator + path;
		}
		return path;
	}
	
	public static String PathAddress() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(request.getScheme() + "://");
		strBuf.append(request.getServerName() + ":");
		strBuf.append(request.getServerPort() + "");
		strBuf.append(request.getContextPath() + "/");
		return strBuf.toString();// +"ss/";//加入项目的名称
	}
	/**
	 * 获取项目目录，如：G:\myProjects\scs/
	 * @return
	 */
	public static String getPorjectPath(){
		return System.getProperty("user.dir") + "/";
	}
	/**
	 * 获取客户端真实IP地址
	 * 注：需在nginx作如下配置：
	 * proxy_set_header Host $http_host;  包含客户端真实的域名和端口号
		proxy_set_header X-Real-IP $remote_addr;  表示客户端真实的IP
		//这个Header和X-Real-IP类似，但它在多层代理时会包含真实客户端及中间每个代理服务器的IP。
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_set_header X-Forwarded-Proto $scheme;  表示客户端真实的协议（http还是https）
		
		在tomcat中作如下配置：
		在server.xml中的<Host>节点中添加如下内容：<Valve className="org.apache.catalina.valves.RemoteIpValve" />
	 * @param request
	 * @return
	 */
	public static String getIPAddress(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		if(StringUtils.hasText(ip) && !"unKnown".equalsIgnoreCase(ip)){
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if(StringUtils.hasText(ip) && !"unKnown".equalsIgnoreCase(ip)){
			//多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if(index != -1){
				return ip.substring(0,index);
			}
			return ip;
		}
		return request.getHeader("X-Real-IP");
	}
}
