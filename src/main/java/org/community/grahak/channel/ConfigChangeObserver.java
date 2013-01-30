package org.community.grahak.channel;

import org.community.configloader.event.ChannelConfigChangeEvent;
import org.community.configloader.event.Observer;
import org.community.grahak.util.ServiceCallHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigChangeObserver implements Observer<ChannelConfigChangeEvent> {
	private static Logger log = LoggerFactory
			.getLogger(ConfigChangeObserver.class);

	private ChannelConfigReader channelConfigReader;

	private ServiceCallHandler serviceCallHandler;

	public void setChannelConfigReader(ChannelConfigReader channelConfigReader) {
		this.channelConfigReader = channelConfigReader;
	}

	public void setServiceCallHandler(ServiceCallHandler serviceCallHandler) {
		this.serviceCallHandler = serviceCallHandler;
	}

//	@Override
//	public void onChange(ConfigChangeNotifier notifier) {
//		if (log.isDebugEnabled()) {
//			log.debug("channel config file is updated, reading it again ..");
//		}
//
//		channelConfigReader.read();
//		// initialize the ServiceCallHandler for maxThreads
//		serviceCallHandler.update();
//	}

	@Override
	public void onChange(ChannelConfigChangeEvent event) {
		
		if (log.isDebugEnabled()) {
			log.debug("channel config file is updated, reading it again ..");
		}

		channelConfigReader.read(event.getChangedConfigs());
		// initialize the ServiceCallHandler for maxThreads
		//serviceCallHandler.update();

	}
}
