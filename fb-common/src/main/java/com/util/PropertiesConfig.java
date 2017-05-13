package com.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig extends Properties {

	private static final long serialVersionUID = -4706616236202036172L;

	public boolean init(String path) {
        try  {    
        	InputStream in = new BufferedInputStream(new FileInputStream(path));
        	load(in);    
        	return true;
        }  catch  (IOException e) {    
        	e.printStackTrace();    
            return false;
        }    
	}
	
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	public int getInt(String key, int defaultValue) {
		String value = getString(key);
		if(value == null) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}
	
	public long getLong(String key) {
		return getInt(key, 0);
	}
	
	public long getLong(String key, long defaultValue) {
		String value = getString(key);
		if(value == null) {
			return defaultValue;
		}
		return Long.parseLong(value);
	}
	
	public String getString(String key) {
		return getProperty(key);
	}
	
	public String getString(String key, String defaultValue) {
		String value = getString(key);
		if(value == null) {
			return defaultValue;
		}
		return value;
	}
	
	public boolean getBoolean(String key) {
		String value = getString(key);
		if(value == null) {
			return false;
		}
		return value.equals("true");
	}
}
