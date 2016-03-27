package com.gifisan.nio.jms.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gifisan.nio.client.ClientResponse;
import com.gifisan.nio.common.StreamUtil;
import com.gifisan.nio.component.Parameters;
import com.gifisan.nio.jms.ByteMessage;
import com.gifisan.nio.jms.ErrorMessage;
import com.gifisan.nio.jms.JMSException;
import com.gifisan.nio.jms.Message;
import com.gifisan.nio.jms.TextMessage;

public class MessageDecoder {
	
	private static Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
	
	public static Message decode(ClientResponse response) throws JMSException{
		int msgType = response.getParameters().getIntegerParameter("msgType");
		Message message = messageParsesFromJSON[msgType].decode(response);
		return message;
	}
	
	static interface MessageDecodeFromJSON {
		
		Message decode(ClientResponse object) throws JMSException;
	}
	
	private static MessageDecodeFromJSON[] messageParsesFromJSON = new MessageDecodeFromJSON[]{
		//ERROR Message
		new MessageDecodeFromJSON() {
			
			public Message decode(ClientResponse response) {
				Parameters param = response.getParameters();
				ErrorMessage message = new ErrorMessage(param.getIntegerParameter("code"));
				return message;
			}
		},
		//NULL Message
		new MessageDecodeFromJSON() {
			
			public Message decode(ClientResponse object) {
				return null;
			}
		},
		//Text Message
		new MessageDecodeFromJSON() {
			
			public Message decode(ClientResponse response) {
				Parameters param = response.getParameters();
				String messageID = param.getParameter("msgID");
				String queueName = param.getParameter("queueName");
				String content = param.getParameter("content");
				TextMessage message = new TextMessage(messageID,queueName,content);
				
				
				return message;
			}
		},
		new MessageDecodeFromJSON() {
			
			public Message decode(ClientResponse response) throws JMSException {
				Parameters param = response.getParameters();
				String messageID = param.getParameter("msgID");
				String queueName = param.getParameter("queueName");
				try {
					byte[] content = StreamUtil.completeRead(response.getInputStream());
					
					return new ByteMessage(messageID,queueName,content);
					
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
					throw new JMSException(e.getMessage()+response.getText(),e);
				}
			}
		}
	};
}
