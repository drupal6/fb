package com.net.http;



import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

public class HttpServerHandler extends ChannelInboundHandlerAdapter{

	private HttpRequest request;
	private Channel channel;
	private ServletManager servletManager;
	
	private static final String FAVICON_ICO = "/favicon.ico";
    private static final String ERROR = "error";
    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    private static final String CONNECTION_CLOSE = "close";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONNECTION = "Connection";
    
	public HttpServerHandler(ServletManager servletManager) {
		this.servletManager = servletManager;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		if(msg instanceof HttpRequest){
			try{
				channel = ctx.channel();
				request = (HttpRequest) msg;
				String uri = request.uri();
				System.out.println(">>uri=" + uri);
				if(uri.equals(FAVICON_ICO)){
					return;
				}
				SelfRequest request1 = new SelfRequest(request);
				SelfResponse resopnse1 = new SelfResponse(this);
				Servlet servlet = servletManager.getHandler(uri);
				if(servlet == null) {
					throw new Exception("uri not match servlet.");
				}else {
					try{
						servlet.handler(request1, resopnse1);
					} finally {
						request1.waitComplate();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				writeResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, ERROR, true);
				
			}finally{
				ReferenceCountUtil.release(msg);
			}
		}else{
			ReferenceCountUtil.release(msg);
		}
	}
    
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

	public void writeResponse(HttpResponseStatus status, String msg, boolean forceClose){
		ByteBuf byteBuf = Unpooled.wrappedBuffer(msg.getBytes());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
		boolean close = isClose();
		if(!close && !forceClose){
			response.headers().add(CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		}
		ChannelFuture future = channel.write(response);
		if(close || forceClose){
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	private boolean isClose(){
		if(request.headers().contains(CONNECTION, CONNECTION_CLOSE, true) ||
				(request.protocolVersion().equals(HttpVersion.HTTP_1_0) && 
				!request.headers().contains(CONNECTION, CONNECTION_KEEP_ALIVE, true)))
			return true;
		return false;
	}
}
