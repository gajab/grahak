package org.community.grahak.cxf;



import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.xml.ws.Holder;

import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.IChannelRegistry;
import org.community.grahak.channel.Channel.SERVICE_TYPE;
import org.community.grahak.context.IServiceRequestContext;
import org.community.grahak.cxf.ClientProxyFactoryBeanFactory;
import org.community.grahak.cxf.ServiceProxyFactory;
import org.community.grahak.cxf.jaxws.GrahakJaxWsProxyFactoryBean;
import org.community.grahak.util.GrahakCache;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceProxy implements InvocationHandler {

	private static Logger log = LoggerFactory.getLogger(ServiceProxy.class);

	private String endPoint;
	private ClientProxyFactoryBeanFactory.ENDPOINT_TYPE endpointType;

	private ApplicationContext applicationContext;
	private ServiceProxyFactory serviceProxyFactory;
	private IChannelRegistry channelRegistry;
	private String serviceName;
	private GrahakCache serviceAccessCache;
	
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock read = rwl.readLock();
	private final Lock write = rwl.writeLock();

	private Class<?> serviceClass;
	

	
	public ServiceProxy(String endPoint, ClientProxyFactoryBeanFactory.ENDPOINT_TYPE endpointType , 
			String serviceName,  Class<?> serviceClass, ApplicationContext applicationContext)
	{
		this.endPoint = endPoint; //can be channel name or endpoint
		this.endpointType = endpointType;
		this.applicationContext=applicationContext;
		serviceProxyFactory = (ServiceProxyFactory) applicationContext.getBean("serviceProxyFactory");
		this.channelRegistry = (IChannelRegistry) this.applicationContext.getBean("channelRegistry");
		this.serviceName = serviceName;
		this.serviceAccessCache = (GrahakCache) this.applicationContext.getBean("serviceAccessCache");
		this.serviceClass = serviceClass;
	}
	
	private String evaluateElExpression(String channelName) {
		
		BeanExpressionContext ctx = new BeanExpressionContext((ConfigurableBeanFactory) applicationContext.getAutowireCapableBeanFactory(), null);
		String value = (String)(((ConfigurableBeanFactory) applicationContext.getAutowireCapableBeanFactory()).getBeanExpressionResolver()).evaluate(channelName, ctx);
		//System.out.println("$$$$$$$$#{ " + value);
		return value;
	}

	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		IServiceRequestContext serviceRequestContext = 
			(IServiceRequestContext) applicationContext.getBean("serviceRequestContext");
		
		Channel channel=null;
		String name =this.endPoint;

		if(this.endPoint.startsWith("#{"))
		{
			name = evaluateElExpression(this.endPoint);
		}
		
		if(this.endpointType == ClientProxyFactoryBeanFactory.ENDPOINT_TYPE.CHANNEL)
		{
			channel = this.channelRegistry.getChannel(name);
			
			if(channel == null)
			{
				String msg = "Error creating client proxy as could not found channel config for channel id " + name;
				log.error(msg);
				throw new RuntimeException(msg);
			}
			
		}
		else if(this.endpointType == ClientProxyFactoryBeanFactory.ENDPOINT_TYPE.URL)
		{
			channel = new Channel();
			channel.initWithEndpointURL(name);
			channel.setId(name);
			
			channelRegistry.registerChannel(name, channel);
		}
		
		Object clientProxy=null;
		String STACK_TRACE = null;
		//check if JAXRS or JAXWS
		boolean isJAXRS = isJAXRS(method);
		if(isJAXRS)
		{
			STACK_TRACE = "org.apache.cxf.jaxrs.JaxRsClientProxy";
			clientProxy = getJAXRSProxy(channel, serviceRequestContext, method, args);
			channel.setServiceType(SERVICE_TYPE.REST);
		}
		else
		{
			STACK_TRACE = "org.apache.cxf.jaxws.JaxWsClientProxy";
			clientProxy = getJAXWSProxy(channel, serviceRequestContext, method, args);
		}
		
				
//		Annotation[][] paramAnns =  method.getParameterAnnotations();
//		int paramNumer = 0;
//		for (Annotation[] params : paramAnns) {
//			
//			for (Annotation annotation : params) {
//				if(annotation.annotationType() == WebParam.class)
//				{
//					args[paramNumer] = new Holder(((Object)args[paramNumer]));
//				}
//			}
//			
//			paramNumer++;
//		}
		
		Object ret = null;
		
		try
        {
			
			ret = method.invoke(clientProxy, args);
		}
		catch(Exception e)
		{
	        Throwable cause = e.getCause();
	        
//	        List<StackTraceElement> stack = new ArrayList<StackTraceElement>(Arrays.asList(cause.getStackTrace()));	   
//	        List<StackTraceElement> stackToRemove = new ArrayList<StackTraceElement>();
//        	
//	        boolean startRemove = false;
//        	
//	        //remove the stack track which was added by java.reflect due to proxies. to avoid confusion in stack track
//	        for (StackTraceElement stackTraceElement : stack) {
//	        	
//	        	if(STACK_TRACE.equals(stackTraceElement.getClassName()))
//	        		startRemove = true;
//	        	
//	        	if(startRemove)
//	        		stackToRemove.add(stackTraceElement);
//
//	        	if("org.community.grahak.cxf.ServiceProxy".equals(stackTraceElement.getClassName()))
//	        	{
//	        		//remove this inside this loop so that if this end condition is not meet then nothing is removed
//	        		stack.removeAll(stackToRemove);
//	        		//removed $Proxy track after this line
//	        		if(stack.size() > 0) //this wont happen
//	        		 stack.remove(0);
//	        		break;
//	        	}
//	    	}
//	        
//	        if(log.isDebugEnabled())
//	        {
//	        	for (StackTraceElement stackTraceElement : stackToRemove) {
//					log.debug("Additional stack info " + stackTraceElement.toString());
//				}
//	        }
//	        
//	        cause.setStackTrace(stack.toArray(new StackTraceElement[stack.size()]));
	        throw cause;
		}
		
		return ret;
	}

	private Object getJAXWSProxy(Channel channel,
			IServiceRequestContext serviceRequestContext, Method method,
			Object[] args) {
		
		serviceName = getServiceName( serviceName, serviceClass);
		
		String cacheKey = channel.getId() + ":" + serviceClass.getCanonicalName() + ":" + serviceName;
		Object clientProxy = this.serviceAccessCache.get(cacheKey);
		
		if(clientProxy == null)
		{
			
			    this.startRead();
			    
				clientProxy = this.serviceAccessCache.get(cacheKey);
				this.finishRead();
				
			    if( clientProxy == null)
				{

			    	this.startWrite();
			    	clientProxy = this.serviceAccessCache.get(cacheKey);
			    	if( clientProxy == null )
				    {
			    	
				    	if(log.isDebugEnabled())
						{
							log.debug("CREATING CXF PROXY with " + cacheKey);
						}
				    	GrahakJaxWsProxyFactoryBean jaxwsFactoryBean =  (GrahakJaxWsProxyFactoryBean) this.applicationContext.getBean("etJaxWsProxyFactory");
				    	jaxwsFactoryBean.setServiceClass(serviceClass);
				    	clientProxy = jaxwsFactoryBean.create(channel, serviceClass, serviceName);
				    	this.serviceAccessCache.add(cacheKey, clientProxy);
				    	
				    }
			    	this.finishWrite();
				}
		}

		return clientProxy;
	}

	private Object getJAXRSProxy(Channel channel,
			IServiceRequestContext serviceRequestContext, Method method,
			Object[] args) {
		
		JAXRSClientFactoryBean jaxrsClientFactoryBean = new JAXRSClientFactoryBean();
		
		Client client = serviceProxyFactory.createProxyForJaxRSService(jaxrsClientFactoryBean, channel, 
				method.getDeclaringClass(),serviceRequestContext);
		
		return client;
	}

	private boolean isJAXRS(Method method) {
		Annotation[] annotations = method.getAnnotations();
		for (Annotation annotation : annotations) {
			
			if(annotation.annotationType().equals(GET.class)
					|| annotation.annotationType().equals(POST.class)
					|| annotation.annotationType().equals(PUT.class)
					|| annotation.annotationType().equals(DELETE.class)
				
					)
				return true;
			
		}
		return false;
	}
	
	
	private void finishWrite() {
		write.unlock();
		
	}

	private void startWrite() {
		write.lock();		
	}
	
	private void startRead()
	{
		read.lock();
	}
	
	private void finishRead()
	{
		read.unlock();
	}
	
	private String getServiceName(final String serviceName, final Class<?> serviceClass)
	{
		//service name specified in annotation take precedence then the servicename defined our SEI
		if(serviceName != null && !serviceName.trim().isEmpty())
		{
			return serviceName;
		}
		/* the name provided on web service interface is port name and not the service name.
		 * the service name should be provided with @WebService (serviceName=""). but it is
		 * not mandatory by JAX-WS hence we can not reply on this. 
		 * Also we do not want to add the service name in channel configuration file as
		 * one channel should be used for different service calls hosted on same host(channel)*/
		//if there is servicename provided with @Webservice annotation then use that
		WebService webServiceAnnotation = (WebService) serviceClass.getAnnotation(WebService.class);
		if(null == webServiceAnnotation)
		{
			if(log.isDebugEnabled())
				log.debug("no @WebService annotation on interface " + serviceClass );
			
			return null;
			
		}
		String serviceNameOnInterface = webServiceAnnotation.serviceName();
		
		if(serviceNameOnInterface != null && serviceNameOnInterface.trim().length() !=0 )
		{
			if(log.isDebugEnabled())
				log.debug("found service name = " + serviceNameOnInterface + " on interface " + serviceClass );
			
			return serviceNameOnInterface;
		}
			
		return null;
	}
}
