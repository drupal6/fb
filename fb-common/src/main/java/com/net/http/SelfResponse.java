package com.net.http;

import io.netty.handler.codec.http.HttpResponseStatus;

public class SelfResponse {

	private HttpServerHandler handler;
	
	public SelfResponse(HttpServerHandler handler) {
		this.handler = handler;
	}
	
	public void write(String msg) {
		handler.writeResponse(HttpResponseStatus.OK, msg, false);
	}
}
