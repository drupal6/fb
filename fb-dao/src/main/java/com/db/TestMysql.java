package com.db;
import com.bean.game.PlayerItem;
import com.util.PropertiesConfig;

public class TestMysql {

	public static void main(String[] args) {
		PropertiesConfig mysqlConfig = new PropertiesConfig();
		mysqlConfig.init("F:/fb/fb-dao/config/mysql.properties");
		MysqlJdbcTemplate.init(mysqlConfig);
		PlayerItem playerItem1 = MysqlJdbcTemplate.queryBean(PlayerItem.class, 1);
		System.out.println(playerItem1.getId() + "<>" + playerItem1.getType());
	}

}
