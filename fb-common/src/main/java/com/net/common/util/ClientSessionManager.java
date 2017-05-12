package com.net.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;

/**
 * session和channel关系管理器
 */
public class ClientSessionManager {

	public static ClientSessionManager instance = new ClientSessionManager();

	private Map<Integer, ClientSession> sessionChannelMap = new ConcurrentHashMap<Integer, ClientSession>();
	private Map<Channel, Integer> channelSessionMap = new ConcurrentHashMap<Channel, Integer>();

	private AtomicInteger idIncre = new AtomicInteger(0);
	
	public static ClientSessionManager getInstance() {
		return instance;
	}

	private int getId() {
		synchronized (idIncre) {
			if(idIncre.get() > Integer.MAX_VALUE / 2) {
				idIncre.set(0);
			}
			return idIncre.incrementAndGet();
		}
	}
	
	public int addChannle(ClientSession clientSession) {
		synchronized (this) {
			int sessionId = getId();
			sessionChannelMap.put(sessionId, clientSession);
			channelSessionMap.put(clientSession.getChannel(), sessionId);
			return sessionId;
		}
	}
	
	/**
	 * 获取指定client session
	 * 
	 * @param sessionId
	 * @return
	 */
	public ClientSession getClientSession(int sessionId) {
		return sessionChannelMap.get(sessionId);
	}
	

	/**
	 * 获取指定channel的sessionId
	 * 
	 * @param channel
	 * @return
	 */
	public Integer getSessionId(Channel channel) {
		return channelSessionMap.get(channel);
	}

	/**
	 * 移除指定session和channel
	 * 
	 * @param sessionId
	 */
	public void removeChannel(int sessionId) {
		synchronized (this) {
			ClientSession clientSession = sessionChannelMap.get(sessionId);
			if (clientSession != null && clientSession.getChannel() != null) {
				channelSessionMap.remove(clientSession.getChannel());
			}
			sessionChannelMap.remove(sessionId);
		}
	}

	public void removeChannel(Channel channel) {
		synchronized (this) {
			Integer sessionId = channelSessionMap.get(channel);
			if (sessionId != null) {
				sessionChannelMap.remove(sessionId);
			}
			channelSessionMap.remove(channel);
		}
	}
	
}