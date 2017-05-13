package jsontool.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jsontool.common.Pair;

public class OperatingExcel {

	private static final Logger logger = LoggerFactory
			.getLogger(OperatingExcel.class);

	/**
	 * @param filePath excel路径
	 * @param sheetName sheet名
	 * @param titleRow 标题行（从1开始）
	 * @param begin 正式内容开始行（从1开始）
	 * @return
	 */
	public List<Map<String, String>> readExcelAsList(String filePath, String sheetName, int titleRow, int begin) {
		titleRow--;
		begin--;
		//
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		InputStream is = null;
		XSSFWorkbook wb = null;
		try {
			is = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("未找到指定Excel文件");
			logger.error("未找到指定Excel文件 {}", filePath);
			logger.error("", e);

		}
		try {
			wb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		XSSFSheet sheet1 = wb.getSheet(sheetName);
		if (sheet1 == null) {
			System.err.println(filePath + " sheet is null. sheetName:"
					+ sheetName);
			return list;
		}
		XSSFRow title = sheet1.getRow(titleRow);
		if (title == null) {
			System.err.println(filePath + " row is null. titleRow:" + titleRow);
			return list;
		}
		int rowCount = sheet1.getLastRowNum();
		int colNum = title.getLastCellNum();
		int index = 0;
		for (int i = begin; i <= rowCount; i++) {
			Map<String, String> cellMap = new HashMap<String, String>();
			boolean allempty = true;
			XSSFRow valueRow = sheet1.getRow(i);
			if (valueRow == null) {
				continue;
			}
			for (int j = 0; j < colNum; j++) {
				XSSFCell titleCell = title.getCell(j);
				if (titleCell == null) {
					continue;
				}
				String titlString = titleCell.getStringCellValue();
				XSSFCell valueCell = valueRow.getCell(j);
				String valueString = getStringByCell(valueCell);
				if (titlString != null && !titlString.trim().isEmpty()) {
					allempty = false;
					cellMap.put(titleCell.getStringCellValue().trim()
							.toLowerCase(), valueString);
				}
			}
			if (!allempty) {
				list.add(index, cellMap);
				index++;
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 读取头部消息和数据类型
	 * @param filePath
	 * @param sheetName
	 * @param titleRow
	 * @return
	 */
	public List<Pair<Integer, String>> readTitle(String filePath, String sheetName, int titleRow) {
		titleRow--;
		List<Pair<Integer, String>> list = new ArrayList<Pair<Integer, String>>();
		InputStream is = null;
		XSSFWorkbook wb = null;
		try {
			is = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("未找到指定Excel文件 {}, exception:{}", filePath, e);
		}
		try {
			wb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		XSSFSheet sheet1 = wb.getSheet(sheetName);
		if (sheet1 == null) {
			System.err.println(filePath + " sheet is null. sheetName:"
					+ sheetName);
			return list;
		}
		XSSFRow row = sheet1.getRow(titleRow);
		int valueRowIndex = titleRow + 1;
		XSSFRow valueRow = sheet1.getRow(valueRowIndex);
		if (row == null || valueRow == null) {
			System.err.println(filePath + " row is null. titleRow:" + titleRow);
			return list;
		}
		int num = row.getLastCellNum();
		int valueType = XSSFCell.CELL_TYPE_STRING;
		for (int i = 0; i < num; i++) {
			Cell titleCell = row.getCell(i);
			Cell valueCell = valueRow.getCell(i);
			if (valueCell != null) {
				valueType = valueCell.getCellType();
			}
			String title = titleCell.getStringCellValue().trim();
			if (title != null && !title.trim().isEmpty()) {
					list.add(new Pair<Integer, String>(valueType, title));
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Pair<Integer, String>> readTitle(String filePath, String sheetName, int titleRow, int begin) {
		titleRow--;
		begin--;
		//
		List<Pair<Integer, String>> list = new ArrayList<Pair<Integer, String>>();
		InputStream is = null;
		XSSFWorkbook wb = null;
		try {
			is = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("未找到指定Excel文件");
			logger.error("未找到指定Excel文件 {}", filePath);
			logger.error("", e);

		}
		try {
			wb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		XSSFSheet sheet1 = wb.getSheet(sheetName);
		if (sheet1 == null) {
			System.err.println(filePath + " sheet is null. sheetName:"
					+ sheetName);
			return list;
		}
		XSSFRow title = sheet1.getRow(titleRow);
		if (title == null) {
			System.err.println(filePath + " row is null. titleRow:" + titleRow);
			return list;
		}
		int rowCount = sheet1.getLastRowNum();
		int colNum = title.getLastCellNum();
		for (int j = 0; j < colNum; j++) {
			Cell titleCell = title.getCell(j);
			String titlString = titleCell.getStringCellValue().trim();
			if (titlString == null || titlString.trim().isEmpty()) {
				continue;
			}
			int valueType = -1;
			for (int i = begin; i <= rowCount; i++) {
				XSSFRow valueRow = sheet1.getRow(i);
				if (valueRow == null) {
					continue;
				}
				XSSFCell valueCell = valueRow.getCell(j);
				if (valueCell == null) {
					valueType = 5; // 没有得话默认是string
					continue;
				}
				int newValueType = getCellValueType(valueCell);
				valueType = mergeValueType(valueType, newValueType);
			}
			list.add(new Pair<Integer, String>(valueType, titlString));
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 0 int 1 float 2 long 3 double 4 boolean 5 string 6 Map
	 * 
	 * @param valueCell
	 * @return
	 */
	private int getCellValueType(XSSFCell valueCell) {
		switch (valueCell.getCellType()) {
		case XSSFCell.CELL_TYPE_NUMERIC:
			// 只处理了 int 和 double
			Double d = valueCell.getNumericCellValue();
			int i = d.intValue();
			if (d > i) {
				return 3;
			}
			return 0;
		case XSSFCell.CELL_TYPE_STRING:
			String valueString = valueCell.getStringCellValue();
			if (valueString == null) {
				return 5;
			}
			if (valueString.trim().equals("false")
					|| valueString.trim().equals("true")) {
				return 4;
			}
			Pattern pattern = Pattern.compile("^\\{.*\\}$");
			Matcher matcher = pattern.matcher(valueString);
			if (matcher.matches()) {
				return 6;
			}
			return 5;
		case XSSFCell.CELL_TYPE_FORMULA:
			CTCell ctCell = null;
			try {
				ctCell = valueCell.getCTCell();
				if (ctCell == null) {
					return -1;
				}
			} catch (Exception e) {
				return 5;
			}
			try {
				Double d1 = Double.parseDouble(ctCell.getV());
				int i1 = d1.intValue();
				if (d1 > i1) {
					return 3;
				}
				return 0;
			} catch (Exception e) {
				return 5;
			}
		case XSSFCell.CELL_TYPE_BOOLEAN:
			return 4;
		default:
			return 5;
		}
	}

	/**
	 * 0 int 1 float 2 long 3 double 4 boolean 5 string 6 Map 类型合并
	 * 
	 * @param oldValueType
	 * @param newValueType
	 * @return
	 */
	private int mergeValueType(int oldValueType, int newValueType) {
		if (oldValueType == -1) {
			return newValueType;
		}
		// Map类型优先
		if (oldValueType == 6 && newValueType == 6) {
			return oldValueType;
		}
		// 优先字符串类型
		if (newValueType == 5) {
			return newValueType;
		}
		if (oldValueType == 5) {
			return oldValueType;
		}
		if (oldValueType == 3 && newValueType == 0) {
			return oldValueType;
		}
		return newValueType;
	}

	public Map<String, Map<String, String>> readExcelAsMap(String filePath,
			String sheetName, int titleRow, int begin) {
		titleRow--;
		begin--;
		//
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		InputStream is = null;
		XSSFWorkbook wb = null;
		try {
			is = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("所指路径未找到指定Excel文件");
		}
		try {
			wb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		XSSFSheet sheet1 = wb.getSheet(sheetName);
		int rowCount = sheet1.getLastRowNum();
		XSSFRow title = sheet1.getRow(titleRow);
		int colCount = title.getLastCellNum();
		for (int i = begin; i < rowCount; i++) {
			Map<String, String> cellMap = new HashMap<String, String>();
			XSSFRow valueRow = sheet1.getRow(i);
			for (int j = 0; j < colCount; j++) {
				XSSFCell titleCell = title.getCell(j);
				XSSFCell valueCell = valueRow.getCell(j);
				String valueString = getStringByCell(valueCell);
				cellMap.put(titleCell.getStringCellValue().trim(),
						valueString.trim());
			}
			String questId = String.valueOf(valueRow.getCell(0)
					.getNumericCellValue());
			if (!(questId == null || questId.isEmpty())) {
				map.put(questId, cellMap);
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private String getStringByCell(XSSFCell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case XSSFCell.CELL_TYPE_NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		case XSSFCell.CELL_TYPE_FORMULA:
			String value = null;
			try {
				switch (cell.getCachedFormulaResultType()) {
				case Cell.CELL_TYPE_NUMERIC:
					BigDecimal bigDecimal = new BigDecimal(
							cell.getNumericCellValue());
					value = "" + bigDecimal.floatValue();
					break;
				case Cell.CELL_TYPE_STRING:
					value = cell.getRichStringCellValue().toString();
					break;
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			return value;
		case XSSFCell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case XSSFCell.CELL_TYPE_BLANK:
			return "";
		default:
			break;
		}
		return "";
	}

	public List<String> getAllSheetNames(String filePath) {
		//
		List<String> list = new ArrayList<String>();
		InputStream is = null;
		XSSFWorkbook wb = null;
		try {
			is = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("未找到指定Excel文件");
			logger.error("未找到指定Excel文件 {}", filePath);
			logger.error("", e);

		}
		try {
			wb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int len = wb.getNumberOfSheets();
		for (int i = 0; i < len; i++) {
			XSSFSheet sheet1 = wb.getSheetAt(i);
			list.add(sheet1.getSheetName());
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
