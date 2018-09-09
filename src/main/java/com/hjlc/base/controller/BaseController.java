package com.hjlc.base.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.hjlc.system.model.Page;
import com.hjlc.system.model.User;
import com.hjlc.userIndex.model.AppUser;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.Logger;
import com.hjlc.util.utils.PageData;

public class BaseController {
	protected Logger logger = Logger.getLogger(this.getClass());
	public final String PAGE_PATH_PREFIX = "/WEB-INF/jsp/";
	/**
	 * 得到PageData
	 */
	public PageData getPageData(){
		return new PageData(this.getRequest());
	}
	
	/**
	 * 得到ModelAndView
	 */
	public ModelAndView getModelAndView(){
		return new ModelAndView();
	}
	
	/**
	 * 得到request对象
	 */
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	/**
	 * 得到分页列表的信息 
	 */
	public Page getPage(){
		return new Page();
	}
	
	public static void logBefore(Logger logger, String interfaceName){
		logger.info("start");
		logger.info(interfaceName);
	}
	
	public static void logAfter(Logger logger){
		logger.info("end");
	}
	/**
	 * 获取session中的用户
	 * @return
	 */
	public User getSessionUser() {
		Subject currentUser = SecurityUtils.getSubject();  
		Object object = currentUser.getSession().getAttribute(Constants.SESSION_USER);
		if (object != null) {
			return (User) object;
		}
		return null;
	}
	/**
	 * 获取session中的APP用户
	 * @return
	 */
	public AppUser getSessionAppUser() {
		Subject currentUser = SecurityUtils.getSubject();  
		Object object = currentUser.getSession().getAttribute(Constants.SESSION_APP_USER);
		if (object != null) {
			return (AppUser) object;
		}
		return null;
	}
}