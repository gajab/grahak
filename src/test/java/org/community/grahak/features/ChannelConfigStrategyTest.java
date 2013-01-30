package org.community.grahak.features;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.message.ExchangeImpl;
import org.community.grahak.TestUtils;
import org.community.grahak.channel.Address;
import org.community.grahak.channel.Channel;
import org.community.grahak.features.ChannelConfigStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class ChannelConfigStrategyTest {	

	private ExchangeImpl exchange ; 
	private Channel channel;

	@Before
	public void setUp() {

		exchange = new ExchangeImpl();
		createChannel();	
	}

	@Test
	public void testAlternateAddresses() throws Exception {

		exchange.put(Channel.CHANNEL, channel);
		ChannelConfigStrategy cofigStrategy = new ChannelConfigStrategy();
		List<String> alturls = cofigStrategy.getAlternateAddresses(exchange);
		assertEquals("ChannelConfigStrategyTest test failed...", 
				            "http://localhost:8090/etservice-access", alturls.get(0).toString());
	}

	private void createChannel(){

		channel = TestUtils.createChannel("TestId");
		List<Address> addresses = new ArrayList<Address>();

		Address primaryAddress = new Address();
		primaryAddress.setHost("localhost");
		primaryAddress.setPort("8080");
		primaryAddress.setPrimary(true);
		primaryAddress.setProtocol("http");		
		addresses.add(primaryAddress);		

		Address add1 = new Address();
		add1.setHost("localhost");
		add1.setPort("8090");
		add1.setPrimary(false);
		add1.setProtocol("http");	
		addresses.add(add1);	

		channel.setAddresses(addresses);
		channel.setBaseUri("/etservice-access");
				
	}
	
	@After 
	public void tearDown() {

		exchange = null;
		channel = null;
		
	} 	
}
