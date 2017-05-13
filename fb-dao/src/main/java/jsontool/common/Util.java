package jsontool.common;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static String getHexString(byte[] buffer) {
		StringBuilder strBuff = new StringBuilder();
		for (int i = 0; i < buffer.length; i++) {
			String tmp = Integer.toHexString(0xFF & buffer[i]);
			if (tmp.length() == 1) {
				strBuff.append("0").append(tmp);
			} else {
				strBuff.append(tmp);
			}
		}
		return strBuff.toString();
	}

	public static String getFileMD5(String filePath) throws Exception {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(filePath);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			byte[] buffer = new byte[1024];
			while (true) {
				int len = fin.read(buffer);
				if (len <= 0) {
					break;
				}
				md5.update(buffer, 0, len);
			}
			byte[] byteArray = md5.digest();
			//
			String md5str = getHexString(byteArray);
			return md5str;
		} finally {
			if (fin != null) {
				fin.close();
			}
		}
	}

	public static int getNum(String s, int def) {
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return def;
		}
	}

//	public static void addIntNode(ASObject baseNode, String name, String value) {
//		if (value == null || value.length() == 0) {
//			return;
//		}
//		baseNode.put(name, getNum(value, 0));
//	}

//	public static void addStringNode(ASObject baseNode, String name,
//			String value) {
//		if (value == null || value.trim().length() < 1) {
//			return;
//		}
//		baseNode.put(name, value.trim());
//	}

	public static void setLocationCenter(Component f) {
		Dimension ds = f.getToolkit().getScreenSize();
		int x = (ds.width - f.getWidth()) / 2;
		int y = (ds.height - f.getHeight()) / 2;
		f.setLocation(x, y);
	}

	public static String paddingLeft(String input, char pad, int length) {
		if (input == null || input.length() >= length) {
			return input;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length - input.length(); ++i) {
			sb.append(pad);
		}
		sb.append(input);
		return sb.toString();
	}

	public static String paddingRight(String input, char pad, int length) {
		if (input == null || input.length() >= length) {
			return input;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(input);
		for (int i = 0; i < length - input.length(); ++i) {
			sb.append(pad);
		}
		return sb.toString();
	}

//	public static ByteArray getByteArray(ASObject obj) throws IOException {
//		ByteArray bytes = new ByteArray();
//		bytes.writeObject(obj);
//		bytes.Compress();
//		return bytes;
//	}

	public static List<String> readFileToList(String path) {
		if (path == null || path.length() == 0) {
			return null;
		}
		List<String> contentList = new ArrayList<String>();
		try {
			Scanner sc = new Scanner(new File(path), "UTF-8");
			while (sc.hasNextLine()) {
				String tempLine = sc.nextLine();
				if (isUseful(tempLine)) {
					contentList.add(tempLine.trim());
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.err.println("未找到文件：" + path);
			e.printStackTrace();
		}
		return contentList;
	}

	private static boolean isUseful(String line) {
		if (line == null || line.length() == 0) {
			return false;
		}
		if (line.trim().startsWith("#")) {
			return false;
		}
		Pattern pattern = Pattern.compile("\\s*");
		return !pattern.matcher(line.trim()).matches();
	}

	public static boolean copyFile(File fromFile, File targetFile) {
		if (fromFile == null || false == fromFile.exists()) {
			System.err.println("FileNotFound: " + fromFile);
			return false;
		}
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
			fin = new FileInputStream(fromFile);
			fout = new FileOutputStream(targetFile);
			byte[] buffer = new byte[4 * 1024];
			while (true) {
				int len = fin.read(buffer, 0, buffer.length);
				if (len <= 0) {
					break;
				}
				fout.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}
		return true;
	}

	public static void mkdirs(File f) {
		if (false == f.exists()) {
			if (false == f.mkdirs()) {
				System.out.println("MakeDirectoryFailure: " + f);
			}
		}
	}

	/**
	 * replace target with replacement in source
	 */
	public static String replaceIgnoreCase(CharSequence source,
			CharSequence target, CharSequence replacement) {
		return Pattern
				.compile(target.toString(),
						Pattern.LITERAL | Pattern.CASE_INSENSITIVE)
				.matcher(source)
				.replaceAll(Matcher.quoteReplacement(replacement.toString()));
	}

	/**
	 * replace #{key}, ignore case
	 * 
	 * @param text
	 * @param params
	 * @return
	 */
	public static String replaceTemplate(String text, Map<String, String> params) {
		if (text != null && params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				if (key == null) {
					continue;
				}
				String value = params.get(key);
				if (value != null) {
					text = replaceIgnoreCase(text, "#{" + key + "}", value);
				}
			}
		}
		return text;
	}

	public static void removeRecursive(File fileOrDir) {
		if (fileOrDir == null) {
			return;
		}
		if (!fileOrDir.exists()) {
			return;
		}
		if (fileOrDir.isDirectory()) {
			for (File f : fileOrDir.listFiles()) {
				removeRecursive(f);
			}
		}
		fileOrDir.delete();
	}

	// remove all folder and files under directory 'dir'
	public static void clearDir(File dir) {
		for (File fileOrDir : dir.listFiles()) {
			removeRecursive(fileOrDir);
		}
	}

}
