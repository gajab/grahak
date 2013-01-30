package org.community.grahak.channel;

import java.io.FileInputStream;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.community.configloader.event.ChannelConfigChangeEvent;
import org.community.configloader.event.Observer;
import org.community.configloader.spring.ConfigLoader;
import org.community.grahak.channel.Channel.SERVICE_TYPE;
import org.community.grahak.channel.xstream.converter.ConnectionInfoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class reads channel configuration provided by applications
 * 
 * @author rkarwa
 * 
 */
public class ChannelConfigReader implements IChannelConfigReader {
	private static Logger log = LoggerFactory
			.getLogger(ChannelConfigReader.class);

	/** pattern matching URL **/

	private static final String CHANNEL_CONFIG_SCHEMA_FILE = "/channelConfig.xsd";
//	private ChannelConfig channelConfig;

	private IChannelRegistry channelRegistry;
	//private boolean isRead = false;

	public void setChannelRegistry(IChannelRegistry channelRegistry) {
		this.channelRegistry = channelRegistry;
	}

	private static XStream xstream = new XStream(new DomDriver());

	// key filename value set of channelIds
	Map<String, Set<String>> fileNameChannelIds = new HashMap<String, Set<String>>();

	private Map<String, String> channelFileMap;

	private List<String> channelFiles;

	//private Observer<ChannelConfigChangeEvent> listener;
	
	//private ConfigLoader configLoader;

/*	public void setConfigLoader(ConfigLoader configLoader) {
		this.configLoader = configLoader;
	}*/

	static {
		// converter for connectioninfo object, the converter sets the default
		// value in case developer does not provide any connection parameter
		xstream.registerConverter(new ConnectionInfoConverter());
		xstream.alias("ChannelConfig", ChannelConfig.class);
		xstream.alias("channel", Channel.class);
		xstream.alias("address", Address.class);
		xstream.alias("connection", ConnectionInfo.class);
		xstream.alias("serviceType", SERVICE_TYPE.class);

		xstream.useAttributeFor(Channel.class, "id");
	}
	


/*	public void setChannelConfig(ChannelConfig channelConfig) {
		this.channelConfig = channelConfig;
	}

	public ChannelConfig getChannelConfig() {
		return channelConfig;
	}*/

//	public void setListener(Observer<ChannelConfigChangeEvent> listener) {
//		this.listener = listener;
//	}

	public void read(List<String> files) {

		ChannelConfig localChannelConfig = null;
		for (String fileName : files) {
			try {
				
//				System.out.println("#########################33" + fileName);
				if (log.isDebugEnabled())
					log.debug("reading channel config file " + fileName);

				//contents = channelFileMap.get(fileName);

//				if (contents == null || "".equals(contents.trim())) {
//					log.warn(" contents of " + fileName + " is null");
//					continue;
//				}

				if (validateAgainstSchema(fileName,
						CHANNEL_CONFIG_SCHEMA_FILE) == false) {

					throw new RuntimeException(
							"error validating channel config file " + fileName);
				}

				localChannelConfig = (ChannelConfig) xstream.fromXML(new FileInputStream(fileName));
				// connection node under channel is default, it user has
				// not provided then set a
				// default connection info object
				initChannelConfigWithDefault(localChannelConfig);
				// in update no new channel or channel id should not
				// change
				// if(isUpdate==false)
				checkDuplicateChannelId(fileName, localChannelConfig);

				//copyToInstanceChannelConfig(localChannelConfig);
				registerChannelsToRegistry(localChannelConfig);

			} catch (Exception exc) {
				log.error("error reading channel config file " + fileName);
				log.error(exc.getMessage());
				throw new RuntimeException(exc);
			}

		}

		if (log.isDebugEnabled())
			log.debug("removing channel config file from configloader map");

		//channelFileMap.clear();
		// isRead = true;
		fileNameChannelIds.clear();

	}

	private void registerChannelsToRegistry(ChannelConfig localChannelConfig) {
	
		List<Channel> channels = localChannelConfig.getChannels();

		for (Channel newChannel : channels) {
			channelRegistry.registerChannel(newChannel.getId(), newChannel);
		}
	}

	private boolean validateAgainstSchema(final String fileName, final String schema) {

		log.debug("validating " + fileName + " against schema channelConfig.xsd");

		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;

		final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
		final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

		final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

		try {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			dbf.setAttribute(JAXP_SCHEMA_SOURCE, getClass()
					.getResourceAsStream(schema));

			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			log.error("Could not replace parser: ");
			log.error(pce.getMessage());
			return false;
		}

		try {

			db.setErrorHandler(new ErrorHandler() {
				@Override
				public void error(SAXParseException arg0) throws SAXException {
					log.error("error validating channel config file "
							+ fileName + "  against schema " + schema);
					log.error(arg0.getMessage());
					throw arg0;
				}

				@Override
				public void fatalError(SAXParseException arg0)
						throws SAXException {
					log.error("error validating channel config file "
							+ fileName + "  against schema " + schema);
					log.error(arg0.getMessage());
					throw arg0;
				}

				@Override
				public void warning(SAXParseException arg0) throws SAXException {
					log.warn(arg0.getMessage());
				}
			});

			//db.parse(new InputSource(new StringReader(contents)));
			db.parse(new InputSource(fileName));

		} catch (Exception e) {
			log.error("Could not create Document: ");
			log.error(e.getMessage());
			return false;
		}
		finally
		{
			//cleanup db, dbf
		}

		return true;

	}

	private void initChannelConfigWithDefault(ChannelConfig localChannelConfig) {
		if (null == localChannelConfig)
			return;

		for (Channel channel : localChannelConfig.getChannels()) {
			if (channel.getConnection() == null) {
				channel.setConnection(new ConnectionInfo());
			}
		}
	}

	private void checkDuplicateChannelId(String newFileName,
			ChannelConfig newChannelConfig) {
		if (null == newChannelConfig)
			return;

		Set<String> newChannelIds = newChannelConfig.getAllUniqueChannelIds();

		Set<String> fileNames = fileNameChannelIds.keySet();

		for (String fileName : fileNames) {

			Set<String> channelIds = fileNameChannelIds.get(fileName);

			for (String channelId : channelIds) {

				if (newChannelIds.contains(channelId)) {
					throw new RuntimeException("Channel config file "
							+ fileName + " already contains channel id "
							+ channelId+" duplicated in file " + newFileName);
					
				}

			}
		}
		// everything fine
		fileNameChannelIds.put(newFileName, newChannelIds);
	}

//	private void copyToInstanceChannelConfig(ChannelConfig newChannelConfig) {
//		if (null == newChannelConfig)
//			return;
//
//		List<Channel> channels = newChannelConfig.getChannels();
//
//		for (Channel newChannel : channels) {
//			Channel instanceChannel = this.channelConfig.getChannel(newChannel
//					.getId());
//			if (instanceChannel == null) {
//				this.channelConfig.addChannel(newChannel);
//			} else {
//				// need to do deepcopy as all the proxy object will be already
//				// holding a channel object
//				// here we need to replace the properties of the same channel
//				// object instead of
//				// creating new channel object.
//				// if we create new channel objects then we have to update the
//				// channel object reference
//				// hold by all the proxy objects
//
//				// assuming we are going to use dozer for object mapping tools
//				// Mapper mapper = new DozerBeanMapper();
//				// source, destination
//				// mapper.map(newChannel, instanceChannel);
//				copyChannel(newChannel, instanceChannel);
//			}
//		}
//	}
//
//	private void copyChannel(Channel source, Channel dest) {
//		if (!source.getId().equals(dest.getId())) {
//			// this is not allowed, Ids should be same
//			String msg = "Source and destination channel id should be same."
//					+ " Source:" + source.getId() + " Dest:" + dest.getId();
//			log.error(msg);
//			throw new RuntimeException(msg);
//		}
//		dest.setServiceType(source.getServiceType());
//		dest.setAddresses(source.getAddresses());
//		dest.setBaseUri(source.getBaseUri());
//		dest.setConnection(source.getConnection());
//		dest.setId(source.getId());
//
//	}
}
