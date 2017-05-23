package com.net.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

public class SelfRequest {
	
	private final Logger logger = LoggerFactory.getLogger(SelfRequest.class);
	
	private static final Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:+(\\d+)");  
	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);
	
	private HttpRequest request;
	
	private HttpMethod method;
	private HttpHeaders headers;
	
	private String contentType;
	private String ip;
	private int port = 0;
	private Map<String, String> params;
	
	private CountDownLatch latch;  
	
	public SelfRequest(HttpRequest request) {
		this.request = request;
	}

	public HttpMethod getMethod() {
		if(method == null) {
			method = request.method();
			System.out.println("method=" + method);
		}
		return method;
	}
	
	public HttpHeaders getHeaders() {
		if(headers == null) {
			headers = request.headers();
		}
		return headers;
	}
	
	public String getIp(){
		if(ip == null) {
			parseHost();
		}
		return ip;
	}
	
	public int getPort(){
		if(port == 0) {
			parseHost();
		}
		return port;
	}
	
	public String getContentType(){
		if(contentType == null) {
			HttpHeaders headers = getHeaders();
			String typeStr = headers.get("Content-Type").toString();
			String[] list = typeStr.split(";");
			contentType = list[0];
			System.out.println("contentType=" + contentType);
		}
		return contentType;
	}
	
	public String getUri() {
		return request.uri();
	}
	
	public String getParam(String key) {
		try {
			Map<String, String> pm = getParams();
			if(pm != null) {
				return pm.get(key);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("getParam error. mes:" + e.getMessage());
		}
		return null;
	}
	
	public Map<String, String> getParams() throws IOException {
		if(getMethod().equals(HttpMethod.GET)){
			if(params == null) {
				params = new HashMap<String, String>();
				QueryStringDecoder queryDecoder = new QueryStringDecoder(getUri(), Charset.forName("UTF-8"));
				Map<String, List<String>> uriAttributes = queryDecoder.parameters();
				for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
					for (String attrVal : attr.getValue()) {
						params.put(attr.getKey(), attrVal);
					}
				}
			}
		}
		if(getMethod().equals(HttpMethod.POST)){
			String contentType = getContentType();
			if(contentType.equals("application/x-www-form-urlencoded")){
				if(params == null) {
					params = new HashMap<String, String>();
					HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request, Charset.forName("UTF-8"));
					List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
					for (InterfaceHttpData data : datas) {
						if(data.getHttpDataType() == HttpDataType.Attribute) {
							Attribute attribute = (Attribute) data;
							params.put(attribute.getName(), attribute.getValue());
						}
					}
				}
			}else{
				logger.error("post request not suport type. contentType:" + contentType);
			}
		}
		return params;
	}
	
	public String getPostJsonBody() {
		if(getMethod().equals(HttpMethod.POST)){
			FullHttpRequest fullRequest = (FullHttpRequest) request;
			String contentType = getContentType();
			if(contentType.equals("application/json") || contentType.equals("text/plain")){
				return fullRequest.content().toString(Charset.forName("UTF-8"));
			}else {
				logger.error("post request not suport type. contentType:" + contentType);
			}
		}
		return null;
	}
	
	public void sync() {
		latch = new CountDownLatch(1);  
	}
	
	public void complate() {
		latch.countDown();
	}
	
	public void waitComplate() throws InterruptedException {
		if(latch != null) {
			latch.await();
		}
	}
	
	private void parseHost() {
		HttpHeaders headers = getHeaders();
		String page = headers.get("Host").toString();
		Matcher m = p.matcher(page);  
		this.ip = m.group(1);
		this.port = Integer.parseInt(m.group(2));
	}
	
}
