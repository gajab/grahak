package org.community.configloader.event;

import java.util.List;

public interface ChannelConfigChangeEvent {

	void setChangedConfigs(List<String> modifiedFiles);
	List<String> getChangedConfigs();

}
