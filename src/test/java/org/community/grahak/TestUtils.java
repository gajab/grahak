package org.community.grahak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.community.grahak.channel.Address;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.ConnectionInfo;
import org.junit.Ignore;

@Ignore
public class TestUtils {
	
	
	public static Message createMessage(Channel channel, HTTPConduit conduit)
	{
		Message message = new MessageImpl();
		Exchange exchange = new ExchangeImpl();
		
		message.setExchange(exchange);
		
		message.put(Message.INVOCATION_CONTEXT, createContextWithChannel(channel, null));
		exchange.setConduit(conduit);
		
		return message;
	}
	
	public static Message createMessage(Channel channel, String serviceName, HTTPConduit conduit)
	{
		Message message = new MessageImpl();
		Exchange exchange = new ExchangeImpl();
		
		message.setExchange(exchange);
		
		message.put(Message.INVOCATION_CONTEXT, createContextWithChannel(channel, serviceName));
		exchange.setConduit(conduit);
		
		return message;
	}
	
	public static Map<String, Map<String, Object>> createContextWithChannel(Channel channel, String serviceName)
	{
		Map<String, Map<String, Object>> context = new HashMap<String, Map<String, Object>>();
		Map<String, Object> requestContext = new HashMap<String, Object>();
		if(channel != null)
			requestContext.put(Channel.CHANNEL, channel);
		if(serviceName != null)
			requestContext.put(Channel.SERVICE_NAME, serviceName);
		
		context.put(ClientImpl.REQUEST_CONTEXT, requestContext);
		
		return context;
	}
	
	public static HTTPConduit createHttpConduit(Bus bus)
	{
		HTTPConduit conduit = null;
		try {
			conduit = new HTTPConduit(bus, new EndpointInfo());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return conduit;
	}
	
	
	public static Channel createChannel(String id)
	{
		Channel channel = new Channel();
		channel.setId(id);
		ConnectionInfo info = new ConnectionInfo();
		info.setConnectionTimeout(1000);
		info.setKeepAlive(true);
		info.setReceiveTimeout(1500);
		channel.setConnection(info);
		
		List<Address> addresses = new ArrayList<Address>();
		Address add = new Address();
		add.setHost("host");
		add.setPort("1111");
		add.setPrimary(true);
		add.setProtocol("http");
		
		addresses.add(add);
		
		channel.setAddresses(addresses);
		channel.setBaseUri("/x/y");
		
		return channel;
	}
	
	
	public static Channel createChannelWithMultipleAddresses()
	{
		Channel channel = new Channel();
		channel.setId("id");
		ConnectionInfo info = new ConnectionInfo();
		info.setConnectionTimeout(1000);
		info.setKeepAlive(true);
		info.setReceiveTimeout(1500);
		channel.setConnection(info);
		
		List<Address> addresses = new ArrayList<Address>();
		Address add1 = new Address();
		add1.setHost("host1");
		add1.setPort("1111");
		add1.setProtocol("http");
		
		
		Address add2 = new Address();
		add2.setHost("host2");
		add2.setPort("2222");
		add2.setPrimary(true);
		add2.setProtocol("http");
		
		
		Address add3 = new Address();
		add3.setHost("host3");
		add3.setPort("3333");
		add3.setProtocol("http");
		
		addresses.add(add1);
		addresses.add(add2);
		addresses.add(add3);
		
		channel.setAddresses(addresses);
		channel.setBaseUri("/x/y");
		
		return channel;
	}
	
	
	public static Channel createSecureChannel(String id)
	{
		Channel channel = new Channel();
		channel.setId(id);
		ConnectionInfo info = new ConnectionInfo();
		info.setConnectionTimeout(1000);
		info.setKeepAlive(true);
		info.setReceiveTimeout(1500);
		channel.setConnection(info);
		
		List<Address> addresses = new ArrayList<Address>();
		Address add = new Address();
		add.setHost("host");
		add.setPort("1111");
		add.setPrimary(true);
		add.setProtocol("https");
		
		addresses.add(add);
		
		channel.setAddresses(addresses);
		channel.setBaseUri("/x/y");
		
		return channel;
	}

}
