package org.community.grahak.cxf;

import java.lang.reflect.Proxy;

import org.community.configloader.spring.ConfigUpdateWatcher;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.ConfigChangeObserver;
import org.community.grahak.channel.IChannelRegistry;
import org.community.grahak.util.ServiceCallHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class ClientProxyFactoryBeanFactory implements ApplicationContextAware {
	
	private static Logger log = LoggerFactory.getLogger(ClientProxyFactoryBeanFactory.class);
	
	public static enum ENDPOINT_TYPE { CHANNEL, URL} ;

	//private EtJaxWsProxyFactoryBean jaxwsFactoryBean;
	private ApplicationContext applicationContext;
	//private IChannelConfigReader channelConfigReader;
	private IChannelRegistry channelRegistry;
	
	
	private ServiceCallHandler serviceCallHandler;
//	private JaxRsClientAOPInterceptor jaxrsAOPInterceptor;
	
	public void setChannelRegistry(IChannelRegistry channelRegistry) {
		this.channelRegistry = channelRegistry;
	}

	//this is called by spring (configure init-method on this bean) after all properties are set
	public void init()
	{
//		if(channelConfigReader==null)
//		{
//			//TODO: since this is called from BeanPostProcessor, autowired or getter/setter from context
//			//file wont work - check it again
//			//Filed a JIRA http://support.springsource.com/spring_support_client_getIncidentById/9174 
//			channelConfigReader = this.applicationContext.getBean(ChannelConfigReader.class);
//			if(channelConfigReader==null)
//			{
//				String msg="channel config reader is null";
//				log.error(msg);
//				throw new RuntimeException(msg);
//			}
//		}
		ConfigUpdateWatcher watcher = this.applicationContext.getBean(ConfigUpdateWatcher.class);
		
		watcher.registerObserver(this.applicationContext.getBean(ConfigChangeObserver.class));
		channelRegistry.registerObserver(serviceCallHandler);
		
		watcher.work();
		//channelConfigReader.read();
		//TODO: Rahul  - there should be better way
		//whenever we do read we have to initialize the serviceCallHandler
		//serviceCallHandler.init(channelConfigReader.getChannelConfig().getChannels());
	}
	//multiple create methods : used in client xml configuration
	public Object create(final Channel channel, final Class<?> serviceClass) {
		
		channelRegistry.registerChannel(channel.getId(), channel);
		return create(channel.getId(), ENDPOINT_TYPE.CHANNEL, null, serviceClass);
		
	}
	
	public Object create(final Channel channel, final String serviceName,  final Class<?> serviceClass) {
		
		channelRegistry.registerChannel(channel.getId(), channel);
		return create(channel.getId(), ENDPOINT_TYPE.CHANNEL, serviceName, serviceClass);
		
	}
	
	public Object create(final String channelName, final String serviceName, final Class<?> serviceClass) {
		
		return create(channelName, ENDPOINT_TYPE.CHANNEL, serviceName, serviceClass);
		
	}
	

	public Object create(final String channelName, final Class<?> serviceClass) {
		
		return create(channelName, ENDPOINT_TYPE.CHANNEL, null, serviceClass);
		
	}

	
	public Object create(final String endpoint, final ENDPOINT_TYPE endpointType,
			final String serviceName, final Class<?> serviceClass, Object... varValues) {
		
		return Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class [] {serviceClass}, 
				new ServiceProxy(endpoint, endpointType, serviceName, serviceClass, applicationContext));
	}

	
	@Override
	public void setApplicationContext(final ApplicationContext appContext)
			throws BeansException {
		this.applicationContext=appContext;		
	}
	

	public void setServiceCallHandler(ServiceCallHandler serviceCallHandler) {
		this.serviceCallHandler = serviceCallHandler;
	}



}
