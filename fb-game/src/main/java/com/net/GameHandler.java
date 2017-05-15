package com.net;

import com.actor.GameActorManager;
import com.actor.impl.IRunner;
import com.net.common.codec.Message;
import com.net.common.util.ClientSession;
import com.net.common.util.ClientSessionManager;
import com.net.connect.ConnectServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class GameHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(ChannelHandlerContext arg0) throws Exception {
		
		Channel channel = arg0.channel();
		ClientSession clientSession = new ClientSession(channel);
		int sessionId = ClientSessionManager.getInstance().addChannle(clientSession);
		clientSession.setActor(GameActorManager.getLoginActor(sessionId));
		
		ChannelCloseListener listener = new ChannelCloseListener();
		channel.closeFuture().addListener(listener);
		System.out.println("GameHandler channelActive");
	}

	@Override
	public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
		if(false == arg1 instanceof Message) {
			throw new Exception("arg1 type is error.");
		}
		int sessionId = ClientSessionManager.getInstance().getSessionId(arg0.channel());
		final ClientSession clientSession = ClientSessionManager.getInstance().getClientSession(sessionId);
		
		final Message message = (Message) arg1;
		message.setSessionId(sessionId);
		if(false == message.validate()) {
			System.out.println("GameHandler validate false");
			return;
		}
		if(message.getSid() > 0) {
			int oldSid = clientSession.getSid();
			clientSession.setSid(message.getSid());
			if(message.getSid() != oldSid + 1) {
				System.out.println("GameHandler clientSession false");
				return;
			}
			
		}
		if(message.getCommandId() > 6000000) {  //游戏服处理
			if(clientSession == null) {
//			clientSession.getActor().put(runner);
			}else {
//			((FbPlayer)clientSession.getFbPlayer()).getActor().put(runner);
			}
			
		} else {  //转发战斗服
			ConnectServer.getInstance().write(message);
			GameActorManager.getBattleActor(sessionId).put(new IRunner() {
				@Override
				public Object run() {
					clientSession.getChannel().writeAndFlush(message);
					System.out.println("GameHandler channelRead heard:" + message.toString());
					return null;
				}
			});
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {
		arg1.printStackTrace();
		System.out.println("GateHandler exceptionCaught" + arg1.getMessage());
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext arg0, Object arg1) throws Exception {
		System.out.println("GateHandler userEventTriggered");
		if(arg1 instanceof IdleStateEvent) {
			System.out.println("GateHandler IdleStateEvent");
		}else {
			arg0.fireUserEventTriggered(arg1);
		}
	}
}
