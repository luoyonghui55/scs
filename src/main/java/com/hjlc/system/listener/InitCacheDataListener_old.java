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
import com.hjlc.util.utils.EhcacheUtil;
import com.hjlc.util.utils.PageData;

/**
 * 初始化缓存数据,此缓存使用Ecache缓存
 */
//@Component("InitCacheDataListener")
public class InitCacheDataListener_old implements ApplicationListener<ContextRefreshedEvent> {
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
			try {
				//将省市区信息添加的缓存中
				EhcacheUtil ehcache = EhcacheUtil.getInstance();
				ehcache.put(Constants.EHCACHE_STATIC_ANME, Constants.EHCACHE_ALL_PROVINCES_KEY, addressService.listAllProvinces(null));
				pageData.put("array", cityIdArray);
				List<PageData> cityList = addressService.listAllCities(pageData);//获取热闹城市列表
				for(PageData pd : cityList){
					ehcache.put(Constants.EHCACHE_STATIC_ANME, Constants.CACHE_CITY_KEY_PREFIX + pd.get("cityid").toString(), pd);
				}
				//获取热闹城市下的所有区县信息
				List<PageData> hotAreaList = addressService.listAllArea(pageData);
				for(String cityId : cityIdArray){
					List<PageData> areaList = new ArrayList<PageData>();
					for(PageData areaPd : hotAreaList){
						if (areaPd.get("cityid").toString().equals(cityId)) {
							areaList.add(areaPd);
						}
					}
					ehcache.put(Constants.EHCACHE_STATIC_ANME, Constants.CACHE_AREA_KEY_PREFIX + cityId, areaList);
				}
				//查询服务类型并存入缓存中
				Page page = new Page();
				pageData.clear();
				pageData.put("P_BM", "SERVICE_TYPE");
				page.setShowCount(100);
				page.setPd(pageData);
				List<PageData> serviceTypeList = dictionariesService.pageDictlistPage(page);
				ehcache.put(Constants.EHCACHE_STATIC_ANME, Constants.EHCACHE_SERVICE_TYPE_KEY, serviceTypeList);
				
				//将热门城市的服务订单信息查询出来放到缓存中
				List<PageData> serviceOrderList = null;
				for(String hotCityId : Constants.HOT_CITY_IDS){
					page = new Page();
					pageData.clear();
					pageData.put("cityid", hotCityId);
					page.setShowCount(15);
					page.setPd(pageData);
					serviceOrderList = serviceOrderService.serviceOrderlistPage(page);
					if (serviceOrderList != null && serviceOrderList.size() > 0) {
						ehcache.put(Constants.EHCACHE_STATIC_ANME, hotCityId, serviceOrderList);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}