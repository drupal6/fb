package com.net.config;

/**
 * 服务器配置
 */
public abstract class Config {
	private String name;
	private int id;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
