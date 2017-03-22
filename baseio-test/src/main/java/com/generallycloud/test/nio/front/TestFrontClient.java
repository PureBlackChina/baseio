/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package com.generallycloud.test.nio.front;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.generallycloud.baseio.codec.protobase.ProtobaseProtocolFactory;
import com.generallycloud.baseio.codec.protobase.future.ProtobaseReadFuture;
import com.generallycloud.baseio.common.CloseUtil;
import com.generallycloud.baseio.common.DateUtil;
import com.generallycloud.baseio.common.SharedBundle;
import com.generallycloud.baseio.common.ThreadUtil;
import com.generallycloud.baseio.component.IoEventHandleAdaptor;
import com.generallycloud.baseio.component.LoggerSocketSEListener;
import com.generallycloud.baseio.component.NioSocketChannelContext;
import com.generallycloud.baseio.component.SocketChannelContext;
import com.generallycloud.baseio.component.SocketSession;
import com.generallycloud.baseio.configuration.ServerConfiguration;
import com.generallycloud.baseio.connector.SocketChannelConnector;
import com.generallycloud.baseio.protocol.ReadFuture;
import com.generallycloud.test.nio.common.ReadFutureFactory;

public class TestFrontClient {

	public static void main(String[] args) throws Exception {
		
		SharedBundle.instance().loadAllProperties("nio");
		
		final AtomicInteger res = new AtomicInteger();

		IoEventHandleAdaptor eventHandleAdaptor = new IoEventHandleAdaptor() {

			@Override
			public void accept(SocketSession session, ReadFuture future) throws Exception {
				
				ProtobaseReadFuture f = (ProtobaseReadFuture)future;
				
				System.out.println(f.getReadText()+"______"+DateUtil.now());
				
				res.incrementAndGet();
			}
		};

		ServerConfiguration configuration = new ServerConfiguration(8900);

		SocketChannelContext context = new NioSocketChannelContext(configuration);
		
		SocketChannelConnector connector = new SocketChannelConnector(context);
		
		context.setIoEventHandleAdaptor(eventHandleAdaptor);
		
		context.setProtocolFactory(new ProtobaseProtocolFactory());
		
		context.addSessionEventListener(new LoggerSocketSEListener());
		
		SocketSession session = connector.connect();
		
		for (int i = 0; i < 10; i++) {

			int fid = Math.abs(new Random().nextInt());
			
			ProtobaseReadFuture future = ReadFutureFactory.create(session,fid, "service-name");

			future.write("你好！");
			
			future.setHashCode(fid);

			session.flush(future);
		}
		
		ThreadUtil.sleep(500);

		CloseUtil.close(connector);
		
		System.out.println("=========="+res.get());
	}

}
