package com.hjlc.system.interceptor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hjlc.system.model.Menu;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.RightsHelper;
/**
 * Shiro登录验证器
 */
public class LoginHandlerInterceptor extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String path = request.getServletPath();
		if(path.matches(Constants.NO_INTERCEPTOR_PATH)){
			return true;
		}else{
			//shiro管理的session
			Session session = SecurityUtils.getSubject().getSession();
			Object appUserObj = session.getAttribute(Constants.SESSION_APP_USER);
			if (appUserObj != null) {
				return true;
			}
			//User user = (User)session.getAttribute(Constants.SESSION_USER);
			Object userObj = session.getAttribute(Constants.SESSION_USER);
			if(userObj != null){
				//判断是否拥有当前点击菜单的权限（内部过滤,防止通过url进入跳过菜单权限）
				/**
				 * 根据点击的菜单的xxx.do去菜单中的URL去匹配，当匹配到了此菜单，判断是否有此菜单的权限，没有的话跳转到404页面
				 * 根据按钮权限，授权按钮(当前点的菜单和角色中各按钮的权限匹对)
				 */
				
				List<Menu> menuList = (List)session.getAttribute(Constants.SESSION_allmenuList); //获取菜单列表
				path = path.substring(1, path.length());
				for(int i=0;i<menuList.size();i++){
					for(int j=0;j<menuList.get(i).getSubMenu().size();j++){
						if(menuList.get(i).getSubMenu().get(j).getMENU_URL().split(".do")[0].equals(path.split(".do")[0])){
							if(!menuList.get(i).getSubMenu().get(j).isHasMenu()){				//判断有无此菜单权限
								response.sendRedirect(request.getContextPath() + Constants.LOGIN);
								return false;
							}else{																//按钮判断
								Map<String, String> map = (Map<String, String>)session.getAttribute(Constants.SESSION_QX);//按钮权限
								map.remove("add");
								map.remove("del");
								map.remove("edit");
								map.remove("cha");
								String MENU_ID =  menuList.get(i).getSubMenu().get(j).getMENU_ID();
								//获取当前登录者loginname
								String USERNAME = session.getAttribute(Constants.SESSION_USERNAME).toString();
								Boolean isAdmin = "admin".equals(USERNAME);
								map.put("add", (RightsHelper.testRights(map.get("adds"), MENU_ID)) || isAdmin?"1":"0");
								map.put("del", RightsHelper.testRights(map.get("dels"), MENU_ID) || isAdmin?"1":"0");
								map.put("edit", RightsHelper.testRights(map.get("edits"), MENU_ID) || isAdmin?"1":"0");
								map.put("cha", RightsHelper.testRights(map.get("chas"), MENU_ID) || isAdmin?"1":"0");
								session.removeAttribute(Constants.SESSION_QX);
								session.setAttribute(Constants.SESSION_QX, map);	//重新分配按钮权限
							}
						}
					}
				}
				return true;
			}else{
				//登陆过滤
				response.sendRedirect(request.getContextPath() + "?loginUser=");
				//response.sendRedirect(request.getContextPath() + Constants.LOGIN);
				//return false;		
				return true;
			}
		}
	}
}