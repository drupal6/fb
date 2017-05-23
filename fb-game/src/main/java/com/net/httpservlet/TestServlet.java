package com.net.httpservlet;

import com.net.http.SelfRequest;
import com.net.http.SelfResponse;
import com.net.http.Servlet;

public class TestServlet extends Servlet{

	@Override
	public void handler(final SelfRequest request, SelfResponse resopnse) {
		request.sync();
		new Thread(new Runnable() {
			@Override
			public void run() {
				resopnse.write("OK");
				request.complate();
			}
		}).start();;
	}

}
