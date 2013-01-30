package org.community.grahak.interceptors;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.phase.PhaseManagerImpl;
import org.apache.cxf.transport.http.HTTPConduit;
import org.community.grahak.TestUtils;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.ChannelRegistry;
import org.community.grahak.channel.IChannelRegistry;
import org.community.grahak.interceptors.ThrottleServiceCallsInterceptor;
import org.community.grahak.util.ServiceCallHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ThrottleServiceCallsInterceptorTest
{
	PhaseInterceptorChain chain;
	Message message;
	Bus bus;	
	ServiceCallHandler serviceCallHandler;
	ThrottleServiceCallsInterceptor throttleInterceptor; 
	List<Channel> channels;

	@Before
	public void setUp() throws Exception {

		bus = BusFactory.newInstance().createBus();

		Channel channel = TestUtils.createChannel("Test");		
		HTTPConduit httpcon= TestUtils.createHttpConduit(bus);
		message = TestUtils.createMessage(channel,httpcon);
		//channels= new ArrayList<Channel>();
		//channels.add(channel);		
		IChannelRegistry  channelReg = new ChannelRegistry();
		channelReg.registerChannel(channel.getId(), channel);
		serviceCallHandler = new ServiceCallHandler();
		serviceCallHandler.setChannelRegistry(channelReg);
		serviceCallHandler.init();
		for(int i=0;i<50;i++){
			serviceCallHandler.isCallAllowed(channel);	
		}

		throttleInterceptor = new ThrottleServiceCallsInterceptor();
		throttleInterceptor.setServiceCallHandler(serviceCallHandler);
	}

	@Test
	public void testParllelServiceCall() throws Exception {	

		InterceptorChain chain = new PhaseInterceptorChain((new PhaseManagerImpl()).getOutPhases());
		message.setInterceptorChain(chain);
		Exception exception = null;

		try{
			throttleInterceptor.handleMessage(message);
		}catch(Fault fault){
			exception = fault;
		}		

		Assert.assertNotNull("Parallel service calls per channel exceeded its limit...", exception);
	}	

	@After 
	public void tearDown() {

		bus.shutdown(true);         
	} 
}
