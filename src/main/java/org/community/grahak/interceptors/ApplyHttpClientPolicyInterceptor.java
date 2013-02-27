package org.community.grahak.interceptors;

import javax.net.ssl.TrustManager;

import org.apache.cxf.clustering.RetryStrategy;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.community.grahak.channel.Channel;
import org.community.grahak.channel.ConnectionInfo;
import org.community.grahak.util.RelaxedX509TrustManager;
import org.community.grahak.util.GrahakHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 
 * This interceptor is invoked as a part of SETUP phase to apply http client policy
 * from the channel configuration.
 * @author rkarwa
 *
 */
public class ApplyHttpClientPolicyInterceptor extends AbstractPhaseInterceptor<Message> {

	private static Logger log = LoggerFactory.getLogger(ApplyHttpClientPolicyInterceptor.class);
	
	private RetryStrategy retryStrategyFeature;
	


	public ApplyHttpClientPolicyInterceptor() {
        super(Phase.SETUP);
    }

	@Override
	public void handleMessage(Message message) throws Fault {
		
		//get channel object for this invocation
		Channel channel = GrahakHelper.getChannel(message);
		
		/*if("local".equals(channel.getPrimaryAddress().getProtocol().toLowerCase()))
		{
			log.info("protocol is local hence skip applying HTTP client policy");
			return;
		}*/
		
		if(log.isDebugEnabled())
			log.debug("applying http client policies");
		
		
		if(channel == null)
		{
			log.error("channel object is null, not applying http client policies");
			return;
		}
		
        if (retryStrategyFeature != null)
        {
		    retryStrategyFeature.setMaxNumberOfRetries(channel.getConnection().getMaxNumberOfRetries());
        }
        else
        {
            log.debug("{}","[WARNING: not setting retryStrategyFeature]");
        }
		//get selected conduit
		Exchange exchange = message.getExchange();
		HTTPConduit conduit = (HTTPConduit) exchange.getConduit(message);
		
		//create http client policy object
		HTTPClientPolicy clientPolicy = new HTTPClientPolicy();
		//populate the http client policy object from the channel configuration
		setClientPolicy(clientPolicy, channel);
		//set the created/populated client policy object to conduit so the HttpConduit uses
		//this client policy object
		conduit.setClient(clientPolicy);
		
		//TODO: this can go to a separate interceptor which will be only configured for development env
		//setting ssl parameters to connect https
		if(channel.isSecure())
		{
			TLSClientParameters params= conduit.getTlsClientParameters();
			if(params ==null)
			{
				 params = new TLSClientParameters();
				 conduit.setTlsClientParameters(params);
			}
			
			log.warn("setting relaxed trust manager. This may need to change in production environment");
			params.setTrustManagers(new TrustManager[] {new RelaxedX509TrustManager()});
		}
		
		
		
	}
	
	/**
	 * copies client configuration from channel to  clientPolicy
	 * @param clientPolicy
	 * @param channel
	 */
	private void setClientPolicy(HTTPClientPolicy clientPolicy, Channel channel)
	{
		ConnectionInfo connectionInfo = channel.getConnection();
		clientPolicy.setReceiveTimeout(connectionInfo.getReceiveTimeout());
		clientPolicy.setConnectionTimeout(connectionInfo.getConnectionTimeout());
		if(connectionInfo.isKeepAlive())
			clientPolicy.setConnection(ConnectionType.KEEP_ALIVE);
		clientPolicy.setAllowChunking(channel.getConnection().isAllowChunking());
	}
	
	
	public RetryStrategy getRetryStrategyFeature() {
		return retryStrategyFeature;
	}

	public void setRetryStrategyFeature(RetryStrategy retryStrategyFeature) {
		this.retryStrategyFeature = retryStrategyFeature;
	}
}
