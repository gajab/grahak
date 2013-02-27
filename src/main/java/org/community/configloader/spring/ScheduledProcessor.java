package org.community.configloader.spring;


import org.springframework.stereotype.Service;


@Service
public class ScheduledProcessor {

	private Worker configUpdateWatcher;
	
	public void setConfigUpdateWatcher(Worker configUpdateWatcher) {
		this.configUpdateWatcher = configUpdateWatcher;
	}

	//this job runs after every 5 minutes
	//@Scheduled(fixedDelay = 5*60*1*1000)
	public void configUpdateWatcher() {
		System.out.println("[checking for file updates]");
		configUpdateWatcher.work();
	}
}
