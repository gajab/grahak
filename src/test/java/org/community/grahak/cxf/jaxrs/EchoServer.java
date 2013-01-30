package org.community.grahak.cxf.jaxrs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.BinaryDataProvider;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.community.grahak.cxf.jaxrs.EchoService;

public class EchoServer extends AbstractBusTestServerBase {
	
	public static final String PORT = allocatePort(EchoServer.class);

	protected void run() {
		
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setResourceClasses(EchoService.class);

		List<Object> providers = new ArrayList<Object>();


		BinaryDataProvider p = new BinaryDataProvider();
		p.setProduceMediaTypes(Collections.singletonList("application/bar"));
		p.setEnableBuffering(true);

		providers.add(p);
		sf.setProviders(providers);
		sf.setResourceProvider(EchoService.class,
				new SingletonResourceProvider(new EchoService(), true));
		sf.setAddress("http://localhost:" + PORT + "/");

		sf.create();        
	}

	public static void main(String[] args) {
		try {
			EchoServer s = new EchoServer();
			s.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		} 
	}
}