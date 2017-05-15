package com.net.common.codec;

import java.nio.ByteBuffer;

import com.util.CRC16;

/**
 * 消息
 */
public class Message {
	/** crc16验证 **/
	private int crc;
	/** 消息编号**/
	private int sid;
	/** 会话ID **/
	private int sessionId;
	/** 命令 **/
	private int commandId;
	/** 数据 **/
	private byte[] data;

	public Message(int crc, int sid, int sessionId, int commandId, byte[] data) {
		this.crc = crc;
		this.sid = sid;
		this.sessionId = sessionId;
		this.commandId = commandId;
		this.data = data;
	}
	public Message(int sid, int sessionId, int commandId, byte[] data) {
		this.crc = 0;
		this.sid = sid;
		this.sessionId = sessionId;
		this.commandId = commandId;
		this.data = data;
	}
	
	public int getCrc() {
		return crc;
	}

	public void setCrc(int crc) {
		this.crc = crc;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getLength() {
		return data.length;
	}

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean validate() {
		if(crc <= 0) {
			return true;
		}
		int scrc = CRC16.update(data);
		scrc ^= (sid & 0xFFFF);
		return scrc == crc;
	}
	
	public void updateCrc() {
		this.crc = CRC16.update(data) ^ (sid & 0xFFFF);
	}
	
	public byte[] toHeaderByteArray() {
		ByteBuffer bf = ByteBuffer.allocate(HeaderDecoder.HEAD_LENGHT);
		bf.put(HeaderDecoder.PACKAGE_TAG);
		bf.putInt(crc);
		bf.putInt(sid);
		bf.putInt(sessionId);
		bf.putInt(data.length);
		bf.putInt(commandId);
		return bf.array();
	}
	
	@Override
	public String toString() {
		return "crc=" + crc + " sid=" + sid + " sessionId=" + sessionId 
				+ " commandId=" + commandId + " dataLen=" + data.length;
	}
}
