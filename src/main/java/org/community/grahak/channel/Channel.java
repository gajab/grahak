package org.community.grahak.channel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POJO class to hold channel configuration from channel config file
 * @author rkarwa
 *
 */
public class Channel {
	
	private static Logger log = LoggerFactory.getLogger(Channel.class);
	
	public static enum SERVICE_TYPE {
		
		 XML("XML"), SOAP("SOAP"), REST("REST");

		private String value = "";

		SERVICE_TYPE(String val) {
			value = val;
		}

		public String getValue() {
			return this.value;
		}
		
	};
	
	/**
	 * Constant used in cxf message to hold channel object
	 */
	public static final String CHANNEL = "grahak.channel";
	public static final String SERVICE_NAME = "grahak.serviceName";
	public static final String CONTEXT_SERVICE_NAME = "grahak.contextServiceName";

	private String id;
	private String baseUri;
	private List<Address> addresses = new ArrayList<Address>();
	private ConnectionInfo connection = new ConnectionInfo();
	//private String serviceType;
	private SERVICE_TYPE serviceType = SERVICE_TYPE.SOAP;
	
	
	public SERVICE_TYPE getServiceType() {
		if(serviceType == null)
		{
			this.serviceType = SERVICE_TYPE.SOAP;
		}
		return serviceType;
		
	}
	public void setServiceType(SERVICE_TYPE serviceType) {
		if(serviceType == null)
		{
			this.serviceType = SERVICE_TYPE.SOAP;
		}
		this.serviceType = serviceType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBaseUri() {
		return baseUri;
	}
	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	public ConnectionInfo getConnection() {
		return connection;
	}
	public void setConnection(ConnectionInfo connection) {
		this.connection = connection;
	}
	
	
	/**
	 * get the primary address of the channel
	 * By default it is the first address specified in the channel config 
	 * file. Primary address can be changed during failover strategy so that
	 * subsequent calls uses last working address
	 * 
	 * @return primary address
	 */
	public Address getPrimaryAddress()
	{
		if(addresses == null)
			return null;
		
		for (Address address : addresses) {
			if(address.isPrimary())
				return address;
		}
		
		Address primary = addresses.get(0);
		primary.setPrimary(true);
		
		if(log.isDebugEnabled())
			log.debug("primary address is " + this.getPrimaryEndpointUrl());
		
		return primary;
	}
	
	/**
	 * Returns the primary address in url form i.e. http(s)://host:port/baseuri
	 * for local port it would be local://servicename
	 * @return
	 */
	public String getPrimaryEndpointUrl() {
		
		
		Address address = this.getPrimaryAddress();
		if("jms".equals(address.getProtocol()))
		{
			return "jms://";
		}
		
		return getEndpointUrl(address);
		
	}
	

	/**
	 * Returns the passed address in url form i.e. http(s)://host:port/baseuri
	 * @param address
	 * @return
	 */
	public String getEndpointUrl(Address address) {

		return getHTTPEndpointUrl(address);


	}
	
	
	public String getJMSEndpoint() {
		// TODO Auto-generated method stub
		String jmsEndPoint  = "jms:" + this.connection.getJmsConfig().getDestinationType() 
									+ ":" + this.connection.getJmsConfig().getTargetDestination();
		
		return jmsEndPoint;
	}
	
	public String getHTTPEndpointUrl(Address address) {

		String endpoint = address.getProtocol() + "://" + address.getHost();
		
		String port = address.getPort();
		endpoint = port == null || port.length() ==0 ? endpoint : (endpoint+ ":" + port);
		
		String baseUri = this.getBaseUri();
		endpoint = baseUri == null || baseUri.length() ==0 ? endpoint : (endpoint + baseUri) ;
		
		return endpoint;


	}
	
	
	/**
	 * This is hook that has to done due to the fact that CXF failover strategy can
	 * return alternate address in list of string format only. Here we need to set
	 * the primary address object based on the url string passed.
	 * @param endPoint
	 */
	public void setPrimaryAddress(String endPoint) {
		
		Address newAddress = getAddressFromEndPoint(endPoint);
		String host = newAddress.getHost();
		String port = newAddress.getPort();
		
		//iterate through all the addresses and set primary address
		boolean foundPrimaryAddress = false;
		for (Address address : addresses) {
			if(address.getHost().equals(host) && (address.getPort().equals(port) ||  (address.getPort() == null && (port == "" || port == null)) ) )
			{
				address.setPrimary(true);
				foundPrimaryAddress= true;
				if(log.isDebugEnabled())
					log.debug("setting primary address as " + host+":"+ port);
				
			}
			else
			{
				address.setPrimary(false);
			}
		}
		
		if(foundPrimaryAddress == false)
		{
			log.error("Could not set primary address " +  endPoint);
			log.info("Dumping all the addresses and making the first one is primary address");
			for (int i=0; i<addresses.size(); i++) {
				Address address = addresses.get(0);
				if(i==0)
					address.setPrimary(true);
				else
					address.setPrimary(false);
				
				log.info(getEndpointUrl(address));
			}
			
		}
		
	}
	
	/**
	 * removes the primary address and returns all the alternate addresses
	 * @return
	 */
	public List<Address> getAlernateAddresses() {
		List<Address> altAddresses = new ArrayList<Address>();
		//remove current primary address
		for (Address address : addresses) {
			if(!address.isPrimary())
				altAddresses.add(address);
		}
		return altAddresses;
	}
	
	public boolean isSecure() {
		
		if(this.getPrimaryAddress().getProtocol().equals("https"))
			return true;
		
		return false;
	}
	
	
	public boolean containEndPoint(String endPoint) {
		
		Address addressFromEndPoint = getAddressFromEndPoint(endPoint);
		String host = addressFromEndPoint.getHost();
		String port = addressFromEndPoint.getPort();
		
		
		for (Address address : addresses) {
			
			if(address.getHost().equals(host) && (address.getPort().equals(port)
					||  (address.getPort() == null && (port == "" || port == null)) ) )
					
				return true;
			
		}
		return false;
	}
	
	private Address getAddressFromEndPoint(String endPoint)
	{
		Address address = new Address();
		int hostStartIndex = endPoint.indexOf("://");
		if(hostStartIndex == -1)
		{
			log.error("could not find host in url " + endPoint);
			return address;
		}
		
		hostStartIndex = hostStartIndex + 3;
		
		String protocol = endPoint.substring(0,hostStartIndex - 3);
		
		int hostEndIndex =  endPoint.indexOf(":",hostStartIndex);
		String host = null;
		String port = "";
		if(hostEndIndex == -1)
		{
			host = endPoint.substring(hostStartIndex);
			
		}
		else
		{
			host = endPoint.substring(hostStartIndex ,hostEndIndex);
			int portStartIndex = hostEndIndex + 1;
			int portEndIndex =  endPoint.indexOf("/",portStartIndex);
			port = endPoint.substring(portStartIndex, portEndIndex);
			
		}
		address.setProtocol(protocol);
		address.setHost(host);
		address.setPort(port);
		
		return address;
	}
	
	public void initWithEndpointURL(String endPoint) {
		
		if (endPoint.startsWith("zmq"))
		{
			System.out.println("[Detected zmq endpoint, returning..]");
			return;
		}
		
		Address address = this.getAddressFromEndPoint(endPoint);
		address.setPrimary(true);
		
		this.addresses.add(address);
		//base uri is everything after :<portnumber>
		int index = endPoint.indexOf(":" + this.getPrimaryAddress().getPort());
		this.setBaseUri(endPoint.substring(index+ this.getPrimaryAddress().getPort().length() + 1));
		System.out.println("@@@@ base uri " + this.getBaseUri());
	}
	public String getJMSBrokerUrl() {
		// tcp://localhost:61616
		Address address = this.getPrimaryAddress();
		return "tcp://" + address.getHost() + ":" + address.getPort();
	}

    @Override
    public String toString()
    {
    	String str = id + ":";
    	for (Address address : this.addresses) {
			str = str + address.getProtocol() + ":" + address.getHost() + ":" + address.getPort() + ":" + address.isPrimary();
		}
    	str = str + ":";
    	str = str + baseUri + ":";
    	str = str + connection.getConnectionTimeout() + ":";
    	str = str + connection.getMaxThreads();
    	return str;
    	
    }
    

}
