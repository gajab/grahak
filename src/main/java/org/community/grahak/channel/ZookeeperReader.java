package org.community.grahak.channel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;

public class ZookeeperReader {
	
	public static final String SERVICE_REGISTRY_ROOT_NODE = "/services";
	
	private static ZooKeeper zk;
	
	private static ZookeeperReader zr;
	
	@Autowired
	private IChannelRegistry channelRegistry;
	
	public void readOnChange(String path) throws Exception
	{
		int si = SERVICE_REGISTRY_ROOT_NODE.length()+1;
		int ei = path.indexOf("/", SERVICE_REGISTRY_ROOT_NODE.length()+1);
		if (ei < 0)
			ei = path.length();
		
		System.out.println(si + " :"+ ei);
		
		String serviceName = path.substring(si,ei);
		System.out.println(serviceName);
		Channel channel =  channelRegistry.getChannel(serviceName);

		System.out.println("found chanel " + channel);
		readServiceConfig(path, serviceName, channel);
		
		channelRegistry.registerChannel(serviceName, channel);


	}
	
	public  void read(String path, Channel channel) throws Exception
	{
		
//		System.out.println(new String(getZookeeperInstance().getData(path, new ServiceRegistryWatcher(), null)));
		List<String> serviceNames =  getZookeeperInstance().getChildren(path, true);
		for (String serviceName : serviceNames) {
			System.out.println(serviceName);

			channel.setId(serviceName); //TODO : for zookeeper servicename is channel id
			readServiceConfig(path, serviceName, channel);
			
		}
		
		System.out.println(channel);
		
		
	}
	
	private void readServiceConfig(String path, String serviceName, Channel channel) throws Exception
	{
		List<String> servers = new ArrayList<String>();

		ConnectionInfo connectionInfo = channel.getConnection();

		List<String> configs =  getZookeeperInstance().getChildren(path+"/"+serviceName, true);

		for (String config : configs) {
			if(config.startsWith("server"))
			{
				servers.add(config);
			}
			if(config.startsWith("baseUri"))
			{
				channel.setBaseUri(new String (getZookeeperInstance().getData(path + "/" + serviceName + "/" + "baseUri", true, null)));
			}
			if(config.startsWith("maxConcurrent"))
			{
				//String mc = new String (getZookeeperInstance().getData(path + "/" + serviceName + "/" + "maxConcurrent", true, null));
				connectionInfo.setMaxThreads(Integer.parseInt(readOptionalConfig(path + "/" + serviceName + "/" + "maxConcurrent", channel)));
			}
			if(config.startsWith(ConnectionInfo.CONNECTION_TIMEOUT))
			{
				String mc = new String (readOptionalConfig(path + "/" + serviceName + "/" + ConnectionInfo.CONNECTION_TIMEOUT, channel));
				connectionInfo.setConnectionTimeout(Integer.parseInt(mc));
			}
			
		}
		
		populateAddresses(path+"/"+serviceName ,servers, channel);
		//read(path+"/" + serviceName);

	}
	
	private String readOptionalConfig(String path, Channel channel) throws KeeperException, InterruptedException, Exception
	{
		String mc = new String (getZookeeperInstance().getData(path, true, null));
		
		return mc;
	}
	
	private void populateAddresses(String path, List<String> servers, Channel channel) throws KeeperException, InterruptedException, Exception {
		System.out.println("before sort ");
		for (String string : servers) {
			System.out.println(string);
		}
		Collections.sort(servers);
		
		System.out.println("after sort ");
		for (String string : servers) {
			System.out.println(string);
		} 
		boolean isPrimarySet = false;
		List<Address> addresses = new ArrayList<Address>();
		for (String server : servers) {
			Address address = new Address();
			String psp = new String(getZookeeperInstance().getData(path + "/" + server, true, null));
			address.setProtocol(psp.substring(0, psp.indexOf(":")));
			address.setHost(psp.substring(psp.indexOf(":") + 1, psp.lastIndexOf(":")));
			address.setPort(psp.substring(psp.lastIndexOf(":") + 1));
			if(!isPrimarySet)
			{
				address.setPrimary(true);
				isPrimarySet = true;
			}
			addresses.add(address);
			
		}
		channel.setAddresses(addresses);
		
	}

	private static ZooKeeper getZookeeperInstance() throws Exception
	{
		if(zk==null)
		{
			zk = new ZooKeeper("LRKARWA:2181", 1000, null);
			zk.register(new ServiceRegistryWatcher());
		}
		
		return zk;
	}
	
	public static ZookeeperReader getZookeeperReaderInstance()
	{
		if(zr == null)
			zr= new ZookeeperReader();
		
		return zr;
	}
	
	public static void main(String [] args) throws Exception
	{
		Channel channel = new Channel();
		getZookeeperReaderInstance().read(SERVICE_REGISTRY_ROOT_NODE, channel);
		Thread.sleep(30000);
	}

}
