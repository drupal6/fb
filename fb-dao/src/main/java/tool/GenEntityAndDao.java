package tool;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import com.db.pool.DBPoolMgr;

/**
 * 
 * @author Administrator
 */
public class GenEntityAndDao {

	private String packageOutPath = "bean"; // 指定实体生成所在包的路径
	private String authorName = ""; // 作者名字
	private static String tablename = ""; // 表名
	private static String className = "";
	private String[] colnames; // 列名数组
	private String[] colnamesT; // 列名数组
	private String[] colTypes; // 列名类型数组
	private int[] colSizes; // 列名大小数组
	private String[] colComment; // 列名注释
	private boolean f_util = false; // 是否需要导入包java.util.*
	private boolean f_sql = false; // 是否需要导入包java.sql.*
	private String dataBase;
	private String subdivision = ""; // 细分包结构

	/**
	 * 
	 * @param type
	 * @param tname
	 * @param outPath
	 * @param stutes 是否覆盖文件
	 */
	public GenEntityAndDao(String tname, String outPath, boolean stutes, boolean makeProto) {
		String[] beanNameT = tname.split("_");
		String beanName = beanNameT[1].toLowerCase();
		if (beanNameT.length > 1) {
			for (int j = 2; j < beanNameT.length; j++) {
				beanName = beanName + initcap(beanNameT[j]);
			}
		}
		tablename = tname;
		className = beanName;
		this.packageOutPath = outPath;
		// 创建连接
		Connection con = null;
		PreparedStatement pStemt = null;
		PreparedStatement pStemtN = null;
		String sql = null;
		String sqlN = null;
		try {
			subdivision = "game";
			con = DBPoolMgr.getMainConn();
			String dbName = DBPoolMgr.getMainConfig().getString("mysql-dbname");

			// 查要生成实体类的表
			sql = "SELECT * FROM " + tablename;
			sqlN = "SELECT COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS "
					+ "WHERE TABLE_NAME= '" + tablename+ "' AND TABLE_SCHEMA='" + dbName
					+ "' ORDER BY ORDINAL_POSITION";
			
			/*
			 * 1 获取结果集 2 并把相关的字段保存起来
			 */
			pStemt = con.prepareStatement(sql);
			pStemtN = con.prepareStatement(sqlN);
			ResultSetMetaData rsmd = pStemt.getMetaData();
			ResultSet rs = pStemtN.executeQuery();
			int size = rsmd.getColumnCount(); // 统计列
			colnames = new String[size];
			colnamesT = new String[size];
			colTypes = new String[size];
			colSizes = new int[size];
			colComment = new String[size];
			for (int i = 0; i < size; i++) {
				rs.next();
				colnamesT[i] = rsmd.getColumnName(i + 1);
				String[] tmp = rsmd.getColumnName(i + 1).split("_");
				String name = tmp[0].toLowerCase();
				if (tmp.length > 1) {
					for (int j = 1; j < tmp.length; j++) {
						name = name + initcap(tmp[j]);
					}
				}
				colnames[i] = name;
				colTypes[i] = rsmd.getColumnTypeName(i + 1);
				colComment[i] = rs.getString("COLUMN_COMMENT");
				if (colTypes[i].equalsIgnoreCase("datetime")) {
					colTypes[i] = "TIMESTAMP";
					f_util = true;
				}
				if (colTypes[i].equalsIgnoreCase("image")
						|| colTypes[i].equalsIgnoreCase("text")) {
					f_sql = true;
				}
				colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
			}

			try {
				File directory = new File("");
				/*
				 * 生成Bean类
				 */
				if(makeProto && subdivision.equals("game")) {
					makeProto = true;
					String contentProto = parseProto(colnames, colTypes, colSizes);
					String outputPathProto = directory.getAbsolutePath() + "/proto/bean/" 
							+ initcap(className) + "Bean.proto";
					java.io.File f = new java.io.File(outputPathProto);
					if(!f.exists()||stutes){
						FileWriter fwB = new FileWriter(outputPathProto);
						PrintWriter pwB = new PrintWriter(fwB);
						pwB.println(contentProto);
						pwB.flush();
						pwB.close();
						System.out.println("生成"+outputPathProto);
					}
				}else {
					makeProto = false;
				}
				String contentBean = parseBean(colnames, colTypes, colSizes, makeProto);
				String outputPathB = directory.getAbsolutePath() + "/src/main/java/"
						+ this.packageOutPath.replace(".", "/") + "/bean/"
						+ subdivision + "/" + initcap(className) + ".java";
				java.io.File f = new java.io.File(outputPathB);
				if(!f.exists()||stutes){
					FileWriter fwB = new FileWriter(outputPathB);
					PrintWriter pwB = new PrintWriter(fwB);
					pwB.println(contentBean);
					pwB.flush();
					pwB.close();
					System.out.println("生成"+outputPathB);
				}

				/*
				 * 生成Dao
				 */
				String contentDao = parseDao(colnames, colTypes, colSizes);
				String outputPathD = directory.getAbsolutePath() + "/src/main/java/"
						+ this.packageOutPath.replace(".", "/") + "/dao/"
						+ initcap(className) + "Dao.java";
				f = new java.io.File(outputPathD);
				if(!f.exists()||stutes){
					FileWriter fwD = new FileWriter(outputPathD);
					PrintWriter pwD = new PrintWriter(fwD);
					pwD.println(contentDao);
					pwD.flush();
					pwD.close();
					System.out.println("生成"+outputPathD);
				}

				/*
				 * 生成DaoImpl
				 */
				String contentDaoI = parseDaoI(colnames, colTypes, colSizes);
				String outputPathDI = directory.getAbsolutePath() + "/src/main/java/"
						+ this.packageOutPath.replace(".", "/") + "/dao/impl/"
						+ initcap(className) + "DaoImpl.java";
				f = new java.io.File(outputPathDI);
				if(!f.exists()||stutes){
					FileWriter fwDI = new FileWriter(outputPathDI);
					PrintWriter pwDI = new PrintWriter(fwDI);
					System.out.println("生成"+outputPathDI);
					pwDI.println(contentDaoI);
					pwDI.flush();
					pwDI.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能：生成实体类主体代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parseBean(String[] colnames, String[] colTypes,
			int[] colSizes, boolean makeProto) {
		StringBuffer sb = new StringBuffer();
		sb.append("package " + this.packageOutPath + ".bean." + subdivision
				+ ";\r\n\r\n");
		// 判断是否导入工具包
		if (f_util) {
			sb.append("import java.util.Date;\r\n");
		}
		if (f_sql) {
			sb.append("import java.sql.*;\r\n");
		}
		sb.append("import com.bean.Option;\r\n");
		sb.append("import com.bean.DataObject;\r\n");
		sb.append("import com.google.protobuf.InvalidProtocolBufferException;\r\n");
		String protoClassName = null;
		String protoBeanName = null;
		if(makeProto) {
			protoClassName = initcap(className) + "BeanMsg";
			protoBeanName = initcap(className) + "Bean";
			sb.append("import com.bean.game.proto."+protoClassName+"."+protoBeanName+";\r\n");
		}
		sb.append("\r\n");
		// 注释部分
		sb.append("/**\r\n");
		sb.append("* " + tablename + " \r\n");
		sb.append("* @author " + this.authorName + "\r\n* @date   "
				+ new Date() + "\r\n");
		sb.append("*/ \r\n");
		// 实体部分
		// extends DataObject {
		sb.append("public class " + initcap(className)
				+ " extends DataObject {\r\n");
		processAllAttrsBean(sb, makeProto, protoBeanName);// 属性
		processAllMethodBean(sb, makeProto);// get set方法
		if(makeProto) {
			processProtoBean(sb, protoBeanName);
		}
		sb.append("}\r\n");

		return sb.toString();
	}

	/**
	 * 功能：生成实体类主体代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parseDao(String[] colnames, String[] colTypes, int[] colSizes) {
		StringBuffer sb = new StringBuffer();
		sb.append("package " + this.packageOutPath + ".dao;\r\n");
		sb.append("\r\n");
		sb.append("import java.util.List;\r\n");
		sb.append("import com.bean." + subdivision + "."
				+ initcap(className) + ";\r\n");

		// 注释部分
		sb.append("/**\r\n");
		sb.append("* " + tablename + " dao接口的生成\r\n");
		sb.append("* @author " + this.authorName + "\r\n* @date "
				+ new Date() + "\r\n");
		sb.append("*/ \r\n");
		// 实体部分
		sb.append("public interface " + initcap(className + "Dao") + " {\r\n");
		// processAllAttrs(sb);// 属性
		processAllMethodDao(sb);// get set方法
		sb.append("}\r\n");

		return sb.toString();
	}

	/**
	 * 功能：生成实体类主体代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parseDaoI(String[] colnames, String[] colTypes,
			int[] colSizes) {
		StringBuffer sb = new StringBuffer();
		sb.append("package " + this.packageOutPath + ".dao.impl;\r\n");
		sb.append("\r\n");
		sb.append("import java.sql.Connection;\r\n");
		sb.append("import java.sql.ResultSet;\r\n");
		sb.append("import java.sql.SQLException;\r\n");
		sb.append("import java.sql.PreparedStatement;\r\n");
		sb.append("import com.dao." + initcap(className) + "Dao;\r\n");
		sb.append("import java.util.List;\r\n");
		sb.append("import java.util.Map;\r\n");
		sb.append("import java.sql.Types;\r\n");
		sb.append("import com.db.DbParameter;\r\n");
		sb.append("import java.util.HashMap;\r\n");
		sb.append("import java.util.ArrayList;\r\n");
		sb.append("import com.db.pool.DBPoolMgr;\r\n");
		sb.append("import com.bean.Option;\r\n");
		sb.append("import com.bean." + subdivision + "."
				+ initcap(className) + ";\r\n");
		sb.append("import com.db.BaseDao;\r\n");
		
		sb.append("\r\n");
		// 注释部分
		sb.append("/**\r\n");
		sb.append("* " + tablename + " daoImpl实现类的生成\r\n");
		sb.append("* @author " + this.authorName + "\r\n* @date "
				+ new Date() + "\r\n");
		sb.append("*/ \r\n");
		// 实体部分
		sb.append("public class " + initcap(className + "DaoImpl")
				+ " extends BaseDao implements " + initcap(className + "Dao")
				+ " {\r\n");
		// processAllAttrs(sb);// 属性
		processAllMethodDaoI(sb);// get set方法
		sb.append("}\r\n");

		return sb.toString();
	}

	/**
	 * 功能：生成所有属性
	 * 
	 * @param sb
	 */
	private void processAllAttrsBean(StringBuffer sb, boolean makeProto, String protoBeanName) {
		sb.append("\r\n");
		for (int i = 0; i < colnames.length; i++) {
			if(false == makeProto) {
				sb.append("\t/** " + colComment[i] + " */\r\n");
				sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " "
						+ colnames[i] + ";\r\n\n");
			}
		}
		if(makeProto) {
			sb.append("\tprivate "+protoBeanName+".Builder builder;\r\n\n");
			
			sb.append("\tpublic " + initcap(className) + "(){\r\n");
			sb.append("\t\tthis.builder = "+protoBeanName+".newBuilder();\r\n");
			sb.append("\t}\r\n\n");
			
			sb.append("\tpublic " + initcap(className) + "("+protoBeanName+" build){\r\n");
			sb.append("\t\tthis.builder = build.toBuilder();\r\n");
			sb.append("\t}\r\n\n");
			
			sb.append("\tpublic void setBuilder(" + protoBeanName + ".Builder builder) {\r\n");
			sb.append("\t\tthis.builder = builder;\r\n");
			sb.append("\t}\r\n\n");
			
			sb.append("\tpublic "+protoBeanName+".Builder getBuilder() {\r\n");
			sb.append("\t\treturn builder;\r\n");
			sb.append("\t}\r\n\n");
		}

	}

	/**
	 * 功能：生成所有方法
	 * 
	 * @param sb
	 */
	private void processAllMethodBean(StringBuffer sb, boolean makeProto) {
		for (int i = 0; i < colnames.length; i++) {
			String type = sqlType2JavaType(colTypes[i]);
			sb.append("\tpublic void set" + initcap(colnames[i]) + "("
					+ type + " " + colnames[i]
					+ ") {\r\n");
			// if(this.userId!=userId){
			if(makeProto) {
				if (type.equals("String") || type.equals("Date")) {
					sb.append("\t\tif(builder.get" + initcap(colnames[i]) + "() == null || !builder.get" + initcap(colnames[i])
					+ "().equals(" + colnames[i] + ")) {\r\n");
				} else {
					sb.append("\t\tif (builder.get" + initcap(colnames[i]) + "() != " + colnames[i]
							+ ") {\r\n");
				}
			}else {
				if (type.equals("String") || type.equals("Date")) {
					sb.append("\t\tif(this." + colnames[i] + " == null || !this." + colnames[i]
							+ ".equals(" + colnames[i] + ")) {\r\n");
				} else {
					sb.append("\t\tif (this." + colnames[i] + " != " + colnames[i]
							+ ") {\r\n");
				}
			}
			// this.userId = userId;
			if(makeProto) {
				sb.append("\t\t\tbuilder.set" + initcap(colnames[i]) + "(" + colnames[i] + ");\r\n");
			}else {
				sb.append("\t\t\tthis." + colnames[i] + " = " + colnames[i] + ";\r\n");
			}
			// setOp(Option.Update);
			sb.append("\t\t\tsetOp(Option.Update);\r\n");
			sb.append("\t\t}\r\n");
			sb.append("\t}\r\n\n");
			sb.append("\tpublic " + type + " get"
					+ initcap(colnames[i]) + "() {\r\n");
			if(makeProto) {
				sb.append("\t\treturn builder.get" + initcap(colnames[i]) + "();\r\n");
			}else {
				sb.append("\t\treturn " + colnames[i] + ";\r\n");
			}
			sb.append("\t}\r\n\n");
		}

	}
	private void processProtoBean(StringBuffer sb, String protoBeanName) {
		sb.append("\t@Override\r\n");
		sb.append("\tpublic void parseFrom(byte[] data) {\r\n");
		sb.append("\t\ttry {\r\n");
			sb.append("\t\t\tbuilder = "+protoBeanName+".parseFrom(data).toBuilder();\r\n");
		sb.append("\t\t} catch (InvalidProtocolBufferException e) {\r\n");
		sb.append("\t\t\te.printStackTrace();\r\n");
		sb.append("\t\t}\r\n");
		sb.append("\t}\r\n\n");
		
		sb.append("\t@Override\r\n");
		sb.append("\tpublic byte[] toByteArray() {\r\n");
		sb.append("\t\treturn builder.build().toByteArray();\r\n");
		sb.append("\t}\r\n\n");
	}

	/**
	 * 功能：生成所有方法
	 * 
	 * @param sb
	 */
	private void processAllMethodDao(StringBuffer sb) {
		sb.append("\r\n");
		// create方法
		sb.append("\tpublic boolean create" + initcap(className) + "("
				+ initcap(className) + " " + className + ");\r\n\r\n");
		// 查询list
		sb.append("\tpublic List<" + initcap(className) + "> get"
				+ initcap(className) + "List();\r\n\r\n");
		// byId查询
		sb.append("\tpublic " + initcap(className) + " get"
				+ initcap(className) + "ById(int id);\r\n\r\n");
		// update方法
		sb.append("\tpublic boolean update" + initcap(className) + "("
				+ initcap(className) + " " + className + ");\r\n\r\n");
	}

	/**
	 * 生成所有方法
	 * 
	 * @param sb
	 */
	private void processAllMethodDaoI(StringBuffer sb) {
		sb.append("\r\n");
		// create方法
		createM(sb);
		// 查询list
		getListM(sb);
		// ById查询
		getBeanM(sb);
		// update方法
		updateM(sb);
		resultToBeanM(sb);
		openConnM(sb);
	}

	private void openConnM(StringBuffer sb) {
		sb.append("\t@Override\r\n");
		sb.append("\tprotected Connection openConn() {\r\n");
		sb.append("\t\treturn DBPoolMgr.getMainConn();\r\n");
		sb.append("\t}\r\n");
	}

	/**
	 * 更新方法
	 * 
	 * @param sb
	 */
	private void updateM(StringBuffer sb) {
		sb.append("\t@Override\r\n");
		sb.append("\tpublic boolean update" + initcap(className) + "("
				+ initcap(className) + " " + className
				+ ") {\r\n");
		StringBuffer sbT = new StringBuffer();
		StringBuffer sbF = new StringBuffer();
		StringBuffer sbP = new StringBuffer();
		for (int i = 0; i < colnames.length; i++) {
			String type = colTypes[i];
			
			if (type.equals("INT")) {
				type = "INTEGER";
			}

			if (i == 0) {
				sbF.append("\t\t\tparam.put(" + (colnames.length)
						+ ", new DbParameter(Types." + type.toUpperCase()
						+ ", " + className + ".get" + initcap(colnames[i])
						+ "()));\r\n");
			}else{
				sbP.append("\t\t\tparam.put(" + (i) + ", new DbParameter(Types."
						+ type.toUpperCase() + ", " + className + ".get"
						+ initcap(colnames[i]) + "()));\r\n");
				sbT.append(colnamesT[i] + "=?");
				if (i < colnames.length - 1) {
					sbT.append(",");
				}
			}

		}
		sb.append("\t\tboolean result = false;\r\n");
		sb.append("\t\tif (" + className + ".beginUpdate()) {\r\n");
		
		sb.append("\t\t\tString sql = \"update " + tablename + " set "
				+ sbT.toString() + " where "+colnamesT[0]+"=?;\";\r\n");
		sb.append("\t\t\tMap<Integer, DbParameter> param = new HashMap<Integer, DbParameter>();\r\n");
		sb.append(sbP.toString());
		sb.append(sbF.toString());
		sb.append("\t\t\tresult = execNoneQuery(sql, param) > -1;\r\n");
		sb.append("\t\t\t" + className + ".commitUpdate(result);\r\n");
		
		sb.append("\t\t}\r\n");
		sb.append("\t\treturn result;\r\n");
		sb.append("\t}\r\n\r\n");
	}

	/**
	 * 获取单个bean对象的方法
	 * 
	 * @param sb
	 */
	private void getBeanM(StringBuffer sb) {
		sb.append("\t@Override\r\n");
		sb.append("\tpublic " + initcap(className) + " get"
				+ initcap(className) + "ById(int id) {\r\n");
		sb.append("\t\tString sql = \"select * from " + tablename
				+ " where "+colnamesT[0]+"=?;\";\r\n");
		sb.append("\t\tMap<Integer, DbParameter> param = new HashMap<Integer, DbParameter>();\r\n");
		sb.append("\t\tparam.put(1, new DbParameter(Types.INTEGER, id));\r\n");
		sb.append("\t\tPreparedStatement pstmt = execQuery(sql, param);\r\n");
		sb.append("\t\tResultSet rs = null;\r\n");
		sb.append("\t\t" + initcap(className) + " " + className
				+ " = null;\r\n");
		sb.append("\t\tif (pstmt != null) {\r\n");
		sb.append("\t\t\ttry {\r\n");
		sb.append("\t\t\t\trs = pstmt.executeQuery();\r\n");
		sb.append("\t\t\t\tif(rs.next()){\r\n");
		sb.append("\t\t\t\t\t" + className + " = resultToBean(rs);\r\n");
		sb.append("\t\t\t\t}\r\n");
		sb.append("\t\t\t} catch (SQLException e) {\r\n");
		sb.append("\t\t\t} finally {\r\n");
		sb.append("\t\t\t\tcloseConn(pstmt, rs);\r\n");
		sb.append("\t\t\t}\r\n");
		sb.append("\t\t}\r\n");
		sb.append("\t\treturn " + className + ";\r\n");
		sb.append("\t}\r\n\r\n");
	}

	/**
	 * 生成bean转换方法
	 * 
	 * @param sb
	 */
	private void resultToBeanM(StringBuffer sb) {
		sb.append("\tpublic " + initcap(className)
				+ " resultToBean(ResultSet rs) throws SQLException {\r\n");
		sb.append("\t\t" + initcap(className) + " " + className
				+ " = new " + initcap(className) + "();\r\n");
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\t\t" + className + ".set" + initcap(colnames[i])
					+ "(rs.get" + initcap(sqlType2ResultSetType(colTypes[i]))
					+ "(\"" + colnamesT[i] + "\"));\r\n");
		}
		sb.append("\t\t" + className + ".setOp(Option.None);\r\n");
		sb.append("\t\treturn " + className + ";\r\n");
		sb.append("\t}\r\n\r\n");
	}

	/**
	 * 生成获取列表方法
	 * 
	 * @param sb
	 */
	private void getListM(StringBuffer sb) {
		sb.append("\t@Override\r\n");
		sb.append("\tpublic List<" + initcap(className) + "> get"
				+ initcap(className) + "List() {\r\n");
		// String sql="select * from t_user;";
		sb.append("\t\tString sql = \"select * from " + tablename + ";\";\r\n");
		sb.append("\t\tPreparedStatement pstmt = execQuery(sql, null);\r\n");
		sb.append("\t\tResultSet rs = null;\r\n");
		sb.append("\t\tList<" + initcap(className)
				+ "> infos = null;\r\n");
		sb.append("\t\tif (pstmt != null) {\r\n");
		sb.append("\t\t\tinfos = new ArrayList<" + initcap(className)
				+ ">();\r\n");
		sb.append("\t\t\ttry {\r\n");
		sb.append("\t\t\t\trs = pstmt.executeQuery();\r\n");
		sb.append("\t\t\t\twhile (rs.next()) {\r\n");
		sb.append("\t\t\t\t\tinfos.add(resultToBean(rs));\r\n");
		sb.append("\t\t\t\t}\r\n");
		sb.append("\t\t\t} catch (SQLException e) {\r\n");
		sb.append("\t\t\t} finally {\r\n");
		sb.append("\t\t\t\tcloseConn(pstmt, rs);\r\n");
		sb.append("\t\t\t}\r\n");
		sb.append("\t\t}\r\n");
		sb.append("\t\treturn infos;\r\n");
		sb.append("\t}\r\n\r\n");
	}

	/**
	 * 生成创建方法
	 * 
	 * @param sb
	 */
	private void createM(StringBuffer sb) {
		sb.append("\t@Override\r\n");
		sb.append("\tpublic boolean create" + initcap(className) + "("
				+ initcap(className) + " " + className
				+ ") {\r\n");
		StringBuffer sbT = new StringBuffer();
		StringBuffer sbF = new StringBuffer();
		StringBuffer sbP = new StringBuffer();
		for (int i = 0; i < colnames.length; i++) {
			String type = colTypes[i];
			sbT.append(colnamesT[i]);
			sbF.append("?");
			if (type.equals("INT")) {
				type = "INTEGER";
			}
			
			sbP.append("\t\t\tparam.put(" + (i + 1) + ", new DbParameter(Types."
					+ type.toUpperCase() + ", " + className + ".get"
					+ initcap(colnames[i]) + "()));\r\n");
			if (i != colnames.length - 1) {
				sbT.append(",");
				sbF.append(",");
			}
		}
		sb.append("\t\tboolean result = false;\r\n");
		sb.append("\t\tif (" + className + ".beginAdd()) {\r\n");
		
		sb.append("\t\t\tString sql = \"insert into " + tablename + " ("
				+ sbT.toString() + ") values (" + sbF.toString() + ");\";\r\n");
		sb.append("\t\t\tMap<Integer, DbParameter> param = new HashMap<Integer, DbParameter>();\r\n");
		sb.append(sbP.toString());
		sb.append("\t\t\tresult = execNoneQuery(sql, param) > -1;\r\n");
		sb.append("\t\t\t" + className + ".commitAdd(result);\r\n");
		sb.append("\t\t}\r\n");
		sb.append("\t\treturn result;\r\n");
		sb.append("\t}\r\n\r\n");
	}

	/**
	 * 功能：将输入字符串的首字母改成大写
	 * 
	 * @param str
	 * @return
	 */
	private String initcap(String str) {

		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		return new String(ch);
	}

	/**
	 * 功能：获得列的数据类
	 * 
	 * @param sqlType
	 * @return
	 */
	private String sqlType2JavaType(String sqlType) {

		if (sqlType.equalsIgnoreCase("bit") 
				|| sqlType.equalsIgnoreCase("int")
				|| sqlType.equalsIgnoreCase("smallint")
				|| sqlType.equalsIgnoreCase("tinyint")) {
			return "int";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("decimal")
				|| sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real")
				|| sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("varchar")
				|| sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar")
				|| sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text")
				|| sqlType.equalsIgnoreCase("image")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("datetime")
				|| sqlType.equalsIgnoreCase("timestamp")) {
			return "Date";
		}

		return null;
	}

	/**
	 * 
	 * @param sqlType
	 * @return
	 */
	private String sqlType2ResultSetType(String sqlType) {

		if (sqlType.equalsIgnoreCase("bit") 
				|| sqlType.equalsIgnoreCase("int")
				|| sqlType.equalsIgnoreCase("smallint")
				|| sqlType.equalsIgnoreCase("tinyint")) {
			return "int";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("decimal")
				|| sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real")
				|| sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("varchar")
				|| sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar")
				|| sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text")
				|| sqlType.equalsIgnoreCase("image")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("datetime")) {
			return "datetime";
		}else if(sqlType.equalsIgnoreCase("timestamp")){
			return "timestamp";
		}

		return null;
	}
	
	
	/**
	 * 功能：生成Proto
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parseProto(String[] colnames, String[] colTypes,
			int[] colSizes) {
		StringBuffer sb = new StringBuffer();
		sb.append("package " + this.packageOutPath + ".proto.message"
				+ ";\r\n\r\n");
		sb.append("option java_package = \"com.bean.game.proto\";\r\n");
		sb.append("option java_outer_classname = \""+initcap(className)+"BeanMsg\";\r\n");
		sb.append("\r\n");
		// 实体部分
		// extends DataObject {
		sb.append("message " + initcap(className)
				+ "Bean {\r\n");
		processAllAttrsProto(sb);// 属性
		sb.append("}\r\n");
		return sb.toString();
	}
	

	/**
	 * 功能：生成所有属性
	 * 
	 * @param sb
	 */
	private void processAllAttrsProto(StringBuffer sb) {
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\toptional " + sqlType2ProtoType(colTypes[i]) + " " + colnames[i] + "\t="+(i+1)+";\t//" + colComment[i] + "\r\n");
		}
	}
	
	private String sqlType2ProtoType(String sqlType) {
		if (sqlType.equalsIgnoreCase("bit") 
				|| sqlType.equalsIgnoreCase("int")
				|| sqlType.equalsIgnoreCase("smallint")
				|| sqlType.equalsIgnoreCase("tinyint")) {
			return "int32";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "int64";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("decimal")
				|| sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real")
				|| sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("varchar")
				|| sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar")
				|| sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text")
				|| sqlType.equalsIgnoreCase("image")) {
			return "string";
		} else if (sqlType.equalsIgnoreCase("datetime")
				|| sqlType.equalsIgnoreCase("timestamp")) {
			return "int64";
		}
		return null;
	}
}