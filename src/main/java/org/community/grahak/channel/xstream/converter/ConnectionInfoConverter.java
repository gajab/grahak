package org.community.grahak.channel.xstream.converter;

import org.community.grahak.channel.ConnectionInfo;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * 
 * XStream Converter for connection info object. This mainly required
 * for setting up default values of connection info object.
 * NOTE* This logic will only be called if there is Connection node
 * under channel.
 * @author rkarwa
 *
 */
public class ConnectionInfoConverter implements Converter {

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1,
			MarshallingContext arg2) {
		//do nothing
    }

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext arg1) {
		
		ConnectionInfo connection = new ConnectionInfo();
		while (reader.hasMoreChildren()) {

			reader.moveDown();

			String value = reader.getValue();

			//if ("keepAlive".equals(reader.getNodeName())) {
			if (ConnectionInfo.KEEP_ALIVE.equals(reader.getNodeName())) {

				boolean bValue = ConnectionInfo.DFLT_KEEP_ALIVE;
				if (value != null && value.trim().length() != 0)
					bValue = Boolean.valueOf(value);

				connection.setKeepAlive(bValue);

			//} else if("allowChunking".equals(reader.getNodeName()))
			} else if(ConnectionInfo.ALLOW_CHUNKING.equals(reader.getNodeName()))
			{
				
				boolean bValue = ConnectionInfo.DFLT_ALLOW_CHUNKING;
				if (value != null && value.trim().length() != 0)
					bValue = Boolean.valueOf(value);

				connection.setAllowChunking(bValue);
				
			}
			else {

				int nValue = 0;

				if (value != null && value.trim().length() != 0)
					nValue = Integer.valueOf(reader.getValue());

				//if ("receiveTimeout".equals(reader.getNodeName())) {
                // backward compatible code - also common mistake
				if (ConnectionInfo.RECEIVE_TIMEOUT.equals(reader.getNodeName()) || "recieveTimeout".equals(reader.getNodeName())) {
					connection.setReceiveTimeout(nValue > 0 ? nValue
							: ConnectionInfo.DFLT_RECEIVE_TIMEOUT);
				}

				//if ("connectionTimeout".equals(reader.getNodeName())) {
				if (ConnectionInfo.CONNECTION_TIMEOUT.equals(reader.getNodeName())) {
					connection.setConnectionTimeout(nValue > 0 ? nValue
							: ConnectionInfo.DFLT_CONNECTION_TIMEOUT);
				}

				//if ("maxThreads".equals(reader.getNodeName())) {
				if (ConnectionInfo.MAX_THREADS.equals(reader.getNodeName())) {
					connection.setMaxThreads(nValue > 0 ? nValue
							: ConnectionInfo.DFLT_MAX_THREAD);
				}
			}

			reader.moveUp();

		}

		return connection;
	}

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) {

		return arg0.equals(ConnectionInfo.class);

	}

}
