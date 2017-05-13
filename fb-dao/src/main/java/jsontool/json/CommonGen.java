package jsontool.json;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class CommonGen extends ETJson {

	@Override
	public void init() throws IOException {
//        OperatingExcel oe = new OperatingExcel();
//        List<Map<String, String>> ybShopList = oe.readExcelAsList(
//                "D://11.xls", "11", "index", "content");
//        try{
//            String gsonString = getJobData(ybShopList);
//            FileUtils.write(new File("D://11.json"),
//                    gsonString, "utf-8");
//        }catch (Exception e){
//            e.printStackTrace();
//            return;
//        }

	}
	
    private String getJobData(List<Map<String, String>> ybShopList) throws Exception {

        Type listOfTestObject = new TypeToken<Map<Integer, commonConfig>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TreeMap<Integer, commonConfig> list = new TreeMap<Integer, commonConfig>();

        for (Map<String, String> map : ybShopList) {
        	commonConfig data = new commonConfig();
            Field[] attributes = data.getClass().getDeclaredFields();
            for (Field field : attributes) {
                String v = field.getName();
                try {
                    BeanUtils.setProperty(data, v, map.get(v));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            list.put(data.getIndex(), data);
        }

        return gson.toJson(list, listOfTestObject);
    }
    
    public class commonConfig
    {
    	int index;
    	String title;
    	String content;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
    }
}
