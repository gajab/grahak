package org.community.grahak.interceptors;


import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.test.AbstractCXFTest;
import org.community.grahak.TestUtils;
import org.community.grahak.channel.Channel;
import org.community.grahak.interceptors.ResloveServiceEndpointInterceptor;
import org.junit.Test;


/**
 * 
 * This is a junit test for ResloveServiceEndpointInterceptor This test was
 * generated at 06/15/2010
 * 
 * @author rkarwa
 */

public class ResloveServiceEndpointInterceptorTest extends AbstractCXFTest {
	
	/**
	 * To test channel primary address is set in message if there is no endpoint has been already
	 * set in the message
	 * @see org.community.grahak.interceptors.ResloveServiceEndpointInterceptor#handleMessage
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageNoEndpointInMessage() throws Fault {
		ResloveServiceEndpointInterceptor interceptor = new ResloveServiceEndpointInterceptor();
		Channel channel = TestUtils.createChannel("id");
		Message message = TestUtils.createMessage(channel, TestUtils.createHttpConduit(getBus()));
		
		interceptor.handleMessage(message);
		
		assertEquals(channel.getPrimaryEndpointUrl(), message.get(Message.ENDPOINT_ADDRESS));
		
	}
	
	/**
	 * To test channel primary address is set in message if there is blank endpoint has been already
	 * set in the message
	 * @see org.community.grahak.interceptors.ResloveServiceEndpointInterceptor#handleMessage
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageBlankEndpointInMessage() throws Fault {
		ResloveServiceEndpointInterceptor interceptor = new ResloveServiceEndpointInterceptor();
		Channel channel = TestUtils.createChannel("id");
		Message message = TestUtils.createMessage(channel, TestUtils.createHttpConduit(getBus()));
		message.put(Message.ENDPOINT_ADDRESS, "    " );
		
		interceptor.handleMessage(message);
		
		assertEquals(channel.getPrimaryEndpointUrl(), message.get(Message.ENDPOINT_ADDRESS));
	}
	
	/**
	 * To test channel primary address is set in message if there is a invalid endpoint has been already
	 * set in the message
	 * @see org.community.grahak.interceptors.ResloveServiceEndpointInterceptor#handleMessage
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageInvalidEndpointInMessage() throws Fault {
		ResloveServiceEndpointInterceptor interceptor = new ResloveServiceEndpointInterceptor();
		Channel channel = TestUtils.createChannel("id");
		Message message = TestUtils.createMessage(channel, TestUtils.createHttpConduit(getBus()));
		message.put(Message.ENDPOINT_ADDRESS, "http://host1:2222/x/y" );
		
		interceptor.handleMessage(message);
		
		assertEquals("http://host:1111/x/y", channel.getPrimaryEndpointUrl());
		assertEquals(channel.getPrimaryEndpointUrl(), message.get(Message.ENDPOINT_ADDRESS));
	}
	
	/**
	 * To test channel primary address is set in message if there is a valid endpoint has been already
	 * set in the message. the valid message is primary address
	 * @see org.community.grahak.interceptors.ResloveServiceEndpointInterceptor#handleMessage
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageValidPrimaryEndpointInMessage() throws Fault {
		ResloveServiceEndpointInterceptor interceptor = new ResloveServiceEndpointInterceptor();
		Channel channel = TestUtils.createChannel("id");
		Message message = TestUtils.createMessage(channel, TestUtils.createHttpConduit(getBus()));
		message.put(Message.ENDPOINT_ADDRESS, "http://host:1111/x/y");
		
		interceptor.handleMessage(message);
		
		assertEquals("http://host:1111/x/y", channel.getPrimaryEndpointUrl());
		assertEquals(channel.getPrimaryEndpointUrl(), message.get(Message.ENDPOINT_ADDRESS));
	}
	
	
	/**
	 * To test channel primary address is set in message if there is a valid endpoint has been already
	 * set in the message. the valid message is NOT primary address
	 * @see org.community.grahak.interceptors.ResloveServiceEndpointInterceptor#handleMessage
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageValidNonPrimaryEndpointInMessage() throws Fault {
		ResloveServiceEndpointInterceptor interceptor = new ResloveServiceEndpointInterceptor();
		Channel channel = TestUtils.createChannelWithMultipleAddresses();
		Message message = TestUtils.createMessage(channel, TestUtils.createHttpConduit(getBus()));
		message.put(Message.ENDPOINT_ADDRESS, "http://host1:1111/x/y");
		//to check that before handleMessage primary address is 2222
		assertEquals("http://host2:2222/x/y", channel.getPrimaryEndpointUrl());
		
		interceptor.handleMessage(message);
		
		assertEquals("http://host1:1111/x/y", channel.getPrimaryEndpointUrl());
		assertEquals(channel.getPrimaryEndpointUrl(), message.get(Message.ENDPOINT_ADDRESS));
	}
	
	//(String)getRequestContext(message).get(Channel.SERVICE_NAME);
	
	/**
	 * To test service name is appended in the enpoint url
	 * @see org.community.grahak.interceptors.ResloveServiceEndpointInterceptor#handleMessage
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageNoEndpointHasServicenameInMessage() throws Fault {
		ResloveServiceEndpointInterceptor interceptor = new ResloveServiceEndpointInterceptor();
		Channel channel = TestUtils.createChannelWithMultipleAddresses();
		Message message = TestUtils.createMessage(channel, "serviceName" , TestUtils.createHttpConduit(getBus()));
		//message.put(Message.ENDPOINT_ADDRESS, "http://host1:1111/x/y");
		
		interceptor.handleMessage(message);
		
		assertEquals("http://host2:2222/x/y", channel.getPrimaryEndpointUrl());
		assertEquals(channel.getPrimaryEndpointUrl()+ "/serviceName", message.get(Message.ENDPOINT_ADDRESS));
	}
	
	/**
	 * To test blank service name is NOT appended in the enpoint url
	 * @see org.community.grahak.interceptors.ResloveServiceEndpointInterceptor#handleMessage
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageNoEndpointHasBlankServicenameInMessage() throws Fault {
		ResloveServiceEndpointInterceptor interceptor = new ResloveServiceEndpointInterceptor();
		Channel channel = TestUtils.createChannelWithMultipleAddresses();
		Message message = TestUtils.createMessage(channel, "    " , TestUtils.createHttpConduit(getBus()));
		//message.put(Message.ENDPOINT_ADDRESS, "http://host1:1111/x/y");
		interceptor.handleMessage(message);
		
		assertEquals("http://host2:2222/x/y", channel.getPrimaryEndpointUrl());
		assertEquals(channel.getPrimaryEndpointUrl(), message.get(Message.ENDPOINT_ADDRESS));
	}

}
