package org.community.grahak.interceptors;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.test.AbstractCXFTest;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.community.grahak.TestUtils;
import org.community.grahak.channel.ConnectionInfo;
import org.community.grahak.interceptors.ApplyHttpClientPolicyInterceptor;
import org.junit.Test;



public class ApplyHttpClientPolicyInterceptorTest extends AbstractCXFTest {

	/**
	 * To test that HttpClientPolicy object is correctly copied from channel object
	 * @see org.community.grahak.interceptors.ApplyHttpClientPolicyInterceptor#handleMessage
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageForConnectionParameters() throws Fault {
		
		final HTTPConduit conduit = TestUtils.createHttpConduit(bus);
		
		final ApplyHttpClientPolicyInterceptor interceptor = new ApplyHttpClientPolicyInterceptor();
		interceptor.handleMessage(TestUtils.createMessage(TestUtils.createChannel("id"), conduit));
		
		final HTTPClientPolicy clientPolicy = conduit.getClient();
		
		assertEquals("", 1000, clientPolicy.getConnectionTimeout()); 
		assertEquals("", 1500, clientPolicy.getReceiveTimeout()); 
		assertEquals("", ConnectionType.KEEP_ALIVE, clientPolicy.getConnection()); 

		assertEquals("", ConnectionInfo.DFLT_ALLOW_CHUNKING, clientPolicy.isAllowChunking()); 
}
	
	@Test
	public void testHandleMessageForSecureConnection() throws Fault {
		
		HTTPConduit conduit = TestUtils.createHttpConduit(bus);
		
		ApplyHttpClientPolicyInterceptor interceptor = new ApplyHttpClientPolicyInterceptor();
		interceptor.handleMessage(TestUtils.createMessage(TestUtils.createSecureChannel("id2"), conduit));
		
		assertNotNull(conduit.getTlsClientParameters()); 
		assertNotNull(conduit.getTlsClientParameters().getTrustManagers()[0]); 
		
	}
	
	/**
	 * null channel will only cause no client policies been set. It should simply
	 * come out from handle message
	 * @throws Fault
	 */
	@Test
	public void testHandleMessageNullChannel() throws Fault {
		
		HTTPConduit conduit = TestUtils.createHttpConduit(bus);
		
		ApplyHttpClientPolicyInterceptor interceptor = new ApplyHttpClientPolicyInterceptor();
		interceptor.handleMessage(TestUtils.createMessage(null, conduit));
		
		//no exception means everything good
		
	}

//	@Override
//	protected String[] getConfigLocations() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
/*	void testHandleMessage()
	{
				//conduitMock.setClient(client);
		
		//.put(HTTPConduit.class, conduitMock);
		//EasyMock.expect(messageMock.put("org.apache.cxf.message.Exchange", exchangeMock)).andReturn(null);
		//messageMock.put("org.apache.cxf.message.Exchange", exchangeMock);

		//EasyMock.expect(messageMock.get(Message.INVOCATION_CONTEXT)).andReturn(context);
		//EasyMock.expect(messageMock.get(Exchange.class)).andReturn(exchangeMock);
		//replay(messageMock);
//		messageMock.put(Message.INVOCATION_CONTEXT, context);;
		
		//EasyMock.expect(messageMock.put("org.apache.cxf.transport.http.HTTPConduit", context));
		
		
		//EasyMock.replay(messageMock);
		//exchangeMock.put("org.apache.cxf.transport.http.HTTPConduit", conduitMock);

		Exchange exchange = EasyMock.createMock(Exchange.class).add;
		
		EasyMock.expect(exchange.getConduit(messageMock)).andReturn(conduitMock);
		
		EasyMock.expect(conduitMock.setClient( isA(HttpClient.class)));
	}*/
		
		

}
