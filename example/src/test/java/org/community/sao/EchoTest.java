package org.community.sao;


import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.community.grahak.annotation.Grahak;
import org.community.service.echo.EchoPayload;
import org.community.service.echo.EchoPayloadImpl;
import org.community.service.schemas.EchoPayloadResponseT;
import org.community.service.schemas.EchoPayloadT;
import org.community.service.schemas.xml_error.GrahakFaultDetails;
import org.community.util.XMLUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBException;


@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/grahak-context.xml",
		"classpath:/grahak-example-context.xml" })
public class EchoTest {

	@Grahak(channel="echochannel")
	private EchoPayload echo;

    // battle of plassey 1757 - the british arrive
    private final static String ENDPOINT_ADDRESS = "http://localhost:1757/service-api/";
    private static Server server;

    @BeforeClass
    public static void initialize() throws Exception {
        startServer();
    }

    private static void startServer() throws Exception {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(EchoPayload.class);
        sf.setAddress(ENDPOINT_ADDRESS);
        sf.setServiceBean(new EchoPayloadImpl());
        sf.getInInterceptors().add(new LoggingInInterceptor());
        sf.getOutInterceptors().add(new LoggingOutInterceptor());
        server = sf.create();
    }

    @AfterClass
    public static void destroy() throws Exception {
        server.stop();
        server.destroy();
    }

    /**
     * XML request looks like so:
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Echo_Payload xmlns="service.community.org/schemas">
    <RequestPadding>123456789</RequestPadding>
    <SleepMilliseconds>0</SleepMilliseconds>
    <PayloadSize>4</PayloadSize>
    </Echo_Payload>
    **/
    private EchoPayloadT createRequest()
    {
        EchoPayloadT echoPayloadT = new EchoPayloadT();
        echoPayloadT.setPayloadSize(4);
        echoPayloadT.setRequestPadding("123456789");
        echoPayloadT.setSleepMilliseconds(0);
        String request = null;
        try
        {
            request = XMLUtil.marshalToString(EchoPayloadT.class, echoPayloadT);
        }catch (JAXBException e)
        {
            System.out.println("[Unable to marshal jaxb request]");
            e.printStackTrace();
        }
        System.out.println("[ EchoPayload:request ]\n"+
                           "========================\n"+
                           request +
                           "==========================");
        return echoPayloadT;
    }

	@Test
/*
     response, looks like this:
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Echo_PayloadResponse xmlns="service.community.org/schemas">
    <Payload>XXXX</Payload>
    </Echo_PayloadResponse>
*/
    public void testEchoPayload() {
        try {
            EchoPayloadT echoPayloadT = createRequest();
            EchoPayloadResponseT responseT = null;
            long begin = System.nanoTime();
            responseT = echo.submitEchoPayload(echoPayloadT);
            long end = System.nanoTime();
            double duration = (end - begin) / 1000000d;

            String result = null;
            try {
                result = XMLUtil.marshalToString(EchoPayloadResponseT.class, responseT);
            } catch (JAXBException e) {
                System.out.println("[Unable to marshal jaxb result]");
                e.printStackTrace();
            }
            System.out.println("result of echo:[Time real=" + duration + "(ms)]\n========================\n" +
                    result + "========================");
        } catch (GrahakFaultDetails e) {
            e.printStackTrace();
        }
    }
    
}


