package com.hjlc.util.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

public class MapUtil {  
	private static final double PI = 3.14159265; // 圆周率
	private static final double EARTH_RADIUS = 6378.137; // 地球半径
	//private static final double RAD = Math.PI / 180.0; // 一百八十度角
    
    private static double rad(double d) {  
        return d * Math.PI / 180.0;  
    }
    
    /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
     * 参数为String类型
     * @param lat1 用户经度
     * @param lng1 用户纬度
     * @param lat2 商家经度
     * @param lng2 商家纬度
     * @return
     */
    public static String getDistance(String lat1Str, String lng1Str, String lat2Str, String lng2Str) {
    	Double lat1 = Double.parseDouble(lat1Str);
    	Double lng1 = Double.parseDouble(lng1Str);
    	Double lat2 = Double.parseDouble(lat2Str);
    	Double lng2 = Double.parseDouble(lng2Str);
    	double patm = 2;
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double difference = radLat1 - radLat2;
        double mdifference = rad(lng1) - rad(lng2);
        double distance = patm * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / patm), patm)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(mdifference / patm), patm)));
        distance = distance * EARTH_RADIUS;
        String distanceStr = String.valueOf(distance);
        return distanceStr;
    }
    
    /**
	 * 获取当前用户一定距离以内的经纬度值
	 * 单位米 return minLat 
	 * 最小经度 minLng 
	 * 最小纬度 maxLat 
	 * 最大经度 maxLng 
	 * 最大纬度 minLat
	 */
	public static Map<String, Double> getAround(String latStr, String lngStr, String raidus) {
		Map<String, Double> map = new HashMap<String, Double>(6);
		
		Double latitude = Double.parseDouble(latStr);// 传值给经度
		Double longitude = Double.parseDouble(lngStr);// 传值给纬度

		Double degree = (24901 * 1609) / 360.0; // 获取每度
		double raidusMile = Double.parseDouble(raidus);
		
		Double mpdLng = Double.parseDouble((degree * Math.cos(latitude * (Math.PI / 180))+"").replace("-", ""));
		Double dpmLng = 1 / mpdLng;
		Double radiusLng = dpmLng * raidusMile;
		//获取最小经度
		Double minLat = longitude - radiusLng;
		// 获取最大经度
		Double maxLat = longitude + radiusLng;
		
		Double dpmLat = 1 / degree;
		Double radiusLat = dpmLat * raidusMile;
		// 获取最小纬度
		Double minLng = latitude - radiusLat;
		// 获取最大纬度
		Double maxLng = latitude + radiusLat;
		
		map.put("minLat", minLat);
		map.put("maxLat", maxLat);
		map.put("minLng", minLng);
		map.put("maxLng", maxLng);
		return map;
	}
	/**
	 * @param raidus
	 * 单位米 return minLat 
	 * 最小经度 minLng 
	 * 最小纬度 maxLat 
	 * 最大经度 maxLng 
	 * 最大纬度 minLat
	 */
	public static double[] getAround(double lat, double lon, int raidus) {
		Double latitude = lat;// 传值给经度
		Double longitude = lon;// 传值给纬度

		Double degree = (24901 * 1609) / 360.0; // 获取每度
		double raidusMile = raidus;

		Double dpmLat = 1 / degree;
		Double radiusLat = dpmLat * raidusMile;
		// 获取最小纬度
		Double minLat = latitude - radiusLat;
		// 获取最大纬度
		Double maxLat = latitude + radiusLat;

		Double mpdLng = degree * Math.cos(latitude * (PI / 180));
		Double dpmLng = 1 / mpdLng;
		Double radiusLng = dpmLng * raidusMile;
		//获取最小经度
		Double minLng = longitude - radiusLng;
		// 获取最大经度
		Double maxLng = longitude + radiusLng;
		System.out.println("jingdu" + minLat + "weidu" + minLng + "zuidajingdu"	+ maxLat + "zuidaweidu" + maxLng);

		return new double[] { minLat, minLng, maxLat, maxLng };
	}
	
	/*public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {    
        if (map == null)
            return null;
        Object obj = beanClass.newInstance();
        BeanUtils.populate(obj, map);
        return obj;  
    }   */ 
    
	/**
	 * 删除键值对
	 * @param map  要删除的MAP
	 * @param key  要删除的KEY
	 */
	public static void removeKey(Map<?,?> map, Object...keys){
		if (map != null && !map.isEmpty()) {
			for (Object k : keys) {
				map.remove(k);
			}
		}
	}
}
