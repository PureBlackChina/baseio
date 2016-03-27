package com.gifisan.nio.jms.client.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.gifisan.nio.client.ClientResponse;
import com.gifisan.nio.client.ClientSesssion;
import com.gifisan.nio.jms.JMSException;
import com.gifisan.nio.jms.client.JMSConnection;

public class JMSConnectonImpl implements JMSConnection {

	boolean		logined	= false;
	ClientSesssion	session	= null;

	public JMSConnectonImpl(ClientSesssion session) throws JMSException {
		this.session = session;
	}

	public void login(String username, String password) throws JMSException {
		if (logined) {
			return;
		}

		Map<String, String> param = new HashMap<String, String>();
		param.put("username", username);
		param.put("password", password);
		String paramString = JSONObject.toJSONString(param);

		ClientResponse response;
		try {
			response = session.request("JMSLoginServlet", paramString);
		} catch (IOException e) {
			throw new JMSException(e.getMessage(), e);
		}
		String result = response.getText();
		boolean logined = "T".equals(result);
		if (!logined) {
			throw new JMSException("用户名密码错误！");
		}
	}

	public void logout() {
		this.logined = false;
	}

}
