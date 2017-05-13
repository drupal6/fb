//package tool;
//
//public class Main {
//
//	// 数据库(db_sz_base,db_sz_main,db_sz_log)
//	private static String DATA_BASE = "db_sz_main";
//	// 表名
//	private static String TABLE_NAME = "t_player_item";
//	// 是否覆盖文件
//	private static boolean IS_OVER_RIDE = true;
//	// 是否生成proto文件
//	private static boolean MAKE_PROTO = true;
//
//	public static void main(String[] args) {
//		// 类生成的路径
//		String outPath = "com.ly";
//		init();
//		new GenEntityAndDao(DATA_BASE, TABLE_NAME, outPath, IS_OVER_RIDE, MAKE_PROTO);
//	}
//
//	private static void init() {
////		ServerConfigUtil.init("..\\Lib\\config\\GameServerConfig.properties");
////		NetConfigUtil.init();
////		DBPoolMgr.init();
//	}
//}
