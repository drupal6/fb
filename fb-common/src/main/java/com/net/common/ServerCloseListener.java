package com.net.common;

public class ServerCloseListener implements Runnable {
	
	private AbstractServer server;
	
	public ServerCloseListener(AbstractServer server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		server.stop();
	}

}
