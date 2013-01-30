package org.community.grahak.interceptors;


import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.community.grahak.channel.Channel;
import org.community.grahak.util.GrahakHelper;
import org.community.grahak.util.ServiceCallHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This interceptor is invoked as a part of SETUP phase to throttle number of
 * parallel service calls per channel.
 * 
 * @author rkarwa
 *
 */
public  class ThrottleServiceCallsInterceptor extends AbstractPhaseInterceptor<Message>  {

	private static Logger log = LoggerFactory.getLogger(ThrottleServiceCallsInterceptor.class);
	
	/**
	 * Service call handler to keep number of service call per channel in progress to allow
	 * certain numbers of parallel calls for a channel specified in channel configuration file
	 */
	private ServiceCallHandler serviceCallHandler;
	
	/**
	 * SETUP_ENDING interceptor called to release handle for the service call
	 */
	private ServiceCallRecievedInterceptor ending = new ServiceCallRecievedInterceptor();

	public ThrottleServiceCallsInterceptor() {
        super(Phase.SETUP);
    }
	
    /**
     * injected by spring configuration
     * @param serviceCallHandler
     */
    public void setServiceCallHandler(ServiceCallHandler serviceCallHandler) {
		this.serviceCallHandler = serviceCallHandler;
	}


	@Override
	public void handleMessage(Message message) throws Fault {
		
		Channel channel = GrahakHelper.getChannel(message);
		
		message.getInterceptorChain().add(ending);
		
		if(!serviceCallHandler.isCallAllowed(channel))
		{
			String msg = "Not more than " + channel.getConnection().getMaxThreads() 
						 + " calls are allowed for " + channel.getPrimaryEndpointUrl();
			log.error(msg);
			
			throw new Fault(Thread.currentThread().getId() + msg , 
					java.util.logging.Logger.getLogger(ThrottleServiceCallsInterceptor.class.getName()));
		}
		
		if(log.isDebugEnabled())
			log.debug("calling service for channel " + channel.getId());
	}
	
	//handle the fault, this will be called during interceptor unwind when any exception/fault occurred in
	// any interceptor after this interceptor. we need to release the service call handler
	@Override
	public void handleFault(Message message) {
		
		Channel channel = GrahakHelper.getChannel(message);
		
    	serviceCallHandler.releaseHandle(channel);
    	
    	if(log.isDebugEnabled())
			log.debug("releasing service handle in handlefault for channel " + channel.getId());
    	
    }
	
	/**
	 * This interceptor would be call after receiving and processing the response
	 * @author rkarwa
	 *
	 */
	public class ServiceCallRecievedInterceptor extends AbstractPhaseInterceptor<Message> {
        
		public ServiceCallRecievedInterceptor() {
            
        	super(Phase.SETUP_ENDING);
        }

        public void handleMessage(Message message) throws Fault {
        	
        	//channel can not be instance variable 
    		Channel channel = GrahakHelper.getChannel(message);
    		
        	serviceCallHandler.releaseHandle(channel);
        	
        	if(log.isDebugEnabled())
    			log.debug("releasing service handle for channel " + channel.getId());
        	
    		
        }
    }
}
