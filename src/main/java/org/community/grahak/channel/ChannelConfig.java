package org.community.grahak.channel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Channel Configuration object created by Xstream through
 * channel config file. Also has a utility function to get
 * the the channel object based on channel name.
 * @author rkarwa
 *
 */
@Component
public class ChannelConfig {

	private ArrayList<Channel> channels = new ArrayList<Channel>();

	public void setChannels(ArrayList<Channel> channels) {
		this.channels = channels;
	}

	public ArrayList<Channel> getChannels() {
		return channels;
	}

	public Channel getChannel(int i) {
		return this.channels.get(i);
	}

	public void addChannel(Channel channel) {

		this.channels.add(channel);
	}
	
	public void addChannels(ArrayList<Channel> channels) {

		this.channels.addAll(channels);
	}

	public Channel getChannel(String channelId) {

		Channel  retChannel = null;
		
		for (Channel channel : this.channels) {

			if (channelId.equals(channel.getId())) {
				
				return channel;

			}
		}
		
		return retChannel;

	}
	
	public Set<String> getAllUniqueChannelIds()
	{
		Set<String> channelIds = new HashSet<String>();
		for (Channel channel : this.channels) {
			String channelId = channel.getId();
			if (channelIds.add(channelId) == false)
				throw new RuntimeException("Duplicate channel id " + channelId);
		}
		
		return channelIds;
	}

}
