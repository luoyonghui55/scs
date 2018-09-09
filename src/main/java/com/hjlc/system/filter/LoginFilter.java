package com.hjlc.system.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.util.StringUtils;

import com.hjlc.base.controller.BaseController;
import com.hjlc.util.utils.Constants;

/**
 * 登录验证过滤器
 */
public class LoginFilter extends BaseController implements Filter {

	/**
	 * 初始化
	 */
	@Override
	public void init(FilterConfig fc) throws ServletException {
		//FileUtil.createDir("d:/LUO/topic/");
	}
	@Override
	public void destroy() {

	}
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getServletPath();
        //request.setAttribute("basePath", basePath);
        //判断session里是否有用户信息
        if (!"/scs".equals(path) && !path.matches(Constants.NO_INTERCEPTOR_PATH) && !SecurityUtils.getSubject().isAuthenticated()) {
        	String xWith = request.getHeader("x-requested-with");
        	//如果是ajax请求响应头会有，x-requested-with
            if (StringUtils.hasText(xWith) && "XMLHttpRequest".equalsIgnoreCase(xWith)) {
            	//System.err.println("-------\nurl="+request.getRequestURL());
                response.setHeader("session-status", "timeout");//在响应头设置session状态
                return;
            }else {
				//返回500错误页面
            	response.sendRedirect("/static/errorPages/500.html");
			}
        }

		chain.doFilter(req, res); // 调用下一过滤器
	}
}