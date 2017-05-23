package com.net;

import com.net.common.NettyServer;
import com.net.config.Config;
import com.net.config.loder.ServerConfigXmlLoader;
import com.net.http.HttpServer;
import com.redis.RedisTemplateMgr;
import com.util.PropertiesConfig;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class GameServer extends NettyServer {
	
	private static final String DEFUALT_SERVER_CONFIG = "config/game-server-config.xml";
	private static final int DEFUALT_HTTP_PORT = 9999;
	private static final String DEFUALT_CLAZZ_CONFIG = "config/http_class.xml";
	
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
	
	@Override
	public void run() {
		redisInit();
		
		HttpServer httpServer = new HttpServer(DEFUALT_HTTP_PORT, DEFUALT_CLAZZ_CONFIG);
		httpServer.start();
		
		super.run();
	}
	
	private boolean redisInit() {
		PropertiesConfig redisConfig = new PropertiesConfig();
		redisConfig.load("E:/newproject/workspace/NewProject/Lib/config/redis.properties");
		return RedisTemplateMgr.init(redisConfig);
	}
}
