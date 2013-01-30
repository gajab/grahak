package org.community.configloader.spring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.community.configloader.event.ChannelConfigChangeEvent;
import org.community.configloader.event.ChannelConfigChangeEventImpl;
import org.community.configloader.event.Observable;
import org.community.configloader.event.Observer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;



public class ConfigUpdateWatcher implements Worker, Observable<ChannelConfigChangeEvent> {

	private static final String DEFAULT_CONFIG_FILE = "/config/grahak/channel/channelConfig.xml";
	//TODO: Rahul list of files to watch is populated by user
	private List<String> configFilesToWatch = new ArrayList<String>();
	//has to be singleton as the map storing last modified time stamp

	private Map<String, Long> configFileToModifiedTimeStamp = new ConcurrentHashMap<String, Long>();
	private List<Observer<ChannelConfigChangeEvent>> observers = new ArrayList<Observer<ChannelConfigChangeEvent>>();
	
	
	public List<String> getConfigFilesToWatch() {
		return configFilesToWatch;
	}
	
	/**
	 * overrides the default
	 * @param configFilesToWatch
	 */
	public void setConfigFilesToWatch(List<String> configFilesToWatch) {
		this.configFilesToWatch.addAll(configFilesToWatch);
	}

	public ConfigUpdateWatcher()
	{
		configFilesToWatch.add(DEFAULT_CONFIG_FILE);
	}
	
	@Override
	public void work() {
		
		List<String> modifiedFiles = new ArrayList<String>();
		for (String configFilePath : configFilesToWatch) {
			Resource configResource = new ClassPathResource(configFilePath);
			try {
				File file = configResource.getFile();
				long lastTimeStamp = file.lastModified();
				if(configFileToModifiedTimeStamp.containsKey(file.getPath()))
				{
					long lastRecordedTimeStamp = configFileToModifiedTimeStamp.get(file.getPath());
					if(lastRecordedTimeStamp != lastTimeStamp)
					{
						modifiedFiles.add(file.getPath());
						configFileToModifiedTimeStamp.put(file.getPath(), lastRecordedTimeStamp);
					}
				}
				else
				{
					//detected first time
					modifiedFiles.add(file.getPath());

					configFileToModifiedTimeStamp.put(file.getPath(), lastTimeStamp);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(modifiedFiles.size() > 0)
		{
			for (Observer<ChannelConfigChangeEvent> observer : observers) {
				
				ChannelConfigChangeEvent event = new ChannelConfigChangeEventImpl();
				event.setChangedConfigs(modifiedFiles);
				observer.onChange(event);
			}
		}
		
		
	}

	@Override
	public void registerObserver(Observer<ChannelConfigChangeEvent> observer) {
		observers.add(observer);
		
	}



}
