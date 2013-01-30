package org.community.grahak.channel.xstream.converter;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.community.grahak.channel.ConnectionInfo;
import org.community.grahak.channel.xstream.converter.ConnectionInfoConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import static org.junit.Assert.assertEquals;


public class ConnectionInfoConverterTest {

	private static final String XML_FILENAME = "/ChannelConfig.xml";

	@Before
	public void setUp() {

	}
	
	@Test
	public void testDefaultConnectionInfoValues() throws Exception {
		String xmlContent = loadXmlAsResourceToString();
		XStream xstream = new XStream(new DomDriver());		
		xstream.alias("connection", ConnectionInfo.class);		
		xstream.registerConverter(new ConnectionInfoConverter());
		ConnectionInfo connectionInfo = (ConnectionInfo) xstream
				.fromXML(xmlContent);
		assertEquals("recieveTimeout",60000,connectionInfo.getReceiveTimeout());
		assertEquals("maxThred",50,connectionInfo.getMaxThreads());
		
	}

	private String loadXmlAsResourceToString() throws IOException {
		URL xsdUrl = this.getClass().getResource(XML_FILENAME);
		File xmlFile = new File(xsdUrl.getFile());
		String content = FileUtils.readFileToString(xmlFile);
		return content;
	}
	
	@After
	public void tearDown() {

	}
}
