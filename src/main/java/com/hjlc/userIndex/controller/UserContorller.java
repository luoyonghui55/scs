package com.hjlc.userIndex.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hjlc.base.controller.BaseController;
import com.hjlc.userIndex.model.AppUser;
import com.hjlc.userIndex.service.AppUserService;
import com.hjlc.util.utils.AppUtil;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.PageData;
import com.hjlc.util.utils.PathUtil;
import com.hjlc.util.utils.RedisUtil;
import com.hjlc.util.utils.SmsUtil;
import com.hjlc.util.utils.Tools;

import redis.clients.jedis.Jedis;
//import redis.clients.jedis.params.set.SetParams;

@Controller
@RequestMapping(value="/userIndex/user")
public class UserContorller extends BaseController {
	@Resource(name="appUserService")
	private AppUserService appUserService;
	//private EhcacheUtil userCache = EhcacheUtil.getInstance();
	//private String userCacheName = "checkCodeDataCache";
	
	//发送注册验证码
	@RequestMapping(value="/sendPhoneCheckCode")
	@ResponseBody
	public Object sendPhoneCheckCode(HttpServletRequest request){
		PageData pd = this.getPageData();
		//pd.put("PHONE", phone);
		try {
			AppUser appUser = appUserService.findByMobile(pd);
			if(appUser != null){
				pd.put("state", "2"); //号码已注册
				return AppUtil.returnObject(new PageData(), pd);
			}
			String phone = pd.getString("PHONE"), pccSendTimes = "pccSendTimes:";
			if (!Tools.checkMobileNumber(phone)) {
				pd.put("state", "6"); //号码格式不对
				return AppUtil.returnObject(new PageData(), pd);
			}
			Jedis redis = RedisUtil.getRedis();
			if (RedisUtil.redisIsUsable) {
				//以下代码，控制同一手机号3小时内最多获取5次验证码,
				//如果手机号不为空，则已发送过一次验证码
				String checkResult = checkPhoneAndIP(redis,pccSendTimes + phone, "5", request);
				if (StringUtils.hasText(checkResult)) {
					pd.put("state", checkResult); //手机号码发送验证码已超过5次
					return AppUtil.returnObject(new PageData(), pd);
				}
				String clientIP = PathUtil.getIPAddress(request);
				//以下代码，控制同一IP3小时内最多获取5次验证码
				checkResult = checkPhoneAndIP(redis,pccSendTimes + clientIP, "4", request);
				if (StringUtils.hasText(checkResult)) {
					pd.put("state", checkResult); //客户端IP地址发送验证码已超过5次
					return AppUtil.returnObject(new PageData(), pd);
				}
			}
			
			int randomNum = Tools.getRandomNum();
			//注：此为短信发送的模板，不可修改，否则会发送不出短信，模板可在平台上修改或添加成自己想要的模板
			String checkWord = "您的注册验证码是" + randomNum + "，在20分钟内输入有效。如非本人操作请忽略此短信。";
			//给手机发送验证码
			//SmsUtil.singleSend(phone, checkWord);
			System.err.println(checkWord);
			//redis.set("pcc:" + phone, randomNum + "", SetParams.setParams().ex(1200));//20分钟
			redis.setex("pcc:" + phone,1200, randomNum + "");//20分钟
			pd.put("state", "1");
		} catch (Exception e) {
			e.printStackTrace();
			pd.put("state", "0");		//获取验证码失败
		}
		return AppUtil.returnObject(new PageData(), pd);
	}
	/**
	 * 校验同一IP或同一个手机号码3小时内是否已获取5次验证码
	 * @param redisKey
	 * @param request
	 * @return 
	 */
	private String checkPhoneAndIP(Jedis redis,String redisKey, String checkFlag, HttpServletRequest request){
		String phoneSendNumberStr = redis.get(redisKey);
		//SetParams setParams = SetParams.setParams();
		if (StringUtils.hasText(phoneSendNumberStr)) {
			//同一手机号码，3小时内获取验证码的次数不超过5次
			int phoneSendNumber = Integer.parseInt(phoneSendNumberStr);
			if (phoneSendNumber >= 5) {
				return checkFlag;
			}
			phoneSendNumber ++;
			//key还剩余多少秒过期
			long keySurplusLiveSecond = redis.ttl(redisKey);
			//setParams.ex((int) keySurplusLiveSecond);//3小时
			//redis.set(redisKey, phoneSendNumber + "", setParams);
			redis.setex(redisKey, (int)keySurplusLiveSecond,phoneSendNumber + "");
		}else {
			//setParams.ex(10800);//3小时
			//redis.set(redisKey, "1", setParams);
			redis.setex(redisKey, 10800,"1");
		}
		return null;
	}
	//登录
	@RequestMapping(value="/toLogin", produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object toLogin() throws Exception{
		Map<String, String> map = new HashMap<String, String>(4);
		PageData pd = this.getPageData();
		String userName = pd.getString("userName");
		String userPassword = pd.getString("userPassword");
		String scsToken = pd.getString("scsToken");
		if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPassword)){
			map.put("status", "1");
		}else {
			if (StringUtils.isEmpty(scsToken) || !("0" + scsToken + "D").equals(Constants.SCS_LOGIN_TOKEN)) {
				map.put("status", "2");
			}else {
				userPassword = new SimpleHash("SHA-1", userName, userPassword).toString();
				//System.err.println("----------PASSWORD=" + userPassword);
				pd.put("USERNAME", userName);
				pd.put("PASSWORD", userPassword);
				AppUser user = appUserService.findByNameAndPassword(pd);
				if(user != null){
					//用户已冻结
					if("1".equals(user.getStatus())){
						map.put("status", "5");
						return map;
					}
					map.put("status", "0");//验证成功
					map.put("userPhone", user.getPhone() == null ? "" : user.getPhone());
					map.put("userId", user.getUserId());
					Subject subject = SecurityUtils.getSubject(); 
				    UsernamePasswordToken token = new UsernamePasswordToken(userName, userPassword); 
				    try {
				        subject.login(token);
				        subject.getSession().setAttribute(Constants.SESSION_APP_USER, user);
				        //System.out.println("\nsid=" + subject.getSession().getId().toString() + "\n");
				    } catch (AuthenticationException e) { 
				    	map.put("status", "4");
				    }
				}else{
					map.put("status", "3");
				}
			}
		}
		return AppUtil.returnObject(new PageData(), map);
	}

	//注册
	@RequestMapping("/IamRegister")
	@ResponseBody
	public Object IamRegister() throws Exception {
		//System.out.println(password+":"+phone+":"+sendMsg);
		PageData pd = this.getPageData();
		//pd.put("PHONE", phone);
		try {
			String sendMsg = pd.getString("sendMsg");
			//从redis中取验证码，并与传过来的比较
			if (sendMsg.equals("")) {
				pd.put("state", "3");
			}
			pd.remove("sendMsg");
			AppUser	appUser = appUserService.findByMobile(pd);
			String phone = pd.getString("phone");
			String password = pd.getString("password");
			if(appUser == null ){
				if(!StringUtils.isEmpty(phone)){
					password = new SimpleHash("SHA-1", phone, password).toString();
					//String pwd = Tools.md5(password);
					//String userId = Tools.getUUID();
					pd.put("PASSWORD", password);
					pd.put("USERNAME", phone);
					pd.put("PHONE", phone);
					appUserService.saveAppUser(pd);
					pd.clear();
					appUser = new AppUser();
					appUser.setUserName(phone);
					appUser.setPhone(phone);
					//appUser.setUserId(userId);
					Subject subject = SecurityUtils.getSubject(); 
				    UsernamePasswordToken token = new UsernamePasswordToken(phone, password);
			        subject.login(token);
			        subject.getSession().setAttribute(Constants.SESSION_APP_USER, appUser);
					pd.put("state", "1");
				}else {
					pd.put("state", "4");
				}
			}else{
				pd.put("state", "2");
			}
		} catch (Exception e) {
			e.printStackTrace();
			pd.put("state", "0");
		}
		pd.remove("password");
		return AppUtil.returnObject(new PageData(), pd);
	}

	@RequestMapping(value="/toLogout")
	@ResponseBody
	public Object toLogout(){
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
		SecurityUtils.getSubject().logout();
		
		PageData pd = this.getPageData();
		pd.put("status", "0");
		return AppUtil.returnObject(new PageData(), pd);
	}
}