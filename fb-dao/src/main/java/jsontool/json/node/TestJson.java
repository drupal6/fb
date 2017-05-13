package jsontool.json.node;

import java.io.IOException;
import java.io.File;
import org.apache.commons.io.FileUtils;
import jsontool.json.ETJson;
import jsontool.json.Config;
import com.bean.config.test.TestConfig;
public class TestJson extends ETJson {

	@Override
	public void init() throws IOException {
		String roleString = getConfig(Config.TEST_FILE, "Test.xlsx", TestConfig.class, "id");
		FileUtils.writeStringToFile(new File(Config.JSON_PAHT + "/Test.json"), roleString, "utf-8");
	}
}