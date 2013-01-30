package org.community.grahak.interceptors;


import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.community.grahak.channel.Channel;
import org.community.grahak.util.GrahakHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This interceptor resloves the service endpoint (host:address) from
 * channel configuration. During failover it reset the primary endpoint address
 * so that subsequent service call on same channel uses last working endpoint
 * @author rkarwa
 *
 */
public class ResloveServiceEndpointInterceptor  extends AbstractPhaseInterceptor<Message> {

	private static Logger log = LoggerFactory.getLogger(ResloveServiceEndpointInterceptor.class);

	public ResloveServiceEndpointInterceptor() {
        super(Phase.SETUP);
    }
	
	@Override
	public void handleMessage(Message message) throws Fault {
		
		Channel channel = GrahakHelper.getChannel(message);
		
		String endPoint = (String)message.get(Message.ENDPOINT_ADDRESS);
		
		if(null == endPoint || endPoint.trim().isEmpty())
		{	
			endPoint  = channel.getPrimaryEndpointUrl();
		}
		else
		{
		
			//above endPoint is either coming from during startup or during failover
			//the channel configuration might have changed and address in the message
			// might be old one.
			//Also failover holds all the alternate address which are not getting reset
			//in ChannelConfigListener. so during current service call the failover will
			//return all old channel addresses. So during the current service call it may
			//happen that this interceptor resolves primary address in the updated channel
			//object and ignores what is coming from failover strategy as failover strategy
			//contains old addresses. In the worst case scenario there is possibility of
			//request going to primary address as many times as the number of alternate
			//address (if the primary address is down). But for the subsequesnt service calls
			// failover strategy will return new address from the updated channel object
			//To fix this, we need to update the alternate address hold by the Failover
			//strategy. But if the ChannelConfigStrategy#getAlternateAddresses() has already
			//been called then only way to update this address is to override getNextAddress()
			// this is kind of workaround but CXF provides no other way. And if the 
			// ChannelConfigStrategy#getAlternateAddresses() is not yet called for this service
			// call execution then anyway alternate address would be updated one so no issue.
			
			
			//we need to check that this endPoint is valid address in the
			//channel object (which may has already been updated in ChannelConfigListener)
			
			//if it is valid then make sure the channel has this as primary address
			if(channel.containEndPoint(endPoint) == true)
			{
				//endPoint coming from failover strategy and its valid address
				channel.setPrimaryAddress(endPoint);
			}
			else
			{
				//endPoint coming from failover strategy and its NOT valid address
				//if it is not valid then get the primary address and set back in message
				log.warn("end point " + endPoint + " is not a valid address in the channel " + channel.getId());
				log.warn("setting endpoint to primary endpoint from channel object " + channel.getPrimaryEndpointUrl());
				endPoint = channel.getPrimaryEndpointUrl(); 
			}
		}
		
		String serviceName = GrahakHelper.getServiceName(message);
		String endPointURL="";
		
		if(serviceName != null && serviceName.trim().length() != 0)
			endPointURL = endPoint + "/" + serviceName;
		else
			endPointURL = endPoint;
		
		if(log.isDebugEnabled())
		{
			log.debug("setting endpoint = "+ endPointURL + " as primary end point");
		}
		
		message.put(Message.ENDPOINT_ADDRESS, endPointURL);
		message.put("javax.xml.ws.service.endpoint.address", endPointURL);
	
	}

}
