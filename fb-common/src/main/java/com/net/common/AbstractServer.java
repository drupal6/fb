package com.net.common;

import com.net.config.Config;

public abstract class AbstractServer implements Runnable {

	private int server_id;
	private String server_name;
	protected Config config;
	
	protected AbstractServer(Config serverConfig) {
		this.config = serverConfig;
		if (this.config != null) {
			this.server_name = this.config.getName();
			this.server_id = this.config.getId();
		}
	}
	
	@Override
	public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread(new ServerCloseListener(this)));
	}

	public abstract void stop();

	public int getServer_id() {
		return server_id;
	}

	public String getServer_name() {
		return server_name;
	}
}
