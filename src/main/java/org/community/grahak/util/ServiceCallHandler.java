package org.community.grahak.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.community.configloader.event.ChannelConfigChangeEvent;
import org.community.configloader.event.Observer;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.ChannelRegisterEvent;
import org.community.grahak.channel.IChannelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wraps a HashMap to keep track of number of service call in progress per channel
 * @author rkarwa
 *
 */
public class ServiceCallHandler implements Observer<ChannelRegisterEvent> {

	private static Logger log = LoggerFactory.getLogger(ServiceCallHandler.class);

	private Map<Channel, Counter> channelCallCounter = new HashMap<Channel, Counter>();

	private IChannelRegistry channelRegistry;
	
	public void setChannelRegistry(IChannelRegistry channelRegistry) {
		this.channelRegistry = channelRegistry;
	}

	/**
	 * init method to be called after first time channel config read
	 * @param channels
	 */
	public void init() {
		List<Channel> channels = channelRegistry.getChannels();
		for (Channel channel : channels) {
			Counter counter = channelCallCounter.get(channel);
			int max = channel.getConnection().getMaxThreads();
			counter = new Counter(0, max);
			channelCallCounter.put(channel, counter);
		}
	}

	/**
	 * update method which updates the number of the concurrent service call allowed per channel
	 * it should be called after updating channel configuration
	 */
//	public void update() {
//		
//		if(log.isDebugEnabled())
//			log.debug("updating the max allowed concurrent calls for each channel");
//		
//		Set<Channel>  channels = channelCallCounter.keySet();
//		
//		for (Channel channel : channels) {
//			int max = channel.getConnection().getMaxThreads();
//			Counter counter = channelCallCounter.get(channel);
//
//			synchronized (channel) {
//				
//				counter.setMax(max);
//			}
//		}
//	}

	/**
	 * @param channel
	 * @return true if # of running service call less than max allowed else false
	 */
	public boolean isCallAllowed(Channel channel) {

		Counter counter = channelCallCounter.get(channel);
		
		if (counter == null) {
			// initialization failure for channel service call counter
			String msg = "initialization failure for channel service call counter";
			log.error(msg);
			return false;
		}

		if (!counter.tryIncrement()) {
			log.error("Service call not allowed for channel" + channel.getId() + " as # of call in progress is " 
					+ counter.count + " and max allowed is " + counter.max);
			return false;
		}

		// should be better than synchronizing the entire method as here
		// only thread specific to the channel is locked
		synchronized (channel) {
			//if (!counter.tryIncrement()) {
			if(counter.count < counter.max)
			{
				counter.count++;
				return true;
			}
			
		}

		log.error("Service call not allowed for channel " + channel.getId() + " as # of call in progress is " 
					+ counter.count + " and max allowed is " + counter.max);
		
		return false;
	}

	/**
	 * release/decrease the service counter by one for the passed channel
	 * @param channel
	 */
	public void releaseHandle(Channel channel) {

		Counter counter = channelCallCounter.get(channel);

		if (counter == null) {
			// initialization failure for channel service call counter
			String msg = "initialization failure for channel service call counter";
			log.error(msg);
			return;
		}
		if (!counter.tryDecrement())
			return;

		synchronized (channel) {
			if (!counter.tryDecrement())
				return;
			counter.count--;
		}

	}
	
	/**
	 * inner counter object which keeps count and max allowed count
	 * @author rkarwa
	 *
	 */
	private static class Counter {
		public volatile int count;
		public int max;

		public Counter(int c, int m) {
			this.count = c;
			this.max = m;
		}

		public void setMax(int m) {
			this.max = m;
		}

		public boolean tryIncrement() {
			if (count <= max)
				return true;
			return false;
		}

		public boolean tryDecrement() {
			if (max >= count)
				return true;
			return false;
		}
	}

	@Override
	public void onChange(ChannelRegisterEvent event) {
		
		Channel newChannel = event.getNewRegisteredChannel();
		if(log.isDebugEnabled())
			log.debug("updating the max allowed concurrent calls for channel" + newChannel.getId());
		
		Counter counter = channelCallCounter.get(newChannel);
		int max = newChannel.getConnection().getMaxThreads();
	
			
		synchronized (newChannel) {
			
			if(counter == null )
			{	
				counter = new Counter(0, max);
				channelCallCounter.put(newChannel, counter);
			}
			else
			{
				counter.setMax(max);
			}
		}
		
		
	}

}
