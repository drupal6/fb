package com.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.PropertiesConfig;

public class JdbcFactory {

	private static final Logger logger = LoggerFactory.getLogger(JdbcFactory.class);

	protected BasicDataSource datasource;

	/**
	 * 初始化
	 * @param propertiesConfig
	 * @return
	 */
	public boolean init(PropertiesConfig propertiesConfig) {
		datasource = new BasicDataSource();
		datasource.setDriverClassName(propertiesConfig.getProperty("mysql-jdbc-driver"));
		datasource.setUrl(propertiesConfig.getProperty("mysql-url"));
		datasource.setUsername(propertiesConfig.getProperty("mysql-username"));
		datasource.setPassword(propertiesConfig.getProperty("mysql-password"));
		datasource.setMaxTotal(propertiesConfig.getInt("mysql-maxtotal"));
		datasource.setMaxIdle(propertiesConfig.getInt("mysql-maxidle"));
		datasource.setMaxWaitMillis(propertiesConfig.getLong("mysql-maxwait"));
		datasource.setTestOnBorrow(propertiesConfig.getBoolean("mysql-testonborrow"));
		return true;
	}
	
	public void setDatasource(BasicDataSource datasource) {
		this.datasource = datasource;
	}

	public Connection getConnection() {
		try {
			return datasource.getConnection();
		} catch (SQLException e) {
			logger.error("getConnection error", e);
		}
		return null;
	}

}
