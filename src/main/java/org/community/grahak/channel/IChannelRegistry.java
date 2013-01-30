package org.community.grahak.channel;

import java.util.List;

import org.community.configloader.event.Observer;

public interface IChannelRegistry {

	public Channel getChannel(String registryId);

	void registerChannel(String id, Channel newChannel);

	public List<Channel> getChannels();

	public void registerObserver(Observer<ChannelRegisterEvent> observer);

}
