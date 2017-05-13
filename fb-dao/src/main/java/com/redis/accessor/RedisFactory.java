package com.redis.accessor;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis工厂类
 */
public class RedisFactory {
	
	private JedisPool jedisPool;

	/**
	 * 获取redis实例
	 */
	public Jedis getJedis() {
		return jedisPool.getResource();
	}

	/**
	 * 关闭redis
	 */
	public void close(Jedis jedis) {
		jedis.close();
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

}
