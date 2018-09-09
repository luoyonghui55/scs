package com.hjlc.system.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hjlc.util.utils.Constants;

public class WebAppContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
	}

	public void contextInitialized(ServletContextEvent event) {
		Constants.WEB_APP_CONTEXT = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		System.out.println("========获取Spring WebApplicationContext");
	}
}