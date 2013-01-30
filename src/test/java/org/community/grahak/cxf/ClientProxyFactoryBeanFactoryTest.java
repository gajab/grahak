package org.community.grahak.cxf;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.community.grahak.TestUtils;
import org.community.grahak.channel.Address;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.ChannelConfig;
import org.community.grahak.channel.IChannelConfigReader;
import org.community.grahak.channel.Channel.SERVICE_TYPE;
import org.community.grahak.cxf.ClientProxyFactoryBeanFactory;
import org.community.grahak.cxf.ClientProxyFactoryBeanFactory.ENDPOINT_TYPE;
import org.community.grahak.util.ServiceCallHandler;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ClientProxyFactoryBeanFactoryTest {

	private ApplicationContext applicationContext;
	private IChannelConfigReader channelConfigReader;	 
	private Channel channel;
	private ChannelConfig channelConfig;
	private Class<?> serviceClass ;

	@Before
	public void setUp() throws Exception {

		channel = TestUtils.createChannel("EchoId");
		List<Address> addresses = new ArrayList<Address>();
		Address add = new Address();
		add.setHost("localhost");
		add.setPort("8080");
		add.setPrimary(true);
		add.setProtocol("http");		
		addresses.add(add);		
		channel.setAddresses(addresses);
		channel.setBaseUri("/echo-jws-service/services");
		
		channelConfigReader = EasyMock.createMock(IChannelConfigReader.class);
		channelConfig = EasyMock.createMock(ChannelConfig.class);
		serviceClass = Class.forName("org.community.grahak.cxf.jaxws.EchoService");
		applicationContext = new ClassPathXmlApplicationContext(new String[] {
		"/config/grahak-context.xml"});		
	}

	@Test
	public void testJaxWsProxy() throws Throwable {
		
	    ClientProxyFactoryBeanFactory cbf = new ClientProxyFactoryBeanFactory();
	    cbf.setApplicationContext(applicationContext);
	    cbf.setServiceCallHandler(applicationContext.getBean(ServiceCallHandler.class));
	   // cbf.setChannelConfigReader(channelConfigReader);
		//EasyMock.expect( channelConfigReader.getChannelConfig()).andReturn(channelConfig);			
		EasyMock.expect(channelConfig.getChannel("EchoLocalServiceChannel")).andReturn(channel);
		EasyMock.replay(channelConfig);
		EasyMock.replay(channelConfigReader);		
		Object obj = cbf.create("EchoLocalServiceChannel", ENDPOINT_TYPE.CHANNEL,"Echo", serviceClass, new String("company"));
		assertNotNull("The Object of JaxWsProxy should not be null", obj);			
	}

	@Test
	public void testJaxRsProxy() throws Throwable {  
		
		ClientProxyFactoryBeanFactory cbf = new ClientProxyFactoryBeanFactory();
		cbf.setApplicationContext(applicationContext);
		cbf.setApplicationContext(applicationContext);
	    cbf.setServiceCallHandler(applicationContext.getBean(ServiceCallHandler.class));
	   //cbf.setChannelConfigReader(channelConfigReader);
		channel.setServiceType(SERVICE_TYPE.REST);				
		//EasyMock.expect( channelConfigReader.getChannelConfig()).andReturn(channelConfig);			
		EasyMock.expect(channelConfig.getChannel("EchoLocalServiceChannel")).andReturn(channel);
		EasyMock.replay(channelConfig);
		EasyMock.replay(channelConfigReader);				
		Object obj = cbf.create("EchoLocalServiceChannel", ENDPOINT_TYPE.CHANNEL, "Echo", serviceClass, new String("company"));
		assertNotNull("The Object of JaxRsProxy should not be null", obj);			
	}
		
	@After 
	public void tearDown() {
		
	  serviceClass=null;   
	  channelConfigReader = null;
	  channelConfig = null;
	  channel = null;
	  applicationContext = null;
	} 
}
