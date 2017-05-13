package com.db;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.db.accessor.MysqlAccessor;
import com.db.accessor.SQLUtil;
import com.util.PropertiesConfig;


public class MysqlJdbcTemplate {

	private static MysqlJdbcTemplate instance = new MysqlJdbcTemplate();
	
	public static MysqlJdbcTemplate getInstance() {
		return instance;
	}
	private static MysqlAccessor accessor;
	
	public static boolean init(PropertiesConfig propertiesConfig) {
		accessor = new MysqlAccessor();
		return accessor.init(propertiesConfig);
	}

	public static <T> T queryBean(Class<T> type, long id) {
		return null;
	}

	public static <T> List<T> batchQueryBean(Class<T> type, Set<Long> ids) {
		return null;
	}

	public static <T> int insert(Class<T> type, Map<String, String> beanMap) {
		String insertSql = SQLUtil.insert(beanMap, type.getSimpleName());
		return accessor.insert(insertSql);
	}

	public static <T> int update(Class<T> type, long id, Map<String, String> updateMap) {
		return 0;
	}

	public static <T> void delete(Class<T> type, long id) {

	}
}
