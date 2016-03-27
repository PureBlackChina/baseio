package com.gifisan.nio.client;

import java.io.IOException;
import java.io.InputStream;

import com.gifisan.nio.common.StringUtil;

public class MultiSession implements ClientSesssion {

	private byte				sessionID		= 0;
	private long				timeout		= 0;
	private MessageBus			bus			= null;
	private ClientRequestTask	requestTask	= null;

	protected MultiSession(ClientRequestTask requestTask, MessageBus bus, byte sessionID) {
		this.requestTask = requestTask;
		this.sessionID = sessionID;
		this.bus = bus;
	}

	public long getTimeout() {
		return timeout;
	}

	public ClientResponse request(String serviceName, String content) throws IOException {
		if (StringUtil.isNullOrBlank(serviceName)) {
			throw new IOException("empty service name");
		}

		ClientRequest request = new ClientRequest(sessionID, serviceName, content);
		
		requestTask.offer(request);

		MessageBus bus = this.bus;

		bus.await(timeout);

		return bus.getResponse();
	}

	public ClientResponse request(String serviceName, String content, InputStream inputStream) throws IOException {
		throw new IllegalStateException("can not trans stream when multi session");
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}
