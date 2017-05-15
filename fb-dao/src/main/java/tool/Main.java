package tool;

import com.db.pool.DBPoolMgr;
import com.util.PropertiesConfig;

public class Main {

	// 表名
	private static String TABLE_NAME = "t_player_item";
	// 是否覆盖文件
	private static boolean IS_OVER_RIDE = true;
	// 是否生成proto文件
	private static boolean MAKE_PROTO = true;
	
	private static boolean ID_AUTOINCREATE = true;

	public static void main(String[] args) {
		// 类生成的路径
		String outPath = "com";
		init();
		new GenEntityAndDao(TABLE_NAME, outPath, IS_OVER_RIDE, MAKE_PROTO, ID_AUTOINCREATE);
	}

	private static void init() {
		PropertiesConfig config = new PropertiesConfig();
		config.load("./config/mysql.properties");
		DBPoolMgr.init(config);
	}
}
