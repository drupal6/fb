package com.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.net.common.util.ClientSessionManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * 监听channel的关闭
 */
public class ChannelCloseListener implements ChannelFutureListener {

	private final static Logger logger = LoggerFactory.getLogger(ChannelCloseListener.class);

	/**
	 * channel关闭处理： 1.移除sessionId和channel的关系 2.移除公共缓存中sessionId和roleId的关系
	 */
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		Channel channel = future.channel();
		Integer sessionId = ClientSessionManager.getInstance().getSessionId(channel);
		ClientSessionManager.getInstance().removeChannel(sessionId);
		logger.info("close channle:" + channel);
	}
}
