package com.redis.accessor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.PropertiesConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public abstract class AbstractRedisAccessor {

	private static final Logger logger = LoggerFactory.getLogger(AbstractRedisAccessor.class);

	private RedisFactory redisFactory;
	
	/**
	 * 初始化redis
	 * @param propertiesConfig
	 * @return
	 */
	public boolean init(PropertiesConfig propertiesConfig) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(propertiesConfig.getInt("maxTotal", 50));
		jedisPoolConfig.setMaxIdle(propertiesConfig.getInt("maxIdle", 50));
		jedisPoolConfig.setMaxWaitMillis(propertiesConfig.getLong("maxWaitMillis", 1000));
		jedisPoolConfig.setTestOnBorrow(propertiesConfig.getBoolean("redis.pool.testOnBorrow"));
		JedisPool jedisPool = new JedisPool(jedisPoolConfig, 
				propertiesConfig.getString("redis.server.ip"), 
				propertiesConfig.getInt("redis.server.port"),
				propertiesConfig.getInt("redis.pool.timeout", 2000),
				propertiesConfig.getString("redis.auth", null));
		
		redisFactory = new RedisFactory();
		redisFactory.setJedisPool(jedisPool);
		
		return true;
	}

	protected <T> T execute(RedisCallback<T> action) {
		T result = null;
		Jedis jedis = null;
		try {
			jedis = redisFactory.getJedis();
			result = action.doIt(jedis);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("redis doIt error", e);
		} finally {
			if (jedis != null) {
				redisFactory.close(jedis);
			}
		}
		return result;
	}

	public RedisFactory getRedisFactory() {
		return redisFactory;
	}

	public void setRedisFactory(RedisFactory redisFactory) {
		this.redisFactory = redisFactory;
	}

}

interface RedisCallback<T> {
	T doIt(Jedis jedis);
}
