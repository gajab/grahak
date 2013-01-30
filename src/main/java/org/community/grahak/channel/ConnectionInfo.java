package org.community.grahak.channel;

/**
* 	POJO object to hold connection info (client policy)
* 	<connectionTimeout>10</connectionTimeout> milliseconds
*	<receiveTimeout>15</receiveTimeout> milliseconds
*	<idleTimeout></idleTimeout> 
*	<keepAlive>true</keepAlive> 
*	<maxThreads>10</maxThreads>
*	<allowChunking>true</allowChunking>
 * @author rkarwa
 *
 */
public class ConnectionInfo {

    //connectionInfo constants
    public static final String KEEP_ALIVE ="keepAlive"; 
    public static final String ALLOW_CHUNKING ="allowChunking"; 
    public static final String RECEIVE_TIMEOUT ="receiveTimeout"; 
    public static final String CONNECTION_TIMEOUT ="connectionTimeout"; 
    public static final String MAX_THREADS ="maxThreads"; 
	
	// default connection timeout in milliseconds
	public static final int DFLT_CONNECTION_TIMEOUT=30000;
	public static final int DFLT_RECEIVE_TIMEOUT=60000;
	public static final int DFLT_IDLE_TIMEOUT=1800000;
	public static final int DFLT_MAX_THREAD=50;
	public static final boolean DFLT_KEEP_ALIVE=true;
	public static final boolean DFLT_ALLOW_CHUNKING=false;

	
	private int connectionTimeout = DFLT_CONNECTION_TIMEOUT;
	private int maxThreads = DFLT_MAX_THREAD;
	private boolean keepAlive = DFLT_KEEP_ALIVE;
	private int receiveTimeout=DFLT_RECEIVE_TIMEOUT;
	private int idleTimeout = DFLT_IDLE_TIMEOUT;
	private boolean allowChunking = DFLT_ALLOW_CHUNKING;
	
	private int maxNumberOfRetries;
    
	
	private JMSConfig jmsConfig;
	
	public JMSConfig getJmsConfig() {
		return jmsConfig;
	}
	public void setJmsConfig(JMSConfig jmsConfig) {
		this.jmsConfig = jmsConfig;
	}
	
    public int getMaxNumberOfRetries() {
		// TODO Auto-generated method stub
		return maxNumberOfRetries;
	}

	public void setMaxNumberOfRetries(int n) {
		// TODO Auto-generated method stub
		maxNumberOfRetries = n;
	}
	public boolean isAllowChunking() {
		return allowChunking;
	}
	public void setAllowChunking(boolean allowChunking) {
		this.allowChunking = allowChunking;
	}
	
	public boolean isKeepAlive() {
		return keepAlive;
	}
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
	public int getReceiveTimeout() {
		return receiveTimeout;
	}
	public void setReceiveTimeout(int receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}
	public int getIdleTimeout() {
		return idleTimeout;
	}
	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getMaxThreads() {
		return maxThreads;
	}
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}
}


