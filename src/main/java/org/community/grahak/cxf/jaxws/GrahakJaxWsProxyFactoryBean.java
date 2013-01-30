package org.community.grahak.cxf.jaxws;


import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.message.Message;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.Channel.SERVICE_TYPE;
import org.community.grahak.cxf.ServiceProxyFactory;
import org.community.grahak.spring.bean.factory.annotation.GrahakAnnotationBeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * This class extends Apache CXF's {@link JaxWsProxyFactoryBean} to override the proxy creation method.
 * The {@link GrahakAnnotationBeanPostProcessor} calls its create() method after setting any additional 
 * parameters (like channel) required to for customized proxy creation.  
 * 
 * @author rkarwa
 *
 */
public class GrahakJaxWsProxyFactoryBean extends JaxWsProxyFactoryBean  
{
	private static Logger log = LoggerFactory.getLogger(GrahakJaxWsProxyFactoryBean.class);
	
	private ServiceProxyFactory serviceProxyFactory;

	public void setServiceProxyFactory(ServiceProxyFactory serviceProxyFactory) {
		this.serviceProxyFactory = serviceProxyFactory;
	}
	
	@SuppressWarnings("rawtypes")
	private void init() {
		
		List<Interceptor <? extends Message>> outInterceptors = new ArrayList<Interceptor <? extends Message>>();
		List<Interceptor <? extends Message>> inInterceptors = new ArrayList<Interceptor <? extends Message>>();
		List<Interceptor <? extends Message>> faultInterceptors = new ArrayList<Interceptor <? extends Message>>();
		List<AbstractFeature> features = new ArrayList<AbstractFeature>();
	    
	    this.setTransportId(null);
	    this.setBindingId(null);
	    
	    this.setOutInterceptors(outInterceptors);
	    this.setInInterceptors(inInterceptors);
	    this.setInFaultInterceptors(faultInterceptors);
	    this.setFeatures(features);

	    this.setAddress(null);
	}


	public Object create(Channel channel, Class<?> serviceClass, String serviceName) 
    {
		if (log.isDebugEnabled())
	    	log.debug("creating service proxy " + channel.getId() + ", "
                       + serviceClass + ", " + serviceName);
		init();
        return serviceProxyFactory.createProxyForJaxWSService(this,channel,serviceClass,serviceName);
	}
}
