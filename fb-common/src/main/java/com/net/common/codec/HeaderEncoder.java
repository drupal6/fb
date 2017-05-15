package com.net.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class HeaderEncoder extends MessageToByteEncoder<Message>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		if (false == (msg instanceof Message)) {
			throw new Exception("msg type is error");
		}
		Message message = (Message) msg;
		byte[] buffer = message.getData();
		byte[] header = message.toHeaderByteArray();

		out.writeBytes(header);
		out.writeBytes(buffer);
		return;
	}

}
