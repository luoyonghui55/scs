package com.hjlc.userIndex.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hjlc.base.dao.DaoSupport;
import com.hjlc.util.utils.PageData;

/**
 * 地址处理service
 */

@Service("addressService")
public class AddressService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**
	 * 获取省份信息列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	//@Cacheable(value="staticDataCache", key=Constants.EHCACHE_ALL_PROVINCES_KEY)
	//@Cacheable(value="staticDataCache", key="#pd", condition="")
	public List<PageData> listAllProvinces(PageData pd)throws Exception{
		return (List<PageData>) dao.findForList("AddressMapper.listAllProvinces", pd);
	}
	/**
	 * 获取城市信息列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	//@Cacheable(value="staticDataCache", key=Constants.EHCACHE_ALL_CITIES_KEY)
	public List<PageData> listAllCities(PageData pd)throws Exception{
		return (List<PageData>) dao.findForList("AddressMapper.listAllCities", pd);
	}
	/**
	 * 获取地区，县、市信息列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	//@Cacheable(value="staticDataCache", key=Constants.EHCACHE_ALL_AREA_KEY)
	public List<PageData> listAllArea(PageData pd)throws Exception{
		return (List<PageData>) dao.findForList("AddressMapper.listAllArea", pd);
	}
	/**
	 * 通过城市名获取城市ID
	 * @param cityName   城市名
	 * @return
	 * @throws Exception
	 */
	public String findCityIdByName(String cityName)throws Exception{
		return (String) dao.findForObject("AddressMapper.findCityIdByName", cityName);
	}
}
/**  
 * http://www.open-open.com/doc/view/761ee08991dc4ba1acb3e2824fed0e28
 * Cacheable注解负责将方法的返回值加入到缓存中  
 * CacheEvict注解负责清除缓存(它的三个参数与@Cacheable的意思是一样的)  
 * @see -----------------------------------------------------------
 * @see value------缓存位置的名称,不能为空,若使用EHCache则其值为ehcache.xml中的<cache name="myCache"/>
 * @see key--------缓存的Key,默认为空(表示使用方法的参数类型及参数值作为key),支持SpEL  
 * @see condition--只有满足条件的情况才会加入缓存,默认为空(表示全部都加入缓存),支持SpEL  
 * @see ------------------------------------------------------
 * @see 该注解的源码位于spring-context-3.2.4.RELEASE-sources.jar中  
 * @see Spring针对Ehcache支持的Java源码位于spring-context-support-3.2.4.RELEASE-sources.jar中  
 */  