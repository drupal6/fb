package com.net.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.net.common.codec.Message;
import com.net.config.ClientServerConfig;
import com.net.config.Config;
import com.net.config.ServerInfo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class ClientServer extends AbstractServer {

	protected ClientServer(Config serverConfig) {
		super(serverConfig);
	}

	private Logger logger = LoggerFactory.getLogger(ClientServer.class); 
	
	protected Map<Integer, List<Channel>> connectSessions = new HashMap<Integer, List<Channel>>();
	protected List<Channel> channelList = new ArrayList<Channel>();

	private int groupNum = 1;
	private Bootstrap bootstrap;
	private EventLoopGroup group;
	
	@Override
	public void run() {
		try {
			super.run();
			group = new NioEventLoopGroup(groupNum);
			
			bootstrap = new Bootstrap();
			bootstrap.group(group);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.handler(createChannelInitailizer());
			
			ClientServerConfig config = (ClientServerConfig) this.config;;
			if (config != null) {
				List<ServerInfo> connectServers = config.getConnectServers();
				if (connectServers != null) {
					for (ServerInfo serverInfo : connectServers) {
						initConnect(serverInfo);
					}
				}
				
				Set<Integer> keySet = connectSessions.keySet();
				for (int key : keySet) {
					channelList.addAll(connectSessions.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("client server run fail.", e);
		}
	}
	
	public void initConnect(ServerInfo serverInfo) throws InterruptedException {
		int connected = 0;
		while (connected < serverInfo.getConnectNum()) {
			ChannelFuture fapp = bootstrap.connect(serverInfo.getIp(), serverInfo.getPort());
			Channel channel = fapp.channel();
			CountDownLatch countLatch = new CountDownLatch(1);
			fapp.addListener(new ChannelListener(countLatch));
			if (false == channel.isActive()) {
				countLatch.await(2000, TimeUnit.MILLISECONDS);
			}

			if (false == channel.isActive()) {
				logger.info("not connect ip:" + serverInfo.getIp() + ",port:"
						+ serverInfo.getPort());
				try {
					fapp.cancel(false);
					Thread.sleep(5000L);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage(), e);
				}
			} else {
				logger.info("connect ip:" + serverInfo.getIp() + ",port:" + serverInfo.getPort());
				add(channel, serverInfo.getId());
				connected++;
			}
		}
	}
	
	@Override
	public void stop() {
		group.shutdownGracefully();
	}

	public abstract ChannelInitializer<SocketChannel> createChannelInitailizer();
	
	/**
	 * 添加channel
	 * 
	 * @param channel
	 * @param id
	 */
	public void add(Channel channel, int id) {
		synchronized (this.connectSessions) {
			List<Channel> sessions = this.connectSessions.get(Integer.valueOf(id));
			if (sessions == null) {
				sessions = new ArrayList<Channel>();
				this.connectSessions.put(Integer.valueOf(id), sessions);
			}
			sessions.add(channel);
		}
	}

	public List<Channel> getChannelList(int serverId) {
		return connectSessions.get(serverId);
	}

	private Channel getChannel() {
		return channelList.get(new Random().nextInt(channelList.size() - 1));
	}

	public void write(Message message) {
		Channel channel = getChannel();
		channel.writeAndFlush(message);
	}
}

class ChannelListener implements ChannelFutureListener {

	private CountDownLatch countlatch;

	public ChannelListener(CountDownLatch countlatch) {
		this.countlatch = countlatch;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if (future.channel().isActive()) {
			countlatch.countDown();
		}
	}

}