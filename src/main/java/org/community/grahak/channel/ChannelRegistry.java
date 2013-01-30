package org.community.grahak.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.community.configloader.event.Observable;
import org.community.configloader.event.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChannelRegistry implements IChannelRegistry, Observable<ChannelRegisterEvent>{
	
	private static Logger log = LoggerFactory.getLogger(ChannelRegistry.class);

	private Map<String, Channel> channelRegistry = new HashMap<String, Channel>();
	private List<Observer<ChannelRegisterEvent>> channelRegisterObservers 
							= new ArrayList<Observer<ChannelRegisterEvent>>();

	
	public Channel getChannel(String registryId)
	{
		return channelRegistry.get(registryId);
	}

	@Override
	public void registerChannel(String id, Channel newChannel) {
		
		Channel channel = channelRegistry.get(id);
		
		if(channel == null)
		{
			channelRegistry.put(id, newChannel);
			
		}
		else
		{
			// need to do deepcopy as all the proxy object will be already
			// holding a channel object
			// here we need to replace the properties of the same channel
			// object instead of
			// creating new channel object.
			// if we create new channel objects then we have to update the
			// channel object reference
			// hold by all the proxy objects

			// assuming we are going to use dozer for object mapping tools
			// Mapper mapper = new DozerBeanMapper();
			// source, destination
			// mapper.map(newChannel, instanceChannel);
			copyChannel(newChannel, channel);

		}
		
		//fire channel register event
		for (Observer<ChannelRegisterEvent> channelRegisterObserver : channelRegisterObservers) {
			
			ChannelRegisterEvent event = new ChannelRegisterEvent();
			event.setNewRegisteredChannel(getChannel(id));
			channelRegisterObserver.onChange(event);
			
		}
	}
	
	private void copyChannel(Channel source, Channel dest) {
		if (!source.getId().equals(dest.getId())) {
			// this is not allowed, Ids should be same
			String msg = "Source and destination channel id should be same."
					+ " Source:" + source.getId() + " Dest:" + dest.getId();
			log.error(msg);
			throw new RuntimeException(msg);
		}
		dest.setServiceType(source.getServiceType());
		dest.setAddresses(source.getAddresses());
		dest.setBaseUri(source.getBaseUri());
		dest.setConnection(source.getConnection());
		dest.setId(source.getId());

	}

	@Override
	public List<Channel> getChannels() {
		// TODO Auto-generated method stub
		
		return new ArrayList<Channel>(channelRegistry.values());
	}

	@Override
	public void registerObserver(Observer<ChannelRegisterEvent> observer) {
		channelRegisterObservers.add(observer);
		
	}


}
