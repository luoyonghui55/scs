package com.hjlc.system.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hjlc.base.controller.BaseController;
import com.hjlc.system.model.Menu;
import com.hjlc.system.model.Role;
import com.hjlc.system.model.User;
import com.hjlc.system.service.MenuService;
import com.hjlc.system.service.RoleService;
import com.hjlc.system.service.UserService;
import com.hjlc.util.utils.AppUtil;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.PageData;
import com.hjlc.util.utils.RightsHelper;
import com.hjlc.util.utils.Tools;
/*
 * 总入口
 */
@Controller
public class LoginController extends BaseController {

	@Resource(name="userService")
	private UserService userService;
	@Resource(name="menuService")
	private MenuService menuService;
	@Resource(name="roleService")
	private RoleService roleService;
	
	/**
	 * 获取登录用户的IP
	 * @throws Exception 
	 */
	public void getRemortIP(String USERNAME) throws Exception {  
		PageData pd = new PageData();
		HttpServletRequest request = this.getRequest();
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {  
			ip = request.getRemoteAddr();  
	    }else{
	    	ip = request.getHeader("x-forwarded-for");  
	    }
		pd.put("USERNAME", USERNAME);
		pd.put("IP", ip);
		userService.saveIP(pd);
	}  
	
	/**
	 * 访问登录页
	 * @return
	 */
	@RequestMapping(value="/login_toLogin")
	public ModelAndView toLogin()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("SYSNAME", Tools.readTxtFile(Constants.SYSNAME)); //读取系统名称
		mv.setViewName(PAGE_PATH_PREFIX + "system/admin/login.jsp");
		mv.addObject("pd",pd);
		return mv;
	}
	
	/**
	 * 请求登录，验证用户
	 */
	@RequestMapping(value="/login_login" ,produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object login()throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errInfo = "";
		String KEYDATA[] = pd.getString("KEYDATA").replaceAll("qq313596790fh", "").replaceAll("QQ978336446fh", "").split(",fh,");
		
		if(null != KEYDATA && KEYDATA.length == 3){
			//shiro管理的session
			Subject currentUser = SecurityUtils.getSubject();  
			Session session = currentUser.getSession();
			String sessionCode = (String)session.getAttribute(Constants.SESSION_SECURITY_CODE);		//获取session中的验证码
			
			String code = KEYDATA[2];
			if(null == code || "".equals(code)){
				errInfo = "nullcode"; //验证码为空
			}else{
				String USERNAME = KEYDATA[0];
				String PASSWORD  = KEYDATA[1];
				pd.put("USERNAME", USERNAME);
				if(!StringUtils.isEmpty(sessionCode) && sessionCode.equalsIgnoreCase(code)){
					String passwd = new SimpleHash("SHA-1", USERNAME, PASSWORD).toString();	//密码加密
					logger.info(passwd);
					pd.put("PASSWORD", passwd);
					pd = userService.getUserByNameAndPwd(pd);
					if(pd != null){
						pd.put("LAST_LOGIN",DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
						userService.updateLastLogin(pd);
						User user = new User();
						user.setUSER_ID(pd.getString("USER_ID"));
						user.setUSERNAME(pd.getString("USERNAME"));
						user.setPASSWORD(pd.getString("PASSWORD"));
						user.setNAME(pd.getString("NAME"));
						user.setRIGHTS(pd.getString("RIGHTS"));
						user.setROLE_ID(pd.getString("ROLE_ID"));
						user.setLAST_LOGIN(pd.getString("LAST_LOGIN"));
						user.setIP(pd.getString("IP"));
						user.setSTATUS(pd.getString("STATUS"));
						session.setAttribute(Constants.SESSION_USER, user);
						session.removeAttribute(Constants.SESSION_SECURITY_CODE);
						
						//shiro加入身份验证
						Subject subject = SecurityUtils.getSubject(); 
					    UsernamePasswordToken token = new UsernamePasswordToken(USERNAME, PASSWORD); 
					    try { 
					        subject.login(token); 
					    } catch (AuthenticationException e) { 
					    	errInfo = "身份验证失败！";
					    }
					}else{
						errInfo = "usererror"; 				//用户名或密码有误
					}
				}else{
					errInfo = "codeerror";				 	//验证码输入有误
				}
				if(StringUtils.isEmpty(errInfo)){
					errInfo = "success";					//验证成功
				}
			}
		}else{
			errInfo = "error";	//缺少参数
		}
		map.put("result", errInfo);
		return AppUtil.returnObject(new PageData(), map);
	}
	
	/**
	 * 访问系统首页
	 */
	@RequestMapping(value="/main/{changeMenu}")
	public ModelAndView login_index(@PathVariable("changeMenu") String changeMenu){
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try{
			//shiro管理的session
			Subject currentUser = SecurityUtils.getSubject();  
			Session session = currentUser.getSession();
			
			User user = (User)session.getAttribute(Constants.SESSION_USER);
			if (user != null) {
				
				User userr = (User)session.getAttribute(Constants.SESSION_USERROL);
				if(null == userr){
					user = userService.getUserAndRoleById(user.getUSER_ID());
					session.setAttribute(Constants.SESSION_USERROL, user);
				}else{
					user = userr;
				}
				Role role = user.getRole();
				String roleRights = role!=null ? role.getRIGHTS() : "";
				//避免每次拦截用户操作时查询数据库，以下将用户所属角色权限、用户权限限都存入session
				session.setAttribute(Constants.SESSION_ROLE_RIGHTS, roleRights); 		//将角色权限存入session
				session.setAttribute(Constants.SESSION_USERNAME, user.getUSERNAME());	//放入用户名
				
				List<Menu> allmenuList = new ArrayList<Menu>();
				
				if(null == session.getAttribute(Constants.SESSION_allmenuList)){
					allmenuList = menuService.listAllMenu();
					if(!StringUtils.isEmpty(roleRights)){
						for(Menu menu : allmenuList){
							menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
							if(menu.isHasMenu()){
								List<Menu> subMenuList = menu.getSubMenu();
								for(Menu sub : subMenuList){
									sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
								}
							}
						}
					}
					session.setAttribute(Constants.SESSION_allmenuList, allmenuList);			//菜单权限放入session中
				}else{
					allmenuList = (List<Menu>)session.getAttribute(Constants.SESSION_allmenuList);
				}
				
				//切换菜单=====
				List<Menu> menuList = new ArrayList<Menu>();
				//if(null == session.getAttribute(Const.SESSION_menuList) || ("yes".equals(pd.getString("changeMenu")))){
				if(null == session.getAttribute(Constants.SESSION_menuList) || ("yes".equals(changeMenu))){
					List<Menu> menuList1 = new ArrayList<Menu>();
					List<Menu> menuList2 = new ArrayList<Menu>();
					
					//拆分菜单
					for(int i=0;i<allmenuList.size();i++){
						Menu menu = allmenuList.get(i);
						if("1".equals(menu.getMENU_TYPE())){
							menuList1.add(menu);
						}else{
							menuList2.add(menu);
						}
					}
					
					session.removeAttribute(Constants.SESSION_menuList);
					if("2".equals(session.getAttribute("changeMenu"))){
						session.setAttribute(Constants.SESSION_menuList, menuList1);
						session.removeAttribute("changeMenu");
						session.setAttribute("changeMenu", "1");
						menuList = menuList1;
					}else{
						session.setAttribute(Constants.SESSION_menuList, menuList2);
						session.removeAttribute("changeMenu");
						session.setAttribute("changeMenu", "2");
						menuList = menuList2;
					}
				}else{
					menuList = (List<Menu>)session.getAttribute(Constants.SESSION_menuList);
				}
				//切换菜单=====
				
				if(null == session.getAttribute(Constants.SESSION_QX)){
					session.setAttribute(Constants.SESSION_QX, this.getUQX(session));	//按钮权限放到session中
				}
				
				//FusionCharts 报表
			 	String strXML = "<graph caption='前12个月订单销量柱状图' xAxisName='月份' yAxisName='值' decimalPrecision='0' formatNumberScale='0'><set name='2013-05' value='4' color='AFD8F8'/><set name='2013-04' value='0' color='AFD8F8'/><set name='2013-03' value='0' color='AFD8F8'/><set name='2013-02' value='0' color='AFD8F8'/><set name='2013-01' value='0' color='AFD8F8'/><set name='2012-01' value='0' color='AFD8F8'/><set name='2012-11' value='0' color='AFD8F8'/><set name='2012-10' value='0' color='AFD8F8'/><set name='2012-09' value='0' color='AFD8F8'/><set name='2012-08' value='0' color='AFD8F8'/><set name='2012-07' value='0' color='AFD8F8'/><set name='2012-06' value='0' color='AFD8F8'/></graph>" ;
			 	mv.addObject("strXML", strXML);
			 	//FusionCharts 报表
			 	
				mv.setViewName("/WEB-INF/jsp/system/admin/index.jsp");
				mv.addObject("user", user);
				mv.addObject("menuList", menuList);
			}else {
				mv.setViewName("/WEB-INF/jsp/system/admin/login.jsp");//session失效后跳转登录页面
			}
			
			
		} catch(Exception e){
			mv.setViewName("/WEB-INF/jsp/system/admin/login.jsp");
			logger.error(e.getMessage(), e);
		}
		pd.put("SYSNAME", Tools.readTxtFile(Constants.SYSNAME)); //读取系统名称
		mv.addObject("pd",pd);
		return mv;
	}
	
	/**
	 * 进入tab标签
	 * @return
	 */
	@RequestMapping(value="/tab")
	public String tab(){
		return "/WEB-INF/jsp/system/admin/tab.jsp";
	}
	
	/**
	 * 进入首页后的默认页面
	 * @return
	 */
	@RequestMapping(value="/login_default")
	public String defaultPage(){
		return "/WEB-INF/jsp/system/admin/default.jsp";
	}
	
	/**
	 * 用户注销
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/logout")
	public ModelAndView logout(){
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		
		//shiro管理的session
		Subject currentUser = SecurityUtils.getSubject();  
		Session session = currentUser.getSession();
		
		session.removeAttribute(Constants.SESSION_USER);
		session.removeAttribute(Constants.SESSION_ROLE_RIGHTS);
		session.removeAttribute(Constants.SESSION_allmenuList);
		session.removeAttribute(Constants.SESSION_menuList);
		session.removeAttribute(Constants.SESSION_QX);
		session.removeAttribute(Constants.SESSION_userpds);
		session.removeAttribute(Constants.SESSION_USERNAME);
		session.removeAttribute(Constants.SESSION_USERROL);
		session.removeAttribute("changeMenu");
		
		//shiro销毁登录
		Subject subject = SecurityUtils.getSubject(); 
		subject.logout();
		
		pd = this.getPageData();
		String  msg = pd.getString("msg");
		pd.put("msg", msg);
		
		pd.put("SYSNAME", Tools.readTxtFile(Constants.SYSNAME)); //读取系统名称
		mv.setViewName("/WEB-INF/jsp/system/admin/login.jsp");
		mv.addObject("pd",pd);
		return mv;
	}
	
	/**
	 * 获取用户权限
	 */
	public Map<String, String> getUQX(Session session){
		PageData pd = new PageData();
		Map<String, String> map = new HashMap<String, String>();
		try {
			String USERNAME = session.getAttribute(Constants.SESSION_USERNAME).toString();
			pd.put(Constants.SESSION_USERNAME, USERNAME);
			String ROLE_ID = userService.findByUId(pd).get("ROLE_ID").toString();
			
			pd.put("ROLE_ID", ROLE_ID);
			
			PageData pd2 = new PageData();
			pd2.put(Constants.SESSION_USERNAME, USERNAME);
			pd2.put("ROLE_ID", ROLE_ID);
			
			pd = roleService.findObjectById(pd);																
				
			pd2 = roleService.findGLbyrid(pd2);
			if(null != pd2){
				map.put("FX_QX", pd2.get("FX_QX").toString());
				map.put("FW_QX", pd2.get("FW_QX").toString());
				map.put("QX1", pd2.get("QX1").toString());
				map.put("QX2", pd2.get("QX2").toString());
				map.put("QX3", pd2.get("QX3").toString());
				map.put("QX4", pd2.get("QX4").toString());
			
				pd2.put("ROLE_ID", ROLE_ID);
				pd2 = roleService.findYHbyrid(pd2);
				map.put("C1", pd2.get("C1").toString());
				map.put("C2", pd2.get("C2").toString());
				map.put("C3", pd2.get("C3").toString());
				map.put("C4", pd2.get("C4").toString());
				map.put("Q1", pd2.get("Q1").toString());
				map.put("Q2", pd2.get("Q2").toString());
				map.put("Q3", pd2.get("Q3").toString());
				map.put("Q4", pd2.get("Q4").toString());
			}
			
			map.put("adds", pd.getString("ADD_QX"));
			map.put("dels", pd.getString("DEL_QX"));
			map.put("edits", pd.getString("EDIT_QX"));
			map.put("chas", pd.getString("CHA_QX"));
			this.getRemortIP(USERNAME);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return map;
	}
}