package com.hjlc.system.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class MyExceptionResolver implements HandlerExceptionResolver{

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		// TODO Auto-generated method stub
		System.out.println("==============异常开始=============");
		ex.printStackTrace();
		System.out.println("==============异常结束=============");
		//ModelAndView mv = new ModelAndView("error");
		ModelAndView mv = new ModelAndView("static/errorPages/500.html");
		mv.addObject("exception", ex.toString().replaceAll("\n", "<br/>"));
		return mv;
	}

}
