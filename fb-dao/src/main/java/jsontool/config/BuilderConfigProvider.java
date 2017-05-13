package jsontool.config;

import jsontool.common.StringUtil;

public class BuilderConfigProvider {

	
	public static StringBuffer creatConfigProvider(String basePath, String providerPackPath, String beanPackPath, String configName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+providerPackPath.replace("/", ".")+";\n\n");
		buffer.append("import java.lang.reflect.Type;\n");
		buffer.append("import java.util.HashMap;\n");
		buffer.append("import java.util.Map;\n");
		buffer.append("import org.slf4j.Logger;\n");
		buffer.append("import org.slf4j.LoggerFactory;\n");
		buffer.append("import com.google.gson.reflect.TypeToken;\n");
//		buffer.append("import game.data.jsondata.DataProvider;\n");
		buffer.append("import " +beanPackPath.replace("/", ".")).append(".").append(StringUtil.downCase(configName))
				.append(".").append(StringUtil.firstUpCase(configName)).append("Config;\n\n");
		buffer.append("public class "+StringUtil.firstUpCase(configName)+"Provider {\n\n");
		
		buffer.append("\tprivate static final Logger LOGGER = LoggerFactory.getLogger("+StringUtil.firstUpCase(configName)+"Provider.class);\n\n");
		buffer.append("\tprivate static "+StringUtil.firstUpCase(configName)+"Provider instance = new "+StringUtil.firstUpCase(configName)+"Provider();\n\n");
		buffer.append("\tpublic static "+StringUtil.firstUpCase(configName)+"Provider getIns() {\n");
		buffer.append("\t\treturn instance;\n");
		buffer.append("\t}\n\n");
		
		buffer.append("\tprivate Map<Integer, "+StringUtil.firstUpCase(configName)+"Config> "+StringUtil.firstDownCase(configName)+"Configs = new HashMap<Integer, "+StringUtil.firstUpCase(configName)+"Config>();\n\n");
		
		buffer.append("\t@SuppressWarnings(\"unchecked\")\n");
		buffer.append("\tpublic boolean load() {\n");
		buffer.append("\t\tMap<Integer, "+StringUtil.firstUpCase(configName)+"Config> new"+StringUtil.firstUpCase(configName)+"Configs = new HashMap<Integer, "+StringUtil.firstUpCase(configName)+"Config>();\n");
		buffer.append("\t\ttry {\n");
		buffer.append("\t\t\tType type = new TypeToken<Map<Integer, "+StringUtil.firstUpCase(configName)+"Config>>() {}.getType();\n");
		buffer.append("\t\t\tnew"+StringUtil.firstUpCase(configName)+"Configs = (Map<Integer, "+StringUtil.firstUpCase(configName)+"Config>) DataProvider.fromJson(\""+StringUtil.firstUpCase(configName)+".json\", type);\n");
		buffer.append("\t\t\t"+StringUtil.firstDownCase(configName)+"Configs = new"+StringUtil.firstUpCase(configName)+"Configs;\n");
		buffer.append("\t\t\treturn true;\n");
		buffer.append("\t\t} catch(Exception e) {\n");
		buffer.append("\t\t\tLOGGER.error(\""+StringUtil.firstUpCase(configName)+"Provider load json exception :\", e);\n");
		buffer.append("\t\t\treturn false;\n");
		buffer.append("\t\t}\n");
		buffer.append("\t}\n\n");
		
		buffer.append("\tpublic Map<Integer, "+StringUtil.firstUpCase(configName)+"Config> "+StringUtil.firstDownCase(configName)+"Configs() {\n");
		buffer.append("\t\treturn "+StringUtil.firstDownCase(configName)+"Configs;\n");
		buffer.append("\t}\n\n");
		
		buffer.append("\tpublic "+StringUtil.firstUpCase(configName)+"Config "+StringUtil.firstDownCase(configName)+"Config(int id) {\n");
		buffer.append("\t\treturn "+StringUtil.firstDownCase(configName)+"Configs.get(id);\n");
		buffer.append("\t}\n\n");
		
		buffer.append("}");
		return buffer;
	}
}
