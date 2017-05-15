package com.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.pool.DBPoolMgr;


public class DbWatch {

	private static final Logger logger = LoggerFactory.getLogger(DbWatch.class);
	
	private long first = 0;
	private long second = 0;

	public DbWatch() {
		first = System.currentTimeMillis();
	}

	public void getPool() {
		second = System.currentTimeMillis();
	}

	public void commit(String procName) {
		long end = System.currentTimeMillis();		
		long spendTime = end - first;
		if (spendTime > 1000) {
			logger.error(String.format("执行语句%s花耗时间总时间 超过:%sms,获取连接：%sms,执行sql:%sms", procName, spendTime, second - first, end - second));
			logger.error("main Pool:"+DBPoolMgr.getMainPoolState());			
		}
	}
}
