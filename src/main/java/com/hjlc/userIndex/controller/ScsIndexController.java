package com.hjlc.userIndex.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hjlc.base.controller.BaseController;
import com.hjlc.system.model.Page;
import com.hjlc.system.service.DictionariesService;
import com.hjlc.system.service.UserService;
import com.hjlc.userIndex.model.ServiceOrder;
import com.hjlc.userIndex.service.AddressService;
import com.hjlc.userIndex.service.ServiceOrderService;
import com.hjlc.util.utils.AppUtil;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.EhcacheUtil;
import com.hjlc.util.utils.ObjectUtil;
import com.hjlc.util.utils.PageData;
import com.hjlc.util.utils.RedisUtil;

import redis.clients.jedis.Jedis;

/**
 * 系统前台部分首页部分
 */

@Controller
@RequestMapping(value="/userIndex")
public class ScsIndexController extends BaseController {
	@Resource(name="userService")
	private UserService userService;
	@Resource(name="addressService")
	private AddressService addressService;
	@Resource(name="dictionariesService")
	private DictionariesService dictionariesService;
	@Resource(name="serviceOrderService")
	private ServiceOrderService serviceOrderService;
	
	//跳转到登录页
	/*@RequestMapping(value="/goLogin")
	public ModelAndView login(HttpServletRequest request) throws UnsupportedEncodingException{
		//request.setCharacterEncoding("utf-8");
		Integer random = new Random().nextInt(999999);
		HttpSession session = request.getSession();
		session.setAttribute("code", random);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("userIndex/userLogin.html");
		return mv; 
	}*/
	
/*	//跳转到主页
	@RequestMapping("/goIndex")
	public ModelAndView index(){
		ModelAndView mv = new ModelAndView();
		//mv.setViewName("userIndex/scsIndex.html");
		mv.setViewName("userPages/scsIndex.html");
		return mv;
	}
*/
	//请求主页的页面数据
	@RequestMapping(value="/initPageInfo",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object initPageInfo() throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>(7);
		PageData pd = this.getPageData();
		List<PageData> areaList = null;
		//获取服务类型
		List<PageData> serviceTypeList = null;
		//取得城市ID
		String cityId = pd.getString("cityId");
		if (!StringUtils.hasText(cityId)) {
			cityId = Constants.hotCityMap.get(pd.getString("cityName"));
			if (!StringUtils.hasText(cityId)) {
				cityId = addressService.findCityIdByName(pd.getString("cityName"));
			}
		}
		Jedis redis = RedisUtil.getRedis();
		if (RedisUtil.redisIsUsable) {
			try{
				//获取该城市下的所有的区县
				byte[] areaByte = redis.get((Constants.UI_AREA + cityId).getBytes());
				
				//List<PageData> areaList = EhcacheUtil.getInstance().forCacheGetAreaByCityId(cityId);//从缓存中取
				if (areaByte == null || areaByte.length < 1) {
					pd.clear();
					pd.put("cityid", cityId);
					//缓存中不存在，则从数据库中取
					areaList = addressService.listAllArea(pd);
					redis.set((Constants.UI_AREA + cityId).getBytes(), ObjectUtil.objectToBytes(areaList));
				}else{
					areaList = (List<PageData>) ObjectUtil.bytesToObject(areaByte);
				}
				//Object ost = EhcacheUtil.getInstance().getValue(Constants.EHCACHE_STATIC_ANME, Constants.EHCACHE_SERVICE_TYPE_KEY);
				byte[] serviceTypeBytes = null;//redis.get(Constants.SYS_SERVICETYPE.getBytes());
				if (serviceTypeBytes == null || serviceTypeBytes.length < 1) {
					Page page = new Page();
					pd.clear();
					pd.put("P_BM", "SERVICE_TYPE");
					page.setPd(pd);
					serviceTypeList = dictionariesService.pageDictlistPage(page);
					redis.set(Constants.SYS_SERVICETYPE.getBytes(), ObjectUtil.objectToBytes(serviceTypeList));
				}else {
					serviceTypeList = (List<PageData>)ObjectUtil.bytesToObject(serviceTypeBytes);
				}
			}catch (Exception e) {
				//如果redis出现异常，则从数据库中取
				pd.clear();
				pd.put("cityid", cityId);
				//缓存中不存在，则从数据库中取
				areaList = addressService.listAllArea(pd);
				Page page = new Page();
				pd.remove("cityid");
				pd.put("P_BM", "SERVICE_TYPE");
				page.setPd(pd);
				serviceTypeList = dictionariesService.pageDictlistPage(page);
			}
		}else {
			//如果redis出现异常，则从数据库中取
			pd.clear();
			pd.put("cityid", cityId);
			//缓存中不存在，则从数据库中取
			areaList = addressService.listAllArea(pd);
			Page page = new Page();
			pd.remove("cityid");
			pd.put("P_BM", "SERVICE_TYPE");
			page.setPd(pd);
			serviceTypeList = dictionariesService.pageDictlistPage(page);
		}
		resultMap.put("areaList", areaList);
		resultMap.put("serviceTypeList", serviceTypeList);
		resultMap.put("status", "0");
		resultMap.put("cityid", cityId);
		return AppUtil.returnObject(new PageData(), resultMap);
	}
	//请求主页的页面数据
	@RequestMapping(value="/queryServiceOrder",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object queryServiceOrder(Page page) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>(7);
		PageData pd = this.getPageData();
		List<PageData> serviceOrderList = null;
		//取得城市ID
		String cityId = pd.getString("cityId");
		if (!StringUtils.hasText(cityId)) {
			cityId = Constants.hotCityMap.get(pd.getString("cityName"));
			if (!StringUtils.hasText(cityId)) {
				cityId = addressService.findCityIdByName(pd.getString("cityName"));
			}
		}
		Jedis redis = RedisUtil.getRedis();
		if (RedisUtil.redisIsUsable) {
			try {
				String redisServiceKey = Constants.CO_SERVICE + cityId;
				int start = page.getCurrentResult();
				//从REDIS缓存中获取服务订单数据
				List<byte[]> serviceOrderByteList = redis.lrange(redisServiceKey.getBytes(), start, start + page.getShowCount());
				serviceOrderList = ObjectUtil.byteListToPageDataList(serviceOrderByteList);
				if (serviceOrderList == null || serviceOrderList.isEmpty()) {
					pd.put("cityid", cityId);
					pd.put("so_status", "0");
					page.setPd(pd);
					serviceOrderList = serviceOrderService.serviceOrderlistPage(page);
				}else {
					String tp = redis.get(Constants.CO_SERVICE_TP + cityId),
						   tc = redis.get(Constants.CO_SERVICE_TC + cityId);
					page.setTotalPage(StringUtils.hasText(tp) ? Integer.parseInt(tp) : 0);
					page.setTotalResult(StringUtils.hasText(tc) ? Integer.parseInt(tc) : 0);
				}
			} catch (Exception e) {
				//如果redis出现异常，则从数据库中取
				pd.put("cityid", cityId);
				pd.put("so_status", "0");
				page.setPd(pd);
				serviceOrderList = serviceOrderService.serviceOrderlistPage(page);
			}
		}else {
			//如果redis出现异常，则从数据库中取
			pd.put("cityid", cityId);
			pd.put("so_status", "0");
			page.setPd(pd);
			serviceOrderList = serviceOrderService.serviceOrderlistPage(page);
		}
		resultMap.put("status", "0");
		resultMap.put("serviceOrderList", serviceOrderList);
		resultMap.put("serviceOrderTotalResult", page.getTotalResult());
		resultMap.put("serviceOrderTotalPage", page.getTotalPage());
		return AppUtil.returnObject(new PageData(), resultMap);
	}
	//获取所有服务类型
	@RequestMapping(value="/queryServiceType",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object queryAllServiceType() throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		PageData pd = this.getPageData();
		
		List<PageData> serviceTypeList = null;
		Object ost = EhcacheUtil.getInstance().getValue(Constants.EHCACHE_STATIC_ANME, Constants.EHCACHE_SERVICE_TYPE_KEY);
		if (ost == null) {
			Page page = new Page();
			pd.put("P_BM", "SERVICE_TYPE");
			page.setPd(pd);
			serviceTypeList = dictionariesService.pageDictlistPage(page);
		}else {
			serviceTypeList = (List<PageData>)ost;
		}
		resultMap.put("serviceTypeList", serviceTypeList);
		resultMap.put("status", "0");
		return AppUtil.returnObject(new PageData(), resultMap);
	}
	//查看订单
	@RequestMapping(value="/showOrderDetail")
	public ModelAndView showOrderDetail() throws Exception {
		ModelAndView mav = this.getModelAndView();
		PageData pd = this.getPageData();
		String osId = pd.getString("oid");
		pd.clear();
		pd.put("soBillNumber", osId);
		ServiceOrder serviceOrder = serviceOrderService.getServiceOrderById(pd);
		mav.addObject("so", serviceOrder);
		mav.setViewName("userPages/orderDetail.jsp");
		return mav;
	}
	
}