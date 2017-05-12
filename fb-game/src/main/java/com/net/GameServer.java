package com.net;

import com.net.common.NettyServer;
import com.net.config.Config;
import com.net.config.loder.ServerConfigXmlLoader;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class GameServer extends NettyServer {
	
	private static final String DEFUALT_SERVER_CONFIG = "config/game-server-config.xml";
	
	private static final GameServer instance = new GameServer();
	
	public static GameServer getInstance() {
		return instance;
	}
	
	private GameServer() {
		super(new ServerConfigXmlLoader().load(DEFUALT_SERVER_CONFIG));
	}
	
	private GameServer(Config serverConfig) {
		super(serverConfig);
	}
	
	@Override
	protected ChannelInitializer<SocketChannel> createHandler() {
		return new GameChannelInitializer();
	}
}
