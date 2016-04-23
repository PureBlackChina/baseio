package com.gifisan.nio.client;

import java.io.IOException;

import com.gifisan.nio.component.ReadFuture;
import com.gifisan.nio.component.Session;
import com.gifisan.nio.service.ServiceAcceptor;

public class ClientServiceAcceptor implements ServiceAcceptor {


	public void accept(Session session, ReadFuture future) throws Exception {
		DefaultClientSession clientSesssion = (DefaultClientSession) session;
		
		ServiceAcceptor acceptor = clientSesssion.getServiceAcceptor(future.getServiceName());
		
		if (acceptor == null) {
			throw new IOException("null acceptor ,service name:"+future.getServiceName());
		}
		
		acceptor.accept(session, future);
	}


}