package com.hjlc.util.utils;

import java.util.List;

import com.alibaba.druid.util.StringUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * ehcache缓存工具类
 */
public class EhcacheUtil {
	//指定ehcache配置文件
	private static final String ehcachePath = "resources/ehcache.xml";
	private CacheManager cacheManager;
	private static EhcacheUtil ehcacheUtil;
	
	private EhcacheUtil(String ehcachePath){
		cacheManager = CacheManager.create(ehcachePath);
	}
	/**
	 * 获取EhcacheUtil实例对象
	 * @return
	 */
	public static EhcacheUtil getInstance(){
		if (ehcacheUtil == null) {
			ehcacheUtil = newInstance();
		}
		return ehcacheUtil;
	}
	private static synchronized EhcacheUtil newInstance(){
		if (ehcacheUtil == null) {
			ehcacheUtil = new EhcacheUtil(ehcachePath);
		}
		return ehcacheUtil;
	}
	/**
	 * 将键值对存入缓存中
	 * @param cacheName  Ehcache配置文件中的缓存名称
	 * @param key  要存入缓存中的键
	 * @param value  要存入缓存中的值
	 */
	public void put(String cacheName, Object key, Object value) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			/**
			 * 该类设计为JAVA工程中单独使用，旨在保存一些常用不常修改的数据
			 * 1、第一个参数：Ehcache配置文件中的缓存名称
			 * 2、第二个参数：允许内存中缓存对象的大小
			 * 3、第三个参数：允许在达到最大内存里写入磁盘
			 * 4、第四个参数：表示永久保存
			 * 5、最后两个参数：表示ELMENT存活时间无穷大
			 */
			cache = new Cache(cacheName, 1000000, true, true, 0 ,0);
			cacheManager.addCache(cache);
		}
		Element element = new Element(key, value);
		cache.put(element);
	}
	/**
	 * 通过cacheName名和键获取对应的缓存中的值
	 * @param cacheName  缓存名称
	 * @param key  缓存中的键
	 * @return
	 */
	public Object getValue(String cacheName, Object key){
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			Element element = cache.get(key);
			return element == null ? null : element.getObjectValue();
		}
		return null;
	}
	/**
	 * 通过缓存对象名称获取对象
	 * @param cacheName
	 * @return
	 */
	public Cache getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}
	/**
	 * 从缓存中删除某个键值对Element对象
	 * @param cacheName
	 * @param key
	 */
	public void removeElement(String cacheName, Object key) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.remove(key);
		}
	}
	/**
	 * 删除某个缓存对象
	 * @param cacheName
	 */
	public void removeCache(String cacheName) {
		cacheManager.removeCache(cacheName);
	}
	/**
	 * 从缓存中通过省份ID获取该省份下的所有城市
	 * @param provinceId
	 * @return
	 */
	public List<PageData> forCacheGetCitiesByProvinceId(String provinceId) {
		//List<PageData> ci
		return null;
	}
	/**
	 * 从缓存中通过城市ID获取该城市下的所有区县
	 * @param provinceId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> forCacheGetAreaByCityId(String cityId) {
		if (!StringUtils.isEmpty(cityId)) {
			Object obj = getValue(Constants.EHCACHE_STATIC_ANME, Constants.CACHE_AREA_KEY_PREFIX + cityId);
			if (obj != null) {
				return (List<PageData>)obj;
			}
		}
		return null;
	}
}