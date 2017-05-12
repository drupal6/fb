package test.net;


import com.net.common.codec.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class TestClientHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(ChannelHandlerContext arg0) throws Exception {
		System.out.println("TestClientHandler channelActive");
	}

	@Override
	public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
		if(false == arg1 instanceof Message) {
			throw new Exception("arg1 type is error.");
		}
		Message message = (Message) arg1;
		System.out.println("TestClientHandler channelRead heard:" + message.getHeader().toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {
		System.out.println("TestClientHandler exceptionCaught");
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext arg0, Object arg1) throws Exception {
		System.out.println("userEventTriggered");
		if(arg1 instanceof IdleStateEvent) {
			System.out.println("IdleStateEvent");
		}else {
			arg0.fireUserEventTriggered(arg1);
		}
	}
}
