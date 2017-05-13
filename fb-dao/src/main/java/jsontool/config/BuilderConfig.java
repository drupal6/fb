package jsontool.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import jsontool.common.StringUtil;


public class BuilderConfig {

	public static String builder(String basePath, String configFile, String configName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(basePath + File.separator + configFile)));
		StringBuffer buffer = new StringBuffer();
		String line = reader.readLine();
		while(line != null) {
			buffer.append(line).append("\n");
			line = reader.readLine();
		}
		String newString = "public static String "+StringUtil.upCase(configName)+"_FILE = EXCEL_PATH + \""+StringUtil.firstUpCase(configName)+".xlsx\";\n \t//##excelfile##\n";
		String ret = buffer.toString();
		return ret.replace("//##excelfile##", newString);
	}
}
