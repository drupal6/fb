package com.net.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.netty.channel.Channel;

/**
 * 内部服务器建立的连接池
 */
public class ChannelPoolManager {

	private List<Channel> channelPool = new ArrayList<Channel>();

	private static ChannelPoolManager poolManager = new ChannelPoolManager();

	public static ChannelPoolManager getInstance() {
		return poolManager;
	}

	public void addChannel(Channel channel) {
		channelPool.add(channel);
	}

	public void removeChannel(Channel channel) {
		channelPool.remove(channel);
	}

	/**
	 * 随机获取一个连接
	 * 
	 * @return
	 */
	public Channel getChannel() {
		Channel channel = channelPool.get(new Random().nextInt(channelPool
				.size()));
		if (channel == null || channel.isActive() == false) {
			throw new RuntimeException("no channel is connected channel:"
					+ channel);
		}
		return channel;
	}

}
