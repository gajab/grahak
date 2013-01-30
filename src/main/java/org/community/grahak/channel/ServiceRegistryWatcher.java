package org.community.grahak.channel;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ServiceRegistryWatcher implements Watcher {

	@Override
	public void process(WatchedEvent event) {
		System.out.println(event.getPath() + " " + event.getType() );
		try {
			if(event.getPath() != null)
				ZookeeperReader.getZookeeperReaderInstance().readOnChange(event.getPath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
