package jsontool.config;

import jsontool.common.StringUtil;

public class BuilderConfigJson {

	
	public static StringBuffer creatConfigProvider(String beanPackPath, String configName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("package jsontool.json.node;\n\n");
		buffer.append("import java.io.IOException;\n");
		buffer.append("import java.io.File;\n");
		buffer.append("import org.apache.commons.io.FileUtils;\n");
		buffer.append("import jsontool.json.ETJson;\n");
		buffer.append("import jsontool.json.Config;\n");
		buffer.append("import "+beanPackPath.replace("/", ".") + "." + StringUtil.downCase(configName) + "." + StringUtil.firstUpCase(configName) + "Config;\n");
		buffer.append("public class " + StringUtil.firstUpCase(configName) + "Json extends ETJson {\n\n");
		
		buffer.append("\t@Override\n");
		buffer.append("\tpublic void init() throws IOException {\n");
		buffer.append("\t\tString roleString = getConfig(Config." + StringUtil.upCase(configName) + "_FILE, \"" + StringUtil.firstUpCase(configName) + ".xlsx\", " + StringUtil.firstUpCase(configName) + "Config.class, keys ...);\n");
		buffer.append("\t\tFileUtils.writeStringToFile(new File(Config.JSON_PAHT + \"/" + StringUtil.firstUpCase(configName) + ".json\"), roleString, \"utf-8\");\n");
		buffer.append("\t}\n");
		
		buffer.append("}");
		return buffer;
	}
}
