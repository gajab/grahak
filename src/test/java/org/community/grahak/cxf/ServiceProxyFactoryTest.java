package org.community.grahak.cxf;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.clustering.FailoverFeature;
import org.community.grahak.TestUtils;
import org.community.grahak.channel.Address;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.Channel.SERVICE_TYPE;
import org.community.grahak.context.ServiceRequestContext;
import org.community.grahak.cxf.ServiceProxyFactory;
import org.community.grahak.cxf.jaxws.GrahakJaxWsProxyFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/config/grahak-context.xml"})
public class ServiceProxyFactoryTest 
{
    @Autowired
    private ServiceProxyFactory spf;
    @Autowired
    private GrahakJaxWsProxyFactoryBean etJaxWsProxyFactoryBean; 
    @Autowired
    private ServiceRequestContext serviceRequestContext; 
	private Class<?> serviceClass;
	private Class<?> serviceClassForJaxrs;	
	private String serviceName = "Echo";
	private String baseUri = "/echo-jws-service/services";
	private String host = "localhost";

	@Before
	public void setUp() throws Exception {

		serviceClassForJaxrs = Class.forName("org.community.grahak.cxf.jaxrs.EchoService");
		serviceClass = Class.forName("org.community.grahak.cxf.jaxws.EchoService");
	}

	//@Test
	public void testProxyForSoapService() throws Throwable 
    {
		Channel  channel = createChannel("http");
		channel.setServiceType(SERVICE_TYPE.SOAP);				
		etJaxWsProxyFactoryBean.setServiceClass(serviceClass);	
		Object obj = spf.createProxyForJaxWSService(etJaxWsProxyFactoryBean, channel, serviceClass, serviceName);
		assertNotNull("The proxy object of Soap Service should not be null",obj);
	}

	@Test
	public void testProxyForLocalService() throws Throwable {

		Channel  channel = createChannel("local");
		etJaxWsProxyFactoryBean.setServiceClass(serviceClass);	
		Object obj = spf.createProxyForJaxWSService(etJaxWsProxyFactoryBean,channel, serviceClass, serviceName);
		assertNotNull("The proxy object of Local Service should not be null",obj);
	}




	//@Test
	public void testProxyForJaxRSService() throws Throwable 
    {
		Channel  channel = createChannel("http");
		channel.setServiceType(SERVICE_TYPE.REST);	
		channel.setBaseUri("/");
        JAXRSClientFactoryBean jaxrsClientFactoryBean = new JAXRSClientFactoryBean();
		jaxrsClientFactoryBean.setServiceClass(serviceClassForJaxrs);	
		Object obj = spf.createProxyForJaxRSService(jaxrsClientFactoryBean, channel, serviceClassForJaxrs, serviceRequestContext);
		assertNotNull("The proxy object of Jaxrs Service should not be null",obj);
	}

	private Channel createChannel(String protocol){

		Channel channel = TestUtils.createChannel("EchoId");
		List<Address> addresses = new ArrayList<Address>();
		Address add = new Address();
		add.setHost(host);
		add.setPort("8080");
		add.setPrimary(true);
		add.setProtocol(protocol);		
		addresses.add(add);		
		channel.setAddresses(addresses);
		channel.setBaseUri(baseUri);
		return channel;		
	}

	@After 
	public void tearDown() {

		serviceClass = null;
		//failOverFeature = null;
	} 
}
