package com.redis.accessor;

import java.util.Map;

import redis.clients.jedis.Jedis;

public class RedisAccessor extends AbstractRedisAccessor {

	/**
	 * incr 获取自增长值
	 * @param key
	 * @return
	 */
	public long getSequence(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doIt(Jedis jedis) {
				return jedis.incr(key);
			}
		});
	}
	
	/**
	 * set
	 * @param key
	 * @param value
	 */
	public String setBytes(final String key, byte[] value) {
		return execute(new RedisCallback<String>() {
			@Override
			public String doIt(Jedis jedis) {
				return jedis.set(key.getBytes(), value);
			}
		});
	}
	
	/**
	 * get
	 * @param key
	 */
	public byte[] getBytes(final String key) {
		return execute(new RedisCallback<byte[]>() {
			@Override
			public byte[] doIt(Jedis jedis) {
				return jedis.get(key.getBytes());
			}
		});
	}
	
	/**
	 * del 删除key
	 * @param keys
	 */
	public Long deleteKey(final String ...keys) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doIt(Jedis jedis) {
				return jedis.del(keys);
			}
		});
	}
	
	/**
	 * hget 获取byte数据
	 * @param key
	 * @param field
	 * @return
	 */
	public byte[] hgetBytes(final String key, final String field) {
		return execute(new RedisCallback<byte[]>() {
			@Override
			public byte[] doIt(Jedis jedis) {
				return jedis.hget(key.getBytes(), field.getBytes());
			}
		});
	}

	/**
	 * hgetAll 获取hset列表的全部内容
	 * @param key
	 * @return
	 */
	public Map<byte[], byte[]> hgetAllBytes(final String key) {
		return execute(new RedisCallback<Map<byte[], byte[]>>() {
			@Override
			public Map<byte[], byte[]> doIt(Jedis jedis) {
				return jedis.hgetAll(key.getBytes());
			}
		});
	}
	
	/**
	 * hset 保存byte数据
	 * @param key
	 * @param field
	 * @param date
	 * @return
	 */
	public Long hsetBytes(final String key, final String field, final byte[] date) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doIt(Jedis jedis) {
				return jedis.hset(key.getBytes(), field.getBytes(), date);
			}
		});
	}
	
	/**
	 * hmset 保存多个2key的值
	 * @param key
	 * @param values
	 * @return
	 */
	public Integer hmsetBytes(final String key, final Map<byte[], byte[]> values) {
		return execute(new RedisCallback<Integer>() {
			@Override
			public Integer doIt(Jedis jedis) {
				jedis.hmset(key.getBytes(), values);
				return 1;
			}
		});
	}
	
	/**
	 * hdel 删除byte数据
	 * @param key
	 * @param field
	 * @return
	 */
	public Long hdelBytes(final String key, final String field) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doIt(Jedis jedis) {
				return jedis.hdel(key.getBytes(), field.getBytes());
			}
		});
	}
}
