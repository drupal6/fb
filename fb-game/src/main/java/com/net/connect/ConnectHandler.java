package com.net.connect;

import com.net.common.codec.Message;
import com.net.common.util.ClientSession;
import com.net.common.util.ClientSessionManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class ConnectHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(ChannelHandlerContext arg0) throws Exception {
		System.out.println("ConnectHandler channelActive");
	}

	@Override
	public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
		if(false == arg1 instanceof Message) {
			throw new Exception("arg1 type is error.");
		}
		Message message = (Message) arg1;
		int sessionId = message.getSessionId();
		try {
			ClientSession clientSession = ClientSessionManager.getInstance().getClientSession(sessionId);
			
			//TODO
			clientSession.writeAndFlush(message);
			System.out.println("ConnectHandler channelRead heard:" + message.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {
		System.out.println("ConnectHandler exceptionCaught");
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext arg0, Object arg1) throws Exception {
		System.out.println("ConnectHandler userEventTriggered");
		if(arg1 instanceof IdleStateEvent) {
			System.out.println("ConnectHandler IdleStateEvent");
		}else {
			arg0.fireUserEventTriggered(arg1);
		}
	}
}
