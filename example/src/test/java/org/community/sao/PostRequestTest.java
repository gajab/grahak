package org.community.sao;


import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.List;

//STATIC IMPORTS
import static org.junit.Assert.assertEquals;

// community impls
import org.community.grahak.annotation.Grahak;
import org.community.service.IPostRequest;
import org.community.service.PostRequestImpl;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/grahak-context.xml",
		"classpath:/grahak-example-context.xml" })
public class PostRequestTest {

	@Grahak(channel="postchannel")
	private IPostRequest postRequest;


    private final static String ENDPOINT_ADDRESS = "http://localhost:1930/service-api/";
    private static Server server;

    @BeforeClass
    public static void initialize() throws Exception {
        startServer();
    }

    private static void startServer() throws Exception {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(PostRequestImpl.class);

        List<Object> providers = new ArrayList<Object>();
        // add custom providers if any
        sf.setProviders(providers);

        sf.setResourceProvider(PostRequestImpl.class,
                               new SingletonResourceProvider(new PostRequestImpl(), true));
        sf.setAddress(ENDPOINT_ADDRESS);

        server = sf.create();
    }

    @AfterClass
    public static void destroy() throws Exception {
        server.stop();
        server.destroy();
    }


	@Test
	public void testPostRequest() {

        try
        {
            postRequest.addCustomer("Willie-Nillie");
        }
        // Sanity check that we can always catch this error
        catch (WebApplicationException e) 
        {
            System.out.println("===ERROR calling JAX-RS Service====");
            int status = e.getResponse().getStatus();
            System.out.println("status code " + status);
            System.out.println(e.getResponse().toString());
            assertEquals(401,status); 
        }
	}
}
