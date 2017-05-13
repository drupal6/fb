package jsontool.common;

public class StringUtil {
	
	/**
	 * 头字符大写
	 * @param str
	 * @return
	 */
	public static String firstUpCase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
	}
	
	/**
	 * 头字符小写
	 * @param str
	 * @return
	 */
	public static String firstDownCase(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
	}
	
	/**
	 * 全部小写
	 * @param str
	 * @return
	 */
	public static String downCase(String str) {
		return str.toLowerCase();
	}
	
	/**
	 * 全部大写
	 * @param str
	 * @return
	 */
	public static String upCase(String str) {
		return str.toUpperCase();
	}

}
