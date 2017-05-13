package com.redis;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.DataObject;
import com.google.protobuf.GeneratedMessage;
import com.redis.accessor.RedisAccessor;
import com.util.PropertiesConfig;



public class RedisTemplateMgr  {

	private static final Logger logger = LoggerFactory.getLogger(RedisTemplateMgr.class);

	private static RedisAccessor redisAccessor;
	
	public static boolean init(PropertiesConfig propertiesConfig) {
		redisAccessor = new RedisAccessor();
		return redisAccessor.init(propertiesConfig);
	}

	public static <T extends DataObject> String hset(String key, T value) {
		return redisAccessor.setBytes(key, value.toByteArray());
	}
	
	public static String hset(String key, String value) {
		return redisAccessor.setBytes(key, value.getBytes());
	}
	
	public static <T extends DataObject> T get(String key, Class<T> clzss) {
		byte[] data = redisAccessor.getBytes(key);
		return tnewInstance(clzss, data);
	}
	
	public static String get(String key) {
		byte[] data = redisAccessor.getBytes(key);
		if(data == null) {
			return null;
		}
		return new String(data);
	}
	
	public static <T extends GeneratedMessage> Long hset(String key, String field, T value) {
		return redisAccessor.hsetBytes(key, field, value.toByteArray());
	}
	
	public static Long hset(String key, String field, String value) {
		return redisAccessor.hsetBytes(key, field, value.getBytes());
	}
	
	public static <T extends GeneratedMessage> Integer hmset(String key, Map<String, T> data) {
		Map<byte[], byte[]> value = new HashMap<>();
		for(Entry<String, T> entry : data.entrySet()) {
			value.put(entry.getKey().getBytes(), entry.getValue().toByteArray());
		}
		return redisAccessor.hmsetBytes(key, value);
	}
	
	public static Integer hmsetString(String key, Map<String, String> data) {
		Map<byte[], byte[]> value = new HashMap<>();
		for(Entry<String, String> entry : data.entrySet()) {
			value.put(entry.getKey().getBytes(), entry.getValue().getBytes());
		}
		return redisAccessor.hmsetBytes(key, value);
	}
	
	public static <T extends DataObject> T hget(String key, String field, Class<T> clzss) {
		byte[] data = redisAccessor.hgetBytes(key, field);
		return tnewInstance(clzss, data);
	}
	
	public static String hget(String key, String field) {
		byte[] data = redisAccessor.hgetBytes(key, field);
		if(data == null) {
			return null;
		}
		return new String(data);
	}
	
	public static <T extends DataObject> Map<String, T> hgetAll(String key, String field, Class<T> clzss) {
		Map<byte[], byte[]> data = redisAccessor.hgetAllBytes(key);
		if(data == null) {
			return null;
		}
		Map<String, T> result = new HashMap<String, T>();
		for(Entry<byte[], byte[]> entry : data.entrySet()) {
			result.put(new String(entry.getKey()), tnewInstance(clzss, entry.getValue()));
		}
		return result;
	}
	
	public static Map<String, String> hgetAll(String key, String field) {
		Map<byte[], byte[]> data = redisAccessor.hgetAllBytes(key);
		if(data == null) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for(Entry<byte[], byte[]> entry : data.entrySet()) {
			result.put(new String(entry.getKey()), new String(entry.getValue()));
		}
		return result;
	}

	public static Long hdel(String key, String field) {
		return redisAccessor.hdelBytes(key, field);
	}
	
	public static Long del(String ...keys) {
		return redisAccessor.deleteKey(keys);
	}
	
	private static <T extends DataObject> T tnewInstance(Class<T> clzss, byte[] data) {
		if(data == null){
			return null;
		}
		T t = null;
		try {
			Constructor<T> clzss1 = clzss.getConstructor(new Class[]{int.class});   
			clzss1.setAccessible(true);   
            t =(T)clzss1.newInstance(new Object[]{0});
			Method parseFrom = clzss.getMethod("parseFrom", byte[].class);
			parseFrom.invoke(t, data);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("clazz new instance error. class:"+clzss.getName());
		}
		return t;
	}
}
