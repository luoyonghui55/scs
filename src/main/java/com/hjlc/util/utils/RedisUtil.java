package com.hjlc.util.utils;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * redis工具类，redis服务器登录密码：LGL$$$1688
 * redis相关信息：目前redis为1主2从，3个sentinel节点，都在一台服务器上
 * IP:139.199.112.173
 * 主节点端口：6688
 * 主节点密码：lGL1688
 * 2个从节点端口为：6689,6687
 * 3个sentinel节点IP和端口：139.199.112.173:26688,139.199.112.173:26689,139.199.112.173:26687
 */

public class RedisUtil {
	//private static String REDIS_IP;
	//public static final String REDIS_IP_DEFAULT = "39.106.8.174";//"192.168.1.186";
	//private static int REDIS_PORT;
	//public static final int REDIS_PORT_DEFAULT = 6688;//6379;
	private static String REDIS_PASSWORD;
	private static final String REDIS_PASSWORD_DEFAULT = "lGl1688";
	private static JedisSentinelPool redisSentinelPool;
	private static Jedis redis;
	/**
	 * redis是否可用,true:可用，false:不可用
	 */
	public static boolean redisIsUsable = true;
	
	static{
		/*REDIS_IP = PropertiesUtil.getStringByKey("redis.IP", REDIS_IP_DEFAULT);
		REDIS_PORT = PropertiesUtil.getIntByKey("redis.port", REDIS_PORT_DEFAULT);*/
		String redisSentinelIPAndPort = PropertiesUtil.getStringByKey("redis.sentinel", null);
		String sentinelMaster = PropertiesUtil.getStringByKey("redis.master", "lglMaster");
		String redisSentinelIPAndPortArray[] = redisSentinelIPAndPort.split(",");
		Set<String> redisSentinelSet = new HashSet<String>(redisSentinelIPAndPortArray.length);
		for (String ipAndPort : redisSentinelIPAndPortArray) {
			redisSentinelSet.add(ipAndPort);
		}
		REDIS_PASSWORD = PropertiesUtil.getStringByKey("redis.password", REDIS_PASSWORD_DEFAULT);
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(PropertiesUtil.getIntByKey("redis.MaxIdle", 200));
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		config.setTestWhileIdle(true);
		config.setMinEvictableIdleTimeMillis(60000l);
		config.setTimeBetweenEvictionRunsMillis(3000l);
		config.setMaxWaitMillis(PropertiesUtil.getIntByKey("redis.MaxWait", 5000));
		//redisPool = new JedisPool(config, REDIS_IP, REDIS_PORT, 0, REDIS_PASSWORD);
		//redis = redisPool.getResource();
		try {
			redisSentinelPool = new JedisSentinelPool(sentinelMaster, redisSentinelSet,config,REDIS_PASSWORD);
		} catch (Exception e) {
			System.err.println("\n\n=================>>>>>>>>>>>>>>>redis出现异常<<<<<<<<<<<<<<<<<<<<\n\n");
			redisIsUsable = false;
		}
		//redis = redisSentinelPool.getResource();
	}
	/**
	 * 获取redis对象
	 * @return
	 */
	public static Jedis getRedis(){
		if (redis == null && redisIsUsable) {
			initRedis();
		}
		return redis;
	}
	private static synchronized Jedis initRedis(){
		if (redis == null) {
			try {
				redis = redisSentinelPool.getResource();
			} catch (Exception e) {
				redisIsUsable = false;
				return null;
			}
		}
		return redis;
	}
	/**
	 * 关闭redis
	 * @param redis
	 */
	public static void closeRedis(Jedis redis){
		try {
			if (redis != null) {
				redis.close();
			}
		} catch (Exception e) {
			redis.quit();
			redis.disconnect();
			e.printStackTrace();
		}
	}
	/**
     * 获取字符串数据
     * @param key
     * @return
     */
	public static String get(String key){
		try {
			return redis.get(key);
		}finally {
			closeRedis(redis);
		}
	}
	/**
     * 获取字节数组数据
     * @param key
     * @return
     */
	public static byte[] get(byte[] key){
		byte[] val = null;
		try {
			val = redis.get(key);
		}finally {
			closeRedis(redis);
		}
		return val;
	}
	/**
     * 添加字节数组数据到缓存中
     * @param key
     * @param val
     * @return
     */
	public static void set(byte[] key, byte[] val){
		try {
			redis.set(key, val);
		}finally {
			closeRedis(redis);
		}
	}
	/**
     * 添加字节数组数据到缓存中
     * @param key
     * @param val
     * @param seconds 过期时间
     * @return
     */
	public static void setex(byte[] key, byte[] val, int seconds){
		try {
			redis.setex(key, seconds, val);
		}finally {
			closeRedis(redis);
		}
	}
	/**
     * 添加字节数组数据到缓存中
     * @param key
     * @param field
     * @param val
     * @return
     */
	public static void hset(byte[] key, byte[] field, byte[] val){
		try {
			redis.hset(key, field, val);
		}finally {
			closeRedis(redis);
		}
	}
	/**
     * 添加字节数组数据到缓存中
     * @param key
     * @param field
     * @param val
     * @return
     */
	public static void hset(String key, String field, String val){
		try {
			redis.hset(key,field, val);
		}finally {
			closeRedis(redis);
		}
	}
	/**
     * 获取字符串数据
     * @param key
     * @param field
     * @return
     */
	public static byte[] hget(byte[] key, byte[] field){
		byte[] val = null;
		try {
			val = redis.hget(key,field);
		}finally {
			closeRedis(redis);
		}
		return val;
	}
	/**
     * 获取字符串数据
     * @param key
     * @param field
     * @return
     */
	public static String hget(String key, String field){
		String val = "";
		try {
			val = redis.hget(key,field);
		}finally {
			closeRedis(redis);
		}
		return val;
	}
	/**
     * 
     * @param key
     * @param field
     * @return
     */
	public static void hdel(byte[] key, byte[] field){
		try {
			redis.hdel(key,field);
		}finally {
			closeRedis(redis);
		}
	}
	/**
     * 存储REDIS队列 顺序存储
     * @param key
     * @param val
     * @return
     */
	public static void lpush(byte[] key, byte[] val){
		try {
			redis.lpush(key,val);
		}finally {
			closeRedis(redis);
		}
	}
	/**
     * 存储REDIS队列 反向存储
     * @param key  
     * @param field
     * @return
     */
	public static void rpush(byte[] key, byte[] val){
		try {
			redis.rpush(key,val);
		}finally {
			closeRedis(redis);
		}
	}
}