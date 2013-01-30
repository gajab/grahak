package org.community.grahak.channel;

/**
 * POJO class for address node in channel config
 * 
 * @author rkarwa
 *
 */
public class Address {
	
	private String host;
	private String port;
	private String protocol;
	private boolean primary;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public boolean isPrimary() {
		return primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

}
