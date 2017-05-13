package jsontool.json;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import jsontool.excel.OperatingExcel;

public abstract class ETJson {

	public static String SEPARATOR = "_";

	public abstract void init() throws IOException;

	/**
	 * 
	 * @param file
	 * @param sheetname
	 * @param keyRow 关键字第几行（从1开始）
	 * @param dataRow 数据第几行（从1开始）
	 * @return
	 */
	public List<Map<String, String>> readExcelAsList(String file, String sheetname, int keyRow, int dataRow) throws IOException {
		System.out.println(new File(file).getAbsolutePath());
		OperatingExcel oe = new OperatingExcel();
		List<Map<String, String>> dataList;
		dataList = oe.readExcelAsList(file, sheetname, keyRow, dataRow);
		return dataList;
	}

	public Gson getGson() {
		return new GsonBuilder().setPrettyPrinting().create();
	}

	protected <T> String getConfig(String filePath, String sheetName, Class<T> class1, String... keys) {
		OperatingExcel oe = new OperatingExcel();
		List<Map<String, String>> ybShopList = oe.readExcelAsList(filePath, sheetName, 3, 4);
		Type listOfTestObject = new TypeToken<Map<String, T>>() {}.getType();
		Gson gson = getGson();
		Map<String, T> result = new TreeMap<String, T>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				String[] o1s = o1.split(SEPARATOR);
				String[] o2s = o2.split(SEPARATOR);
				try {
					int o1i = Integer.parseInt(o1s[0]);
					int o2i = Integer.parseInt(o2s[0]);
					if (o1i >= o2i) {
						return 1;
					} else {
						return -1;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});

		for (Map<String, String> map : ybShopList) {
			boolean con = false;
			for (String key : keys) {
				if (map.get(key.toLowerCase()) == null || map.get(key.toLowerCase()).trim().equals("")) {
					con = true;
					break;
				}
			}
			if (con == true) {
				break;
			}
			T data;
			try {
				data = class1.newInstance();
				Field[] attributes = data.getClass().getDeclaredFields();
				for (Field field : attributes) {
					String v = field.getName();
					try {
						BeanUtils.setProperty(data, v, castValueType(field, map.get(v.toLowerCase())));
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
				StringBuffer id = new StringBuffer();
				for (int i = 0; i < keys.length; i++) {
					id.append(BeanUtils.getProperty(data, keys[i]));
					if (i < keys.length - 1) {
						id.append(SEPARATOR);
					}
				}
				result.put(id.toString(), data);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return gson.toJson(result, listOfTestObject);
	}

	public Object castValueType(Field field, String value) {
		if (field.getType() == Integer.TYPE) {
			if (value == null || value.trim().isEmpty()) {
				return 0;
			}
			try {
				Integer i = Integer.parseInt(value);
				return i.intValue();
			} catch (Exception e) {
				Double d = Double.parseDouble(value);
				return d.intValue();
			}
		}
		if (field.getType() == Float.TYPE) {
			if (value == null || value.trim().isEmpty()) {
				return 0;
			}
			Double d = Double.parseDouble(value);
			return d.floatValue();
		}
		if (field.getType() == Long.TYPE) {
			if (value == null || value.trim().isEmpty()) {
				return 0;
			}
			Double d = Double.parseDouble(value);
			return d.longValue();
		}
		if (field.getType() == Double.TYPE) {
			if (value == null || value.trim().isEmpty()) {
				return 0;
			}
			Double d = Double.parseDouble(value);
			return d.doubleValue();
		}
		return extendValueType(field, value);
	}

	public Object extendValueType(Field field, String value) {
		return value;
	}

	/**
	 * 将特殊的字符串转换成json字符串
	 * 
	 * @param str
	 *            {1:test,2:hello}
	 * @return {"1":"test","2":"hello"}
	 */
	public String toJsonString(String str) {
		String[] args = str.split(":");
		if (args.length > 1) {
			StringBuffer buff = new StringBuffer();
			String[] units = str.split(",");
			for (int i = 0; i < units.length; i++) {
				String unit = units[i];
				buff.append(unit.replace("{", "{\"").replaceFirst(":", "\":\"").replace("}", "\"}"));
				if (i != units.length - 1) {
					buff.append("\",\"");
				}
			}
			return buff.toString();
		}
		return str;
	}

	public String getGoTo(String value) {
		if (value.equalsIgnoreCase("null")) {
			return null;
		}
		if (value.equalsIgnoreCase("无跳转")) {
			return null;
		}
		return value;
	}

	protected <T> String getConfig(String filePath, String sheetName, Class<T> class1) {
		System.out.println(class1);
		OperatingExcel oe = new OperatingExcel();
		List<Map<String, String>> ybShopList = oe.readExcelAsList(filePath, sheetName, 3, 4);
		Type listOfTestObject = new TypeToken<List<T>>() {}.getType();
		Gson gson = getGson();
		List<T> result = new ArrayList<T>();
		for (Map<String, String> map : ybShopList) {
			boolean con = true;
			for (String key : map.keySet()) {
				if (!map.get(key).trim().equals(""))
					con = false;
				break;
			}
			if (con == true) {
				break;
			}
			try {
				T data;
				data = class1.newInstance();
				Field[] attributes = data.getClass().getDeclaredFields();
				for (Field field : attributes) {
					String v = field.getName();
					try {
						BeanUtils.setProperty(data, v, castValueType(field, map.get(v.toLowerCase())));
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
				result.add(data);
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
		return gson.toJson(result, listOfTestObject);
	}
}
