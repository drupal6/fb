package com.net;

import com.net.common.NettyServer;
import com.net.config.Config;
import com.net.config.loder.ServerConfigXmlLoader;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class BattleServer extends NettyServer {

	private static final String DEFUALT_SERVER_CONFIG = "config/battle-server-config.xml";
	
	private static final BattleServer instance = new BattleServer();
	
	public static BattleServer getInstance() {
		return instance;
	}
	
	private BattleServer() {
		super(new ServerConfigXmlLoader().load(DEFUALT_SERVER_CONFIG));
	}
	
	private BattleServer(Config serverConfig) {
		super(serverConfig);
	}

	@Override
	protected ChannelInitializer<SocketChannel> createHandler() {
		return new BattleChannelInitializer();
	}
}
