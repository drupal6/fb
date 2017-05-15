package com.db.pool;

import java.sql.Connection;

import com.util.PropertiesConfig;

/**
 * 数据库连接池管理类
 * 
 */
public class DBPoolMgr {
	
	private static PropertiesConfig mainConfig;
	private static IDBPool mainPool;

	public static boolean init(PropertiesConfig config) {
		mainConfig = config;
		return initPool();
	}


	public static boolean initPool() {
		if(mainConfig == null) {
			return false;
		}
		mainPool = createPools(mainConfig);
		return true;
	}

	/**
	 * 检查连接池状态是否挂掉，如挂了重新初始化
	 * 
	 * @param checkStrategy
	 *            是否检查游戏库
	 * @param checkLog
	 *            是否检查日志库
	 */
	public static void checkConnectionPool() {
		boolean initStrategy = false;
		if (mainPool == null || mainPool.getCurConns() <= 0) {
			initStrategy = true;
		}

		if (initStrategy) {
			initPool();
		}
	}

	/**
	 * 根据指定属性创建连接池实例.
	 * 
	 * @param props
	 *            连接池属性
	 */
	private static IDBPool createPools(PropertiesConfig mainConfig) {
		try {
			IDBPool pool = new BoneCPDBPool(mainConfig);
			return pool;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void closePools() {
		if (mainPool != null) {
			mainPool.shutdown();
			mainPool = null;
		}
	}
	
	public static void closeMainPools() {
		if (mainPool != null) {
			mainPool.shutdown();
			mainPool = null;
		}
	}

	public static Connection getMainConn() {
		Connection conn = mainPool.getConnection();
		return conn;
	}

	public static String getMainPoolState() {
		if (mainPool != null) {
			return mainPool.getState();
		}
		return null;
	}
	
	public static PropertiesConfig getMainConfig() {
		return mainConfig;
	}
}
