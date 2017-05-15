package com.db;

import java.sql.Types;
import java.util.Map;

public class ParameterHelper {

	private String sqlStr;
	private String data;
	private Map<Integer, DbParameter> para;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ParameterHelper(String sqlStr, String data, Map<Integer, DbParameter> para) {
		this.sqlStr = sqlStr;
		this.data = data;
		this.para = para;
		paramSet();
	}

	public String getSqlStr() {
		return sqlStr;
	}

	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}

	public Map<Integer, DbParameter> getPara() {
		return para;
	}

	public void setPara(Map<Integer, DbParameter> para) {
		this.para = para;
	}

	private void paramSet() {
		if (this.data != null && data.length() > 0) {
			String[] paras = this.data.split(",");
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < paras.length; i++) {
				para.put((i + 1), new DbParameter(Types.INTEGER, Integer.valueOf(paras[i])));
				str.append("?,");
			}
			setSqlStr(this.sqlStr.replace("#", str.substring(0, str.length() - 1)));

		}
	}

}
