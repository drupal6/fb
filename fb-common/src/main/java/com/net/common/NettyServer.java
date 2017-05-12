package com.net.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.net.config.Config;
import com.net.config.ServerConfig;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class NettyServer extends AbstractServer{

	private Logger logger = LoggerFactory.getLogger(NettyServer.class);
	
	private int port;
	private int bossNum = 1;
	private int workerNum = Runtime.getRuntime().availableProcessors() + 1;
	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workGroup;
	
	protected NettyServer(Config serverConfig) {
		super(serverConfig);
		this.port = ((ServerConfig)config).getPort();
		this.bossNum = ((ServerConfig)config).getBossNum();
		this.workerNum = ((ServerConfig)config).getWorkerNum();
		
	}
	
	@Override
	public void run() {
		super.run();
		bossGroup = new NioEventLoopGroup(bossNum);
		workGroup = new NioEventLoopGroup(workerNum);
		
		bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(createHandler());
		bootstrap.bind(port);
		
		logger.info("netty server started.");
		
	}
	
	@Override
	public void stop() {
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
		logger.info("netty server stop.");
	}
	
	protected abstract ChannelInitializer<SocketChannel> createHandler();

}
