import com.bean.config.test.TestConfig;
import com.bean.provider.TestProvider;

public class TestReadJson {

	public static void main(String[] args) {
		System.setProperty("jsonPath", "F:/fb/fb-dao/json");
		TestProvider.getIns().load();
		TestConfig testConfig = TestProvider.getIns().testConfig(1);
		System.out.println(testConfig.getDesc());
	}

}
