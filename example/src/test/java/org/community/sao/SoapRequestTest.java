package org.community.sao;


import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.community.grahak.annotation.Grahak;
import org.community.service.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/grahak-context.xml",
		"classpath:/grahak-example-context.xml" })
public class SoapRequestTest {

	@Grahak(channel="soapchannel")
	private ISoapRequest soapRequest;
    
    private static final String CUSTOMER = "Grahak";

    // in 1612 the dutch arrived in Surat, India
    // derive the address from channel config file (same location, there's no need to do this)
    private final static String ENDPOINT_ADDRESS = "http://localhost:1612/service-api/";
    private static Server server;

    @BeforeClass
    public static void initialize() throws Exception {
        startServer();
    }

    private static void startServer() throws Exception {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(ISoapRequest.class);
        sf.setAddress(ENDPOINT_ADDRESS);
        sf.setServiceBean(new SoapRequestImpl());
        sf.getInInterceptors().add(new LoggingInInterceptor());
        sf.getOutInterceptors().add(new LoggingOutInterceptor());
        server = sf.create();
    }

    @AfterClass
    public static void destroy() throws Exception {
        server.stop();
        server.destroy();
    }


	@Test
	public void testSoapRequest() {
            soapRequest.addCustomer(CUSTOMER);
    }
    
    @Test
    public void testGetSoapRequest() throws NoIdMatchException {
        String customer = soapRequest.getCustomer(1);
        assert CUSTOMER.equals(customer);
    }

    @Test(expected = NoIdMatchException.class)
    public void negativeSoapTest() throws NoIdMatchException{
       String customer = soapRequest.getCustomer(2);
    }
}


