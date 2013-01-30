package org.community.configloader.spring;

import java.io.IOException;
import java.util.Map;

import org.community.configloader.event.Observer;


public interface ConfigLoader {

	void addListener(Observer<?> listener);

	Map<String, String> load(String channelConfigLoadPattern) throws IOException;

}
