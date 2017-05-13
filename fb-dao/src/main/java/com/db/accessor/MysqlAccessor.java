package com.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;

public class MysqlAccessor extends AbstractMysqlAccessor {

	private QueryRunner queryRunner;

	public MysqlAccessor() {
		queryRunner = new QueryRunner();
	}

	public <T> T queryBean(Class<T> type, long id) {
		return null;
	}

	public <T> List<T> batchQueryBean(Class<T> type, Set<Long> ids) {
		return null;
	}

	public int insert(String sql, Object... args) {
		return executeUpdate(sql, args);
	}

	public int update(String sql, Object... args) {
		return executeUpdate(sql, args);
	}

	public void delete(String sql, Object... args) {
		executeUpdate(sql, args);
	}

	public int executeUpdate(final String sql, final Object... args) {
		return execute(new SimpleJdbcCallback<Integer>() {
			@Override
			public Integer doIt(Connection conn) throws SQLException {
				return queryRunner.update(conn, sql, args);
			}
		});
	}

}
