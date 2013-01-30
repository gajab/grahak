package org.community.grahak.interceptors;


import java.lang.reflect.Method;
import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.community.grahak.channel.Channel;
import org.community.grahak.util.GrahakHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * First interceptor in chain which audits the required information
 * for a service call
 * @author rkarwa
 *
 */
public class AuditOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static Logger log = LoggerFactory.getLogger(AuditOutInterceptor.class);

	public AuditOutInterceptor() {
		super(Phase.SETUP);
		addBefore(ResloveServiceEndpointInterceptor.class.getName());

	}

	@Override
	public void handleMessage(Message message) throws Fault {
        Channel channel = GrahakHelper.getChannel(message);

		if(log.isDebugEnabled())
		{
			log.debug("Auditing information for the service call with channel id " + channel.getId());
			
			Map<?,?> requestContext = GrahakHelper.getRequestContext(message);
			Method method = (Method) requestContext.get(Method.class.getName());
			log.debug("Service Name: " + GrahakHelper.getContextServiceName(message));
			
			if(method != null)
				log.debug("Service Method Name: " + method.getName());
			
			log.debug("Service Type: " + channel.getServiceType());
			log.debug("Out Interceptor chain: " + message.getInterceptorChain());
		}
	}

}
