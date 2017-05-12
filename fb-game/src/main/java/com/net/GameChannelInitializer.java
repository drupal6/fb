package com.net;

import com.net.common.codec.HeaderDecoder;
import com.net.common.codec.HeaderEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class GameChannelInitializer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline channelPipeline = ch.pipeline();
		channelPipeline.addLast("decoder", new HeaderDecoder());
		channelPipeline.addLast("encoder", new HeaderEncoder());
		channelPipeline.addLast("handler", new GameHandler());
	}

}
