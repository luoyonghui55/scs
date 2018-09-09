package com.hjlc.util.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
 * 常量类
 */

public class Constants {
	public static final String SESSION_SECURITY_CODE = "sessionSecCode";
	public static final String SESSION_USER = "sessionUser";
	public static final String SESSION_APP_USER = "sessionAppUser";
	public static final String SESSION_ROLE_RIGHTS = "sessionRoleRights";
	public static final String SESSION_menuList = "menuList";			//当前菜单
	public static final String SESSION_allmenuList = "allmenuList";		//全部菜单
	public static final String SESSION_QX = "QX";
	public static final String SESSION_userpds = "userpds";			
	public static final String SESSION_USERROL = "USERROL";				//用户对象
	public static final String SESSION_USERNAME = "USERNAME";			//用户名
	public static final String TRUE = "T";
	public static final String FALSE = "F";
	public static final String LOGIN = "/login_toLogin.do";				//登录地址
	//public static final String SCS_INDEX = "/userIndex/scsIndex";       //系统首页
	public static final String SYSNAME = "admin/config/SYSNAME.txt";	//系统名称路径
	public static final String PAGE	= "admin/config/PAGE.txt";			//分页条数配置路径
	public static final String EMAIL = "admin/config/EMAIL.txt";		//邮箱服务器配置路径
	public static final String SMS1 = "admin/config/SMS1.txt";			//短信账户配置路径1
	public static final String SMS2 = "admin/config/SMS2.txt";			//短信账户配置路径2
	public static final String FWATERM = "admin/config/FWATERM.txt";	//文字水印配置路径
	public static final String IWATERM = "admin/config/IWATERM.txt";	//图片水印配置路径
	public static final String WEIXIN	= "admin/config/WEIXIN.txt";	//微信配置路径
	public static final String FILEPATHIMG = "uploadFiles/uploadImgs/";	//图片上传路径
	public static final String FILEPATHFILE = "uploadFiles/file/";		//文件上传路径
	public static final String FILEPATHTWODIMENSIONCODE = "uploadFiles/twoDimensionCode/"; //二维码存放路径
	public static final int PAGE_SIZE = 15;
	//不对匹配该值的访问路径拦截（正则）
	public static final String NO_INTERCEPTOR_PATH = ".*/((login)|(userIndex)|(userPages)|(logout)|(code)|(app)|(weixin)|(static)|(main)|(websocket)|(xccm)).*";
	
	public static ApplicationContext WEB_APP_CONTEXT = null; //该值会在web容器启动时由WebAppContextListener初始化
	public static final String EHCACHE_ALL_PROVINCES_KEY = "all_provinces_key";
	public static final String EHCACHE_ALL_CITIES_KEY = "all_cities_key";
	public static final String EHCACHE_ALL_AREA_KEY = "all_area_key";
	public static final String CACHE_CITY_KEY_PREFIX = "CACHE_CITY_KEY_PREFIX_";
	public static final String CACHE_AREA_KEY_PREFIX = "CACHE_AREA_KEY_PREFIX_";
	public static final String EHCACHE_STATIC_ANME = "staticDataCache";
	public static final String EHCACHE_SERVICE_TYPE_KEY = "SERVICE_TYPE_KEY";
	public static final String UEDITOR_IMG_SAVE_PATH = "C:/xccm";
	public static final Map<String, String> hotCityMap = new HashMap<String, String>(){{
		put("上海市", "310100");
		put("北京市", "110100");
		put("杭州市", "330100");
		put("武汉市", "420100");
		put("天津市", "120100");
		put("长沙市", "430100");
		put("广州市", "440100");
		put("深圳市", "440300");
		put("成都市", "510100");
	}};
	
	/**
	 * 热门城市ID数组,北京、上海、深圳、广州、杭州、天津、长沙、武汉、成都
	 */
	public static final String[] HOT_CITY_IDS = {"110100","310100","440300","440100","330100","120100","430100","420100","510100"};
	/**
	 * 热门省份ID数组,北京、上海、广东、湖北、湖南、四川
	 */
	public static final String[] HOT_PROVINCE_IDS = {"110000","310000","440000","330000","420000","430000","510000"};
	/**
	 * APP Constants
	 */
	//app注册接口_请求协议参数)
	public static final String[] APP_REGISTERED_PARAM_ARRAY = new String[]{"countries","uname","passwd","title","full_name","company_name","countries_code","area_code","telephone","mobile"};
	public static final String[] APP_REGISTERED_VALUE_ARRAY = new String[]{"国籍","邮箱帐号","密码","称谓","名称","公司名称","国家编号","区号","电话","手机号"};
	
	//app根据用户名获取会员信息接口_请求协议中的参数
	public static final String[] APP_GETAPPUSER_PARAM_ARRAY = new String[]{"USERNAME"};
	public static final String[] APP_GETAPPUSER_VALUE_ARRAY = new String[]{"用户名"};
	/**
	 * 登录TOKEN：#I@Love&yOu，被MD5加密的字符串
	 */
	public static final String SCS_LOGIN_TOKEN = "0C0525B8802AC5790EEA4593684A70FD";
	public static String PICTURE_VISIT_FILE_PATH = "";//图片访问的路径
	public static String PICTURE_SAVE_FILE_PATH = "";//图片存放的路径
	/**
	 * redis key前缀，字典类
	 */
	public static final String UI_PROVICE = "UI_PROVICE";
	/**
	 * redis key前缀，地址类
	 */
	public static final String UI_HOTCITY = "UI_HOTCITY";
	/**
	 * redis key前缀，省类
	 */
	public static final String UI_AREA = "UI_AREA_";
	/**
	 * redis key前缀，城市类
	 */
	public static final String SYS_SERVICETYPE = "SYS_SERVICETYPE";
	/**
	 * redis key前缀，服务数据
	 */
	public static final String CO_SERVICE = "CO_SERVICE_";//service
	public static final String CO_SERVICE_TC = "CO_SERVICE_TC_";//总记录数，TOTAL COUNT简称
	public static final String CO_SERVICE_TP = "CO_SERVICE_TP_";//总页数，TOTAL PAGE简称
	
	public static String getPICTURE_VISIT_FILE_PATH() {
		return PICTURE_VISIT_FILE_PATH;
	}

	public static void setPICTURE_VISIT_FILE_PATH(String pICTURE_VISIT_FILE_PATH) {
		PICTURE_VISIT_FILE_PATH = pICTURE_VISIT_FILE_PATH;
	}

	public static String getPICTURE_SAVE_FILE_PATH() {
		return PICTURE_SAVE_FILE_PATH;
	}

	public static void setPICTURE_SAVE_FILE_PATH(String pICTURE_SAVE_FILE_PATH) {
		PICTURE_SAVE_FILE_PATH = pICTURE_SAVE_FILE_PATH;
	}

}