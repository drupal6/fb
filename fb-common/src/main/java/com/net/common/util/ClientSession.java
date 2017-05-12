package com.net.common.util;

import com.actor.impl.IActor;
import com.net.common.codec.Message;

import io.netty.channel.Channel;

public class ClientSession {

	private Channel channel;
	private Object fbPlayer;
	private IActor actor;
	
	public ClientSession(Channel channel) {
		this.channel = channel;
	}
	public void writeAndFlush(Message message) {
		if(channel != null) {
			channel.writeAndFlush(message);
		}
	}

	public Channel getChannel() {
		return channel;
	}

	public Object getFbPlayer() {
		return fbPlayer;
	}

	public void setFbPlayer(Object fbPlayer) {
		this.fbPlayer = fbPlayer;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public IActor getActor() {
		return actor;
	}
	public void setActor(IActor actor) {
		this.actor = actor;
	}
}
