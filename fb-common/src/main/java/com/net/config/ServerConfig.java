package com.net.config;

public class ServerConfig extends Config {

	private int port;
    
	private int bossNum;
	
	private int workerNum;
	
	
	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBossNum() {
		return bossNum;
	}

	public void setBossNum(int bossNum) {
		this.bossNum = bossNum;
	}

	public int getWorkerNum() {
		return workerNum;
	}

	public void setWorkerNum(int workerNum) {
		this.workerNum = workerNum;
	}
}
