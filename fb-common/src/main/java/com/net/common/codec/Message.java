package com.net.common.codec;

import java.nio.ByteBuffer;

/**
 * 消息
 * 
 * @author zhaohui
 * 
 */
public class Message {
	/** 头消息 **/
	private Header header;
	/** 数据 **/
	private byte[] data;

	public Message() {

	}

	public Message(Header header) {
		this.header = header;
	}

	public Message(Header header, byte[] data) {
		this.header = header;
		this.data = data;
	}

	public void setContent(int commandId, byte[] data) {
		header.setCommandId(commandId);
		this.data = data;
	}

	public int getSessionId() {
		return header.getSessionId();
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public int getCommand() {
		return getHeader().getCommandId();
	}

	
	public byte[] toHeaderByteArray() {
		ByteBuffer bf = ByteBuffer.allocate(HeaderDecoder.HEAD_LENGHT);
		bf.put(HeaderDecoder.PACKAGE_TAG);
		bf.put(header.getEncode());
		bf.put(header.getEncrypt());
		bf.put(header.getExtend1());
		bf.put(header.getExtend2());
		bf.putInt(header.getSessionId());
		bf.putInt(data.length);
		bf.putInt(header.getCommandId());
		return bf.array();
	}
}
