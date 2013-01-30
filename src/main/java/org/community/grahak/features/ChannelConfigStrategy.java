package org.community.grahak.features;

import java.util.ArrayList;
import java.util.List;


import org.apache.cxf.clustering.SequentialStrategy;
import org.apache.cxf.message.Exchange;
import org.community.grahak.channel.Address;
import org.community.grahak.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Sequential failover strategy which allows alternate address
 * to be read from channel config files
 * @author rkarwa
 *
 */
public class ChannelConfigStrategy extends SequentialStrategy {
	
	private static final Logger log = LoggerFactory.getLogger(ChannelConfigStrategy.class);

	//this method is only called once for per service call execution.
	//CXF holds the list return by this method and calls service
	// on alternate address one by one
	@Override
	public List<String> getAlternateAddresses(final Exchange exchange)
	{
		if(log.isDebugEnabled())
		{
			log.debug("getting alternate addresses for failover");
		}
		
		final Channel channel = (Channel) exchange.get(Channel.CHANNEL);
		
		final List<String> alt = new ArrayList<String>();
		//failover holds all the alternate address which are not getting reset
		//in ChannelConfigListener. so during current service call the failover will
		//return all old channel addresses but for the subsequesnt service calls
		// it will return new address from the updated channel object
		
		final List<Address> addresses = channel.getAlernateAddresses();
		
		if(log.isDebugEnabled())
		{
			log.debug("alternate addresses are:");
		}
		
		for (Address address : addresses) {
			
			if(log.isDebugEnabled())
			{
				log.debug(address.getHost());
			}
			
			alt.add(channel.getEndpointUrl(address));
		}
		
		return alt;
		
	}
	
	
}
