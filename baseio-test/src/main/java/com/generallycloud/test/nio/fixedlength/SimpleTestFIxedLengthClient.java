package com.generallycloud.test.nio.fixedlength;

import com.generallycloud.nio.codec.fixedlength.FixedLengthProtocolFactory;
import com.generallycloud.nio.codec.fixedlength.future.FixedLengthReadFuture;
import com.generallycloud.nio.codec.fixedlength.future.FixedLengthReadFutureImpl;
import com.generallycloud.nio.common.CloseUtil;
import com.generallycloud.nio.common.ThreadUtil;
import com.generallycloud.nio.component.SocketChannelContext;
import com.generallycloud.nio.component.SocketChannelContextImpl;
import com.generallycloud.nio.component.IoEventHandleAdaptor;
import com.generallycloud.nio.component.LoggerSocketSEListener;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.configuration.ServerConfiguration;
import com.generallycloud.nio.connector.SocketChannelConnector;
import com.generallycloud.nio.protocol.ReadFuture;

public class SimpleTestFIxedLengthClient {

	public static void main(String[] args) throws Exception {

		IoEventHandleAdaptor eventHandleAdaptor = new IoEventHandleAdaptor() {

			@Override
			public void accept(SocketSession session, ReadFuture future) throws Exception {
				System.out.println();
				System.out.println("____________________"+future.getReadText());
				System.out.println();
			}
		};
		
		SocketChannelContext context = new SocketChannelContextImpl(new ServerConfiguration("localhost", 18300));

		SocketChannelConnector connector = new SocketChannelConnector(context);

		context.setIoEventHandleAdaptor(eventHandleAdaptor);
		
		context.addSessionEventListener(new LoggerSocketSEListener());

		context.setProtocolFactory(new FixedLengthProtocolFactory());
		
		SocketSession session = connector.connect();

		FixedLengthReadFuture future = new FixedLengthReadFutureImpl(context);

		future.write("hello server!");

		session.flush(future);
		
		ThreadUtil.sleep(100);

		CloseUtil.close(connector);
	}
}
