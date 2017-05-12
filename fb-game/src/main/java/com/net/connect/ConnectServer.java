package com.net.connect;

import com.net.common.ClientServer;
import com.net.config.Config;
import com.net.config.loder.ClientServerConfigXmlLoader;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ConnectServer extends ClientServer {

	private static final String DEFUALT_SERVER_CONFIG = "config/connect-config.xml";
	
	private static final ConnectServer instance = new ConnectServer();
	
	public static ConnectServer getInstance() {
		return instance;
	}
	
	private ConnectServer() {
		super(new ClientServerConfigXmlLoader().load(DEFUALT_SERVER_CONFIG));
	}
	private ConnectServer(Config serverConfig) {
		super(serverConfig);
	}

	@Override
	public ChannelInitializer<SocketChannel> createChannelInitailizer() {
		return new ConnectChannelInitializer();
	}

}
