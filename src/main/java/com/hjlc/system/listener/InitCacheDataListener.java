package com.hjlc.system.listener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.hjlc.system.model.Page;
import com.hjlc.system.service.DictionariesService;
import com.hjlc.userIndex.service.AddressService;
import com.hjlc.userIndex.service.ServiceOrderService;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.ObjectUtil;
import com.hjlc.util.utils.PageData;
import com.hjlc.util.utils.RedisUtil;

import redis.clients.jedis.Jedis;

/**
 * 初始化缓存数据，此缓存使用Redis缓存
 */
//@Component("InitCacheDataListener")==========去掉启动初始化
public class InitCacheDataListener implements ApplicationListener<ContextRefreshedEvent> {
	@Resource(name="addressService")
	private AddressService addressService;
	@Resource(name="dictionariesService")
	private DictionariesService dictionariesService;
	@Resource(name="serviceOrderService")
	private ServiceOrderService serviceOrderService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		/*不做此判断，会执行多次，
		applicationontext和使用MVC之后的webApplicationontext会两次调用上面的方法，如何区分这个两种容器呢？
		但是这个时候，会存在一个问题，在web 项目中（spring mvc），系统会存在两个容器，一个是root application context,
		另一个就是我们自己的 projectName-servlet context（作为root application context的子容器）。*/
		if (event.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
			PageData pageData = new PageData();
			String[] cityIdArray = Constants.HOT_CITY_IDS;
			Jedis redis = RedisUtil.getRedis();
			try {
				//将省市区信息添加的缓存中
				//EhcacheUtil ehcache = EhcacheUtil.getInstance();
				//ehcache.put(Constants.EHCACHE_STATIC_ANME, Constants.EHCACHE_ALL_PROVINCES_KEY, addressService.listAllProvinces(null));
				List<PageData> provinceList = addressService.listAllProvinces(null);
				byte[] redisProviceKey = (Constants.UI_PROVICE).getBytes();
				byte[] redisHotCityKey = (Constants.UI_HOTCITY).getBytes();
				String redisAreaKey = Constants.UI_AREA;
				byte[] redisServiceTypeKey = Constants.SYS_SERVICETYPE.getBytes();
				redis.del(redisProviceKey, redisHotCityKey, redisServiceTypeKey);//redisAreaKey
				redis.lpush(redisProviceKey, ObjectUtil.listToByteArray(provinceList));//将provinceList封装成数组后一次性添加到缓存中
				pageData.put("array", cityIdArray);
				List<PageData> cityList = addressService.listAllCities(pageData);//获取热门城市列表
				/*for(PageData cityHotCity : cityList){
					redis.lpush(redisHotCityKey, ObjectUtil.objectToBytes(cityHotCity));
				}*/
				redis.lpush(redisHotCityKey, ObjectUtil.listToByteArray(cityList));
				//获取热闹城市下的所有区县信息
				List<PageData> hotAreaList = addressService.listAllArea(pageData);
				
				for(String cityId : cityIdArray){
					List<PageData> areaList = new ArrayList<PageData>();
					for(PageData areaPd : hotAreaList){
						if (areaPd.get("cityid").toString().equals(cityId)) {
							areaList.add(areaPd);
						}
					}
					redis.set((redisAreaKey + cityId).getBytes(), ObjectUtil.objectToBytes(areaList));
					//redis.lpush(redisAreaKey.getBytes(), ObjectUtil.objectToBytes(areaList));
					//ehcache.put(Constants.EHCACHE_STATIC_ANME, Constants.CACHE_AREA_KEY_PREFIX + cityId, areaList);
				}
				//查询服务类型并存入缓存中
				Page page = new Page();
				pageData.clear();
				pageData.put("P_BM", "SERVICE_TYPE");
				page.setShowCount(100);
				page.setPd(pageData);
				/**
				 * 暂时不用redis缓存订单信息，因为后期涉及模糊查询和按类型查询等信息
				 */
				/*List<PageData> serviceTypeList = null;dictionariesService.pageDictlistPage(page);
				//ehcache.put(Constants.EHCACHE_STATIC_ANME, Constants.EHCACHE_SERVICE_TYPE_KEY, serviceTypeList);
				redis.set(redisServiceTypeKey, ObjectUtil.objectToBytes(serviceTypeList));
				//将热门城市的服务订单信息查询出来放到缓存中
				for(String hotCityId : Constants.HOT_CITY_IDS){
					page = new Page();
					pageData.clear();
					pageData.put("cityid", hotCityId);
					pageData.put("so_status", "0");
					page.setShowCount(15);
					page.setPd(pageData);
					String redisServiceKey = Constants.CO_SERVICE + hotCityId;
					redis.del(redisServiceKey);
					List<PageData> serviceOrderList = serviceOrderService.serviceOrderlistPage(page);
					if (serviceOrderList != null && serviceOrderList.size() > 0) {
						//ehcache.put(Constants.EHCACHE_STATIC_ANME, hotCityId, serviceOrderList);
						//for (PageData pdServiceOrder : serviceOrderList) {
						//	redis.hmset(redisServiceKey , pdServiceOrder);//+ pdServiceOrder.getString("so_id")
						//}
						Collections.reverse(serviceOrderList);//将LIST返序
						byte[][] serviceOrderByteArray = ObjectUtil.listToByteArray(serviceOrderList);
						if (serviceOrderByteArray != null && serviceOrderByteArray.length > 0) {
							redis.lpush(redisServiceKey.getBytes(), serviceOrderByteArray);
						}
						redis.set(Constants.CO_SERVICE_TC + hotCityId, page.getTotalResult() + "");
						redis.set(Constants.CO_SERVICE_TP + hotCityId, page.getTotalPage() + "");
					}
				}*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}