package com.hjlc.util.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 资源加载器
 */
public final class ResourceLoader {
	private static ResourceLoader resourceLoader = new ResourceLoader();
	private static Map<String, Properties> loaderMap = new HashMap<String, Properties>();
	
	private ResourceLoader() {
		
	}
	public static ResourceLoader getInstance(){
		return resourceLoader;
	}
	/**
	 * 从Properties属性配置文件获取Properties对象
	 * @param fileName  属性文件配置路径
	 * @return
	 */
	public Properties getPropertiesFromPropertiesFile(String fileName){
		Properties p = loaderMap.get(fileName);
		try {
			if (p != null) {
				return p;
			}
			String filePath = null;
			String confPath = System.getProperty("resources");
			if (confPath == null) {
				filePath = this.getClass().getClassLoader().getResource(fileName).getPath();
			}else {
				filePath = confPath + "/" + fileName;
			}
			p = new Properties();
			p.load(new FileInputStream(new File(filePath)));
			loaderMap.put(fileName, p);
		} catch (FileNotFoundException e) {
			p = null;
			e.printStackTrace();
		} catch (IOException e) {
			p = null;
			e.printStackTrace();
		}
		return p;
	}
}