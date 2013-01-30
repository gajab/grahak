package org.community.grahak.util;

import java.util.Map;

import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.message.Message;
import org.community.grahak.channel.Channel;


/**
 * Helper class with various static helper methods
 * @author rkarwa
 *
 */
@SuppressWarnings( "rawtypes")
public class GrahakHelper {
	
	
	public static Map getRequestContext(Message message)
	{
		Map context =  (Map) message.get(Message.INVOCATION_CONTEXT);
		return (Map)context.get(ClientImpl.REQUEST_CONTEXT);
		
	}
	public static Channel getChannel(Message message)
	{
		Map requestContext = getRequestContext(message);
		return (Channel) requestContext.get(Channel.CHANNEL);
		
	}

	@SuppressWarnings("unchecked")
	public static void updateChannel(Message message, Channel channel) {
		
		Map requestContext = getRequestContext(message);
		requestContext.put(Channel.CHANNEL, channel);
	}
	
	public static String getServiceName(Message message)
	{
		return (String)getRequestContext(message).get(Channel.SERVICE_NAME);
	}
	
	public static String getContextServiceName(Message message)
	{
		return (String)getRequestContext(message).get(Channel.CONTEXT_SERVICE_NAME);
	}

}
