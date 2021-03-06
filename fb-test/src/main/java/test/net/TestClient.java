package test.net;
import com.ly.bean.proto.PlayerItemBeanMsg.PlayerItemBean;
import com.net.common.codec.Message;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TestClient {

	public static void main(String[] args) {
		
		EventLoopGroup group = new NioEventLoopGroup(1);
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new TestClientChannelInitializer());
		
		ChannelFuture fapp = bootstrap.connect("127.0.0.1", 8000);
		Channel channel = fapp.channel();
		PlayerItemBean.Builder builder = PlayerItemBean.newBuilder();
		builder.setId(1);
		builder.setBaseItemId(2);
		builder.setType(1);
		builder.setNum(5);
		byte[] data = builder.build().toByteArray();
		
		Message message = new Message(1, 0, 100001, data);
		System.out.println(data.length);
		
		channel.writeAndFlush(message);
	}
	
}
