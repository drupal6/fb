package com.db;

import java.sql.Connection;

import com.db.pool.DBPoolMgr;

public class MainDao extends BaseDao {
	
	protected Connection openConn() {
		return DBPoolMgr.getMainConn();
	}
}