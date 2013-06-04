package org.community.grahak.cxf;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jws.WebService;
import javax.xml.ws.BindingProvider;

//import org.apache.cxf.interceptor.AbstractBasicInterceptorProvider;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.jms.JMSConfigFeature;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.apache.cxf.transport.jms.spec.JMSSpecConstants;
import org.apache.cxf.transport.zmq.ZMQConfigFeature;
import org.apache.cxf.transport.zmq.ZMQConfiguration;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.clustering.RetryStrategy;
import org.apache.cxf.endpoint.AbstractEndpointFactory;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.community.grahak.channel.Channel;
import org.community.grahak.context.IServiceRequestContext;
import org.community.grahak.cxf.jaxws.GrahakJaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.SingleConnectionFactory;


@SuppressWarnings("rawtypes")
public class ServiceProxyFactory {

	private static final Logger log = LoggerFactory.getLogger(ServiceProxyFactory.class);
	
	@Autowired
	private SingleConnectionFactory jmsConnectionFactory;

    /** these maps are wired in from spring-context **/
    private Map<String,List> SOAP; 
    private Map<String,List> XML; 
    private Map<String,List> LOCAL; 
    private Map<String,List> REST;

    private Map<String,Map<String,List>> TYPE_MAP = new HashMap<String,Map<String,List>>();

    private static String FEATURES = "features";
    private static String IN_INTERCEPTORS = "inInterceptors";
    private static String OUT_INTERCEPTORS = "outInterceptors";
    private static String IN_FAULT_INTERCEPTORS = "inFaultInterceptors";

    /** this should be called as part of spring initialization method **/
    public void init()
    {
        TYPE_MAP.put(Channel.SERVICE_TYPE.SOAP.getValue(),SOAP);
        TYPE_MAP.put(Channel.SERVICE_TYPE.XML.getValue(),XML);
        TYPE_MAP.put(Channel.SERVICE_TYPE.REST.getValue(),REST);
        TYPE_MAP.put("local",LOCAL);
    }

    public void setLOCAL(Map<String,List> local)
    {
        LOCAL = local;
    }

    public void setXML(Map<String,List> xml)
    {
        XML = xml;
    }

    public void setSOAP(Map<String,List> soap)
    {
        SOAP = soap;
    }

    public void setREST(Map<String,List> rest)
    {
        REST = rest;
    }

    public Map<String,List> getPolicyMap (final org.apache.cxf.interceptor.InterceptorProvider provider)
    {
       //NOTE: this is by reference (provider.getInInterceptors() etc ). 
    	//Any changes in policyMap will direct impact the provider (aka jaxws/jaxrs proxy)
    	Map<String,List> policyMap = new HashMap<String,List>(); 
       policyMap.put(IN_INTERCEPTORS,provider.getInInterceptors());
       policyMap.put(OUT_INTERCEPTORS,provider.getOutInterceptors());
       policyMap.put(IN_FAULT_INTERCEPTORS,provider.getInFaultInterceptors());
       
       return policyMap;
    }

    public void initBindings(final GrahakJaxWsProxyFactoryBean etJaxWsProxyFactoryBean,String mapFromStr)
    {
        if ( "local".equals(mapFromStr))
        {
            etJaxWsProxyFactoryBean.setTransportId(org.apache.cxf.transport.local.LocalTransportFactory.TRANSPORT_ID);
        }
        else if(Channel.SERVICE_TYPE.XML.getValue().equals(mapFromStr))
        {
            etJaxWsProxyFactoryBean.setTransportId(org.apache.cxf.common.WSDLConstants.NS_BINDING_XML);
            etJaxWsProxyFactoryBean.setBindingId(org.apache.cxf.common.WSDLConstants.NS_BINDING_XML);
        }
        else if("jms".equals(mapFromStr))
        {
        	etJaxWsProxyFactoryBean.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);

        }
        /** this may not be required since it's default ** 
        else if ( "local".equals(mapFromStr))
        {
            etJaxWsProxyFactoryBean.setTransportId(org.apache.cxf.transport.local.LocalTransportFactory.TRANSPORT_ID);
            //etJaxWsProxyFactoryBean.setBindingId(org.apache.cxf.transport.local.LocalTransportFactory.TRANSPORT_ID);
        }
        else if( Channel.SERVICE_TYPE.SOAP.equals(serviceType))
        {
            etJaxWsProxyFactoryBean.setTransportId(null);
            etJaxWsProxyFactoryBean.setBindingId(null);
        }
        ***/
    }

    public void setProxyBindings(final InterceptorProvider provider,Map<String,List> typeMap ,Map<String,List> policyMap, final Channel channel)
    {
        //typeMap entries contains values => TYPE_MAP_VALUES
        //failOverFeature, inInterceptors,outInterceptors, faultInterceptors
        Set<Map.Entry<String,List>> typeSet = typeMap.entrySet();
        //iterate the map entries and add them to components 
        //for example add all inInterceptors ..
        for ( Map.Entry<String,List> typeMapEntry : typeSet)
        {
            String key = typeMapEntry.getKey();
            List listVal = typeMapEntry.getValue();
            if ( log.isDebugEnabled())
                log.debug("[Key = "+ key + "] [value=" + listVal + "]"); 
            
            // use the policyMap to add to the list
            List policyList = policyMap.get(key);
            for ( Object obj : listVal )
            {
                if(obj instanceof RetryStrategy)
                {
                	((RetryStrategy)obj).setMaxNumberOfRetries(channel.getConnection().getMaxNumberOfRetries());
                }
            	policyList.add(obj);
            }
        }
    }


    public Object createProxyForJaxWSService(final GrahakJaxWsProxyFactoryBean etJaxWsProxyFactoryBean,
                                             final Channel channel,
                                             Class<?> serviceClass, final String serviceName)
    {
        // this should be one of SOAP,XML,LOCAL
        // the channel serviceType should match above
        String protocol = "zmq";//channel.getPrimaryAddress().getProtocol();
        String serviceType = channel.getServiceType().getValue();
        String mapFromStr = "local".equals(protocol)? protocol : serviceType;

        Map<String,List> typeMap = TYPE_MAP.get(mapFromStr);

        if ( log.isDebugEnabled())
        {
            log.debug("[Protocol = " + protocol + "]");
            log.debug("[ServiceType = " + serviceType +"]");
            log.debug("[MapFromStr = " + mapFromStr +"]");
            log.debug("[TypeMap."+protocol+"."+serviceType+" = "+ typeMap +"]");
        }

        Map<String,List> policyMap = getPolicyMap(etJaxWsProxyFactoryBean);
        policyMap.put(FEATURES,etJaxWsProxyFactoryBean.getFeatures());

        initBindings(etJaxWsProxyFactoryBean,mapFromStr);
        setProxyBindings(etJaxWsProxyFactoryBean,typeMap,policyMap,channel);

        if ( "local".equals(mapFromStr))
        {
            final ArrayList<Interceptor <? extends Message>> outInterceptors = new ArrayList<Interceptor <? extends Message>>();
            etJaxWsProxyFactoryBean.setOutInterceptors(outInterceptors);
        }
        
        if("jms".equals(protocol))
        {
        	JMSConfigFeature feature = new JMSConfigFeature();

            
            JMSConfiguration jmsConfig = new JMSConfiguration();
            
            jmsConfig.setRequestURI(channel.getJMSEndpoint());
            //jmsConfig.setTargetDestination(channel.getConnection().getJmsConfig().getTargetDestination());
            
            feature.setJmsConfig(jmsConfig);

            
        	List<Feature> features = etJaxWsProxyFactoryBean.getFeatures();
        	features.add(feature);
        	etJaxWsProxyFactoryBean.setFeatures(features);
        	
        	
        	((ActiveMQConnectionFactory)jmsConnectionFactory.getTargetConnectionFactory())
        						.setBrokerURL(channel.getJMSBrokerUrl());
        	
        	jmsConfig.setConnectionFactory(jmsConnectionFactory);
           
        }
        if("zmq".equals(protocol))
        {
        	ZMQConfigFeature feature = new ZMQConfigFeature();
        	ZMQConfiguration config = new ZMQConfiguration();
        	config.setIoThreads(2);
        	feature.setZmqConfig(config);
        	List<Feature> features = etJaxWsProxyFactoryBean.getFeatures();
        	features.add(feature);
        	etJaxWsProxyFactoryBean.setFeatures(features);
        	
        }
        
	   // String url = channel.getPrimaryEndpointUrl(); // for jms it will return jms://
	    etJaxWsProxyFactoryBean.setAddress("zmq:(tcp://localhost:9000?socketOperation=connect&socketType=req)");
	    
	    etJaxWsProxyFactoryBean.setServiceClass(serviceClass); //TODO: added new test
	    
	    Object proxy = etJaxWsProxyFactoryBean.create();
	    setRequestContext(proxy, channel, serviceClass, serviceName);
        return proxy;
    }

	public Client createProxyForJaxRSService( JAXRSClientFactoryBean jaxRSClientFactoryBean, Channel channel,
			                                  Class<?> serviceClass, IServiceRequestContext serviceRequestContext) 
    {
		if (log.isDebugEnabled())
	    	log.debug("creating JAXRS service " + channel.getId() + ", " + serviceClass );

        // this should be just REST
        // the channel serviceType should match 
        String serviceType = channel.getServiceType().getValue();
        Map<String,List> serviceTypeMap = TYPE_MAP.get(serviceType);

        if ( log.isDebugEnabled())
        {
            log.debug("[ServiceType = " + serviceType +"]");
            log.debug("[TYPE_MAP = "+ TYPE_MAP +"]");
            log.debug("[ServiceTypeMap.SOAP = "+ serviceTypeMap +"]");
        }

        Map<String,List> policyMap = getPolicyMap(jaxRSClientFactoryBean);
        //policyMap.put(FAIL_OVER_FEATURE,jaxRSClientFactoryBean.getFeatures());

        setProxyBindings(jaxRSClientFactoryBean,serviceTypeMap,policyMap,channel);
        	
    	//TODO: This had to be done here as features was not getting invoke when set in configuration file
   		//setting feature for failover feature
    	/************************************************
    	List<AbstractFeature> features = new ArrayList<AbstractFeature>();
    	features.add((AbstractFeature) this.applicationContext.getBean("jaxrsCluster"));
   	
    	//jaxRSClientFactoryBean.setFeatures(features);
    	//addFailoverFeature(jaxRSClientFactoryBean);
        //failOverFeature
        List<AbstractFeature> features = new ArrayList<AbstractFeature>();
	    features.add((AbstractFeature)failOverFeature);
	    jaxRSClientFactoryBean.setFeatures(features);
        ******************************************/
       
        if(serviceRequestContext.getUserName() != null)
        	jaxRSClientFactoryBean.setUsername(serviceRequestContext.getUserName());
        if(serviceRequestContext.getPassword() != null)
        	jaxRSClientFactoryBean.setPassword(serviceRequestContext.getPassword());
		if(serviceRequestContext.getHeaders() != null)
			jaxRSClientFactoryBean.setHeaders(serviceRequestContext.getHeaders());
		
		jaxRSClientFactoryBean.setServiceClass(serviceClass);
		jaxRSClientFactoryBean.setAddress(channel.getPrimaryEndpointUrl());
		Client client = jaxRSClientFactoryBean.create();
		ClientConfiguration clientConfig  = WebClient.getConfig(client);
		clientConfig.getRequestContext().put(Channel.CHANNEL, channel);
		client.header("Accept", "*");		
		
		return client;
	}

    //TODO : refactor code 
    // This is a client to monitor different proxy objects that map to channels and serviceName
	private void setRequestContext(Object proxy, Channel channel, Class serviceClass, String serviceName)
	{
        String contextSvcName = null;
        WebService webServiceAnnotation = (WebService) serviceClass.getAnnotation(WebService.class);
        if ( webServiceAnnotation != null )
        {
            String svcNameParam = webServiceAnnotation.serviceName();
            if (svcNameParam != null && svcNameParam.trim().length() != 0 )
            {
                contextSvcName = svcNameParam;
                log.info("contextSvcName from annot.serviceName="+contextSvcName);
            }
            else
            {
                String nameParam = webServiceAnnotation.name();
                if (nameParam != null && nameParam.trim().length() != 0 )
                {
                    contextSvcName = nameParam;
                    log.info("contextSvcName from annot.name="+contextSvcName);
                }
            }
        }

        if ( contextSvcName == null )
        {
            if (serviceName != null && serviceName.trim().length() != 0)
            {
                contextSvcName = serviceName;
                log.info("contextSvcName from class.serviceName="+contextSvcName);
            }
        }

        if ( contextSvcName == null )
            log.info(" channel " + channel.getId() + " has " + channel.getBaseUri() + " base URI - no serviceName defined");
        else
            ((BindingProvider) proxy).getRequestContext().put(Channel.CONTEXT_SERVICE_NAME, contextSvcName);
        
        if (serviceName != null && serviceName.trim().length() != 0)
			((BindingProvider) proxy).getRequestContext().put(Channel.SERVICE_NAME, serviceName);

		((BindingProvider) proxy).getRequestContext().put(Channel.CHANNEL, channel);
	}
}
