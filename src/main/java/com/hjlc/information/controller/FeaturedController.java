package com.hjlc.information.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hjlc.base.controller.BaseController;
import com.hjlc.information.service.FeaturedService;
import com.hjlc.system.model.Page;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.PageData;

/**
 * 类名称：FeaturedController 
 * @version
 */
@Controller
@RequestMapping(value = "/featured")
public class FeaturedController extends BaseController {

	@Resource(name = "featuredService")
	private FeaturedService featuredService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list")
	public ModelAndView listUsers(HttpSession session, Page page) throws Exception {
		logBefore(logger, "特别推荐列表");
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		try {
			pd = this.getPageData();
			// 检索条件================================
			String title = pd.getString("title");
			if (null != title && !"".equals(title)) {
				title = title.trim();
				pd.put("title", title);
			}
			// 检索条件================================
			page.setPd(pd);
			List<PageData> varList = featuredService.list(page);
			mv.setViewName(PAGE_PATH_PREFIX + "information/featured/featured_list.jsp");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Constants.SESSION_QX, this.getHC()); // 按钮权限
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	/**
	 * 去新增页面
	 */
	@RequestMapping(value = "/goAdd")
	public ModelAndView goAdd() {
		ModelAndView mv = new ModelAndView();
		try {
			mv.setViewName("information/featured/featured_edit");
			mv.addObject("msg", "save");
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	/**
	 * 去修改页面
	 */
	@RequestMapping(value = "/goEdit")
	public ModelAndView goEdit() {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			pd = featuredService.findById(pd);
			mv.setViewName("information/featured/featured_edit");
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
	@RequestMapping(value = "/edit")
	public ModelAndView edit(PrintWriter out) throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();

		String sequence = pd.getString("sequence");

		pd.put("uptime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")); // 修改时间
		pd.put("sequence", "".equals(sequence) ? 0 : sequence);

		featuredService.edit(pd);

		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save(PrintWriter out) throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String sequence = pd.getString("sequence");
		pd.put("addtime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")); // 新增时间
		pd.put("uptime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")); // 修改时间
		pd.put("sequence", "".equals(sequence) ? 0 : sequence);
		featuredService.save(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete")
	public void delete(PrintWriter out) throws Exception {
		PageData pd = this.getPageData();
		try {
			featuredService.delete(pd);
			out.write("success");
			out.close();
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}

	}

	/* ===============================权限================================== */
	public Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Constants.SESSION_QX);
	}
	/* ===============================权限================================== */
}
