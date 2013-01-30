package org.community.configloader.event;

import java.util.List;

public class ChannelConfigChangeEventImpl implements ChannelConfigChangeEvent {

	private List<String> changedConfigs;

	public List<String> getChangedConfigs() {
		return changedConfigs;
	}

	@Override
	public void setChangedConfigs(List<String> modifiedFiles) {
		this.changedConfigs = modifiedFiles;

	}

}
