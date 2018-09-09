package com.hjlc.util.utils;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.druid.util.StringUtils;

/**
* 用ConcurrentMap来缓存属性文件的key-value
*/

public class PropertiesUtil {
	private static ResourceLoader loader = ResourceLoader.getInstance();
	private static ConcurrentMap<String, String> configMap = new ConcurrentHashMap<String, String>();
	private static final String DEFAULT_CONFIG_FILE = "redis.properties";
	private static Properties p = null;
	
	/**
	 * 通过属性文件名和key获取对应的属性值
	 * @param key  属性文件中的关键字
	 * @param propertyFileName  属性文件路径+名
	 * @param defaultValue  默认值
	 * @return  属性值
	 */
	public static String getStringByKey(String key, String propertyFileName, String defaultValue){
		p = loader.getPropertiesFromPropertiesFile(propertyFileName);
		key = key.trim();
		String val = "";
		if (!configMap.containsKey(key)) {
			val = p.getProperty(key);
			if (StringUtils.isEmpty(val))
				val = defaultValue;
			else
				configMap.put(key, val);
		}else {
			val = configMap.get(key);
		}
		return val;
	}
	/**
	 * 获取默认的属性文件中KEY对应的属性值
	 * @param key  属性文件中的关键字
	 * @param defaultValue  默认值
	 * @return
	 */
	public static String getStringByKey(String key, String defaultValue) {
		return getStringByKey(key, DEFAULT_CONFIG_FILE, defaultValue);
	}
	/**
	 * 获取默认的属性文件中KEY对应的属性值
	 * @param key  属性文件中的关键字
	 * @param propertyFileName  属性文件路径+名
	 * @param defaultValue  默认值
	 * @return
	 */
	public static int getIntByKey(String key, String propertyFileName, int defaultValue) {
		String val = getStringByKey(key, propertyFileName, defaultValue + "");
		val = StringUtils.isEmpty(val) ? defaultValue + "" : val;
		return StringUtils.isEmpty(val) ? -1 : Integer.parseInt(val);
	}
	/**
	 * 获取默认的属性文件中KEY对应的属性值
	 * @param key  属性文件中的关键字
	 * @param defaultValue  默认值
	 * @return
	 */
	public static int getIntByKey(String key, int defaultValue) {
		return getIntByKey(key, DEFAULT_CONFIG_FILE, defaultValue);
	}
}
