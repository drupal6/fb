package com.net.http;

import java.util.List;

import com.net.config.HttpClassConfig;
import com.net.config.loder.HttpClazzConfigLoader;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpChannleInitializer extends ChannelInitializer<SocketChannel>{

	private SslContext sslCtx;
	private ServletManager servletManager;
	
	
	public HttpChannleInitializer(SslContext sslCtx, String configPath) {
		List<HttpClassConfig> configs = HttpClazzConfigLoader.load(configPath);
		servletManager = new ServletManager();
		for(HttpClassConfig c : configs) {
			servletManager.addHandler(c.getPath(), c.getClazz());
			System.out.println("load servlet:" + c.toString());
		}
		this.sslCtx = sslCtx;
	}
	
	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
		ChannelPipeline pipeline = arg0.pipeline();
		if(sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(arg0.alloc()));
		}
		pipeline.addLast(new HttpResponseEncoder());
		pipeline.addLast(new HttpRequestDecoder());
		pipeline.addLast(new HttpObjectAggregator(65536));
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new CorsHandler(corsConfig));
		pipeline.addLast(new HttpServerHandler(servletManager));
		
	}

}