package com.net.common.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class HeaderDecoder extends ByteToMessageDecoder{

	/**头文件长度**/
	public static final int HEAD_LENGHT = 17;
	/** 包头标志 **/
	public static final byte PACKAGE_TAG = 0x01;
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < HEAD_LENGHT) {
			return;
		}
		in.markReaderIndex();
		byte tag = in.readByte();
		if (tag != PACKAGE_TAG) {
			throw new Exception("非法协议包");
		}
		byte encode = in.readByte();
		byte encrypt = in.readByte();
		byte extend1 = in.readByte();
		byte extend2 = in.readByte();
		int sessionid = in.readInt();
		int length = in.readInt();
		int commandId = in.readInt();

		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}

		Header header = new Header(encode, encrypt, extend1, extend2, sessionid, length, commandId);
		byte[] data = new byte[length];
		in.readBytes(data);
		Message message = new Message(header, data);
		out.add(message);
	}

}
