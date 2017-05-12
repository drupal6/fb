package com.net;

import com.net.common.codec.Message;
import com.net.common.util.ChannelPoolManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class BattleHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(ChannelHandlerContext arg0) throws Exception {
		ChannelPoolManager.getInstance().addChannel(arg0.channel());
		System.out.println("ServiceHandler channelActive");
	}

	@Override
	public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
		if(false == arg1 instanceof Message) {
			throw new Exception("arg1 type is error.");
		}
		Message message = (Message) arg1;
		
		//TODO
		arg0.channel().writeAndFlush(message);
		System.out.println("ServiceHandler channelRead heard:" + message.getHeader().toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {
		arg1.printStackTrace();
		System.out.println("ServiceHandler exceptionCaught");
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext arg0, Object arg1) throws Exception {
		System.out.println("ServiceHandler userEventTriggered");
		if(arg1 instanceof IdleStateEvent) {
			System.out.println("ServiceHandler IdleStateEvent");
		}else {
			arg0.fireUserEventTriggered(arg1);
		}
	}
}
