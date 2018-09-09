package com.hjlc.information.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hjlc.base.controller.BaseController;
import com.hjlc.information.service.NewsService;
import com.hjlc.system.model.Page;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.FileUtil;
import com.hjlc.util.utils.Freemarker;
import com.hjlc.util.utils.PageData;
import com.hjlc.util.utils.PathUtil;

/** 
 * 类名称：NewsController
 */
@Controller
@RequestMapping(value="/news")
public class NewsController extends BaseController{
	@Resource(name="newsService")
	private NewsService newsService;
	
	/**
	 * 列表
	 */
	@RequestMapping(value="/list")
	public ModelAndView listUsers(HttpSession session, Page page) throws Exception{
		logBefore(logger, "新闻列表");
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		try{
			pd = this.getPageData();
			
			//检索条件================================
			String title = pd.getString("title");
			String publisher = pd.getString("publisher");
			if(null != title && !"".equals(title)){
				title = title.trim();
				pd.put("title", title);
			}
			if(null != publisher && !"".equals(publisher)){
				publisher = publisher.trim();
				pd.put("publisher", publisher);
			}
			//检索条件================================
			page.setPd(pd);
			List<PageData>	varList = newsService.list(page);
			mv.setViewName(PAGE_PATH_PREFIX + "information/news/news_list.jsp");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		
		return mv;
	}
	
	
	/**
	 * 去新增页面
	 */
	@RequestMapping(value="/goAdd")
	public ModelAndView goAdd(){
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			mv.setViewName(PAGE_PATH_PREFIX + "information/news/news_edit.jsp");
			mv.addObject("msg", "save");
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}						
		return mv;
	}
	
	/**
	 * 去修改页面
	 */
	@RequestMapping(value="/goEdit")
	public ModelAndView goEdit(){
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			
			pd = newsService.findById(pd);
			
			mv.setViewName(PAGE_PATH_PREFIX + "information/news/news_edit.jsp");
			mv.addObject("msg", "edit");
			mv.addObject("pd", pd);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}						
		return mv;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(PrintWriter out) throws Exception{
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		//获取IP
		HttpServletRequest request = this.getRequest();
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {  
			ip = request.getRemoteAddr();  
	    }else{
	    	ip = request.getHeader("x-forwarded-for");  
	    }
		
		String sequence = pd.getString("sequence");
		
		pd.put("pip", ip);	
		pd.put("uptime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));				//修改时间
		pd.put("sequence", "".equals(sequence)?0:sequence);
		
		newsService.edit(pd);
		
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**
	 * 保存
	 */
	@RequestMapping(value="/save")
	public ModelAndView save(PrintWriter out) throws Exception{
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		//获取IP
		HttpServletRequest request = this.getRequest();
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {  
			ip = request.getRemoteAddr();  
	    }else{
	    	ip = request.getHeader("x-forwarded-for");  
	    }
		
		String sequence = pd.getString("sequence");
		
		pd.put("pip", ip);	
		pd.put("addtime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));				//新增时间
		pd.put("uptime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));				//修改时间
		pd.put("hits", 0);									//点击数
		pd.put("sequence", "".equals(sequence)?0:sequence);
		
		newsService.save(pd);
		
		
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping(value="/delete")
	public void delete(PrintWriter out)throws Exception{
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		try{
			pd = this.getPageData();
			newsService.delete(pd);
			out.write("success");
			out.close();
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		
	}
	
	/**
	 * 生成html
	 */
	@RequestMapping(value="/createHtml")
	public void createHtml(PrintWriter out)throws Exception{
		//ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		try{
			pd = this.getPageData();
			FileUtil.delFolder(PathUtil.getClasspath()+"html/news"); //生成代码前,先清空之前生成的代码
			Map<String,Object> root = new HashMap<String,Object>();		//创建数据模型
			List<PageData>	varList = newsService.newslist(pd);
			root.put("varList", varList);
			String filePath = "html/news/";								//存放路径
			String ftlPath = "news";									//ftl路径
			/*生成*/
			Freemarker.printFile("index.ftl", root, "index.html", filePath, ftlPath);
			out.write("success");
			out.close();
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		
	}
	
	
	/* ===============================权限================================== */
	public Map<String, String> getHC(){
		Subject currentUser = SecurityUtils.getSubject();  //shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>)session.getAttribute(Constants.SESSION_QX);
	}
	/* ===============================权限================================== */
}