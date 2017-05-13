package com.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;

import com.util.PropertiesConfig;

public abstract class AbstractMysqlAccessor {

	private JdbcFactory jdbcFactory;
	
	protected <T> T execute(SimpleJdbcCallback<T> action) {
		return execute(action, true);
	}
	public boolean init(PropertiesConfig propertiesConfig) {
		jdbcFactory = new JdbcFactory();
		return jdbcFactory.init(propertiesConfig);
	}
	private <T> T execute(SimpleJdbcCallback<T> action, boolean flag) {
		Connection conn = null;
		try {
			conn = jdbcFactory.getConnection();
			T result = action.doIt(conn);
			conn.close();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setJdbcFactory(JdbcFactory jdbcFactory) {
		this.jdbcFactory = jdbcFactory;
	}

}

interface SimpleJdbcCallback<T> {
	T doIt(Connection conn) throws SQLException;
}
