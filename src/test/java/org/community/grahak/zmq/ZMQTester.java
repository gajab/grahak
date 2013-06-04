package org.community.grahak.zmq;



import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.ws.Endpoint;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.BinaryDataProvider;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.community.grahak.annotation.Grahak;
import org.community.grahak.cxf.jaxrs.EchoServer;
import org.community.grahak.cxf.jaxrs.EchoService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.org.apache.cxf.hello_world_zmq.HelloWorldImpl;
import org.org.apache.cxf.hello_world_zmq.HelloWorldPortType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zeromq.ZMQ;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
  "classpath*:applicationContext-test.xml"
   })
public class ZMQTester extends AbstractBusTestServerBase{
	
	 protected final int ZMQ_TEST_PORT = 9000;
	    protected static ZMQ.Context zmqContext;

	    @BeforeClass
	    public static void beforeClass() {
	    	//start service
	    	//ZMQTester tester = new ZMQTester();
	    	//tester.start();
	        zmqContext = ZMQ.context(1);
	    }
	    
	    
	private final static String address = "zmq:(tcp://localhost:9001?socketOperation=connect&socketType=req)";
	@Grahak(endpoint=address)
	HelloWorldPortType client;
    
    @Test
    public void callClient() throws Exception {
       // String address = "zmq:(tcp://localhost:" + ZMQ_TEST_PORT + "?socketOperation=connect&socketType=req)";
    	new Thread(new Runnable() {
            public void run() {
                ZMQ.Socket zmqSocket = zmqContext.socket(ZMQ.REP);
                zmqSocket.bind("tcp://*:9000");
                byte[] request = zmqSocket.recv(0);
                try {
                    //XMLAssert.assertXMLEqual(FileUtils.readFileToString(new File(getClass().getResource("/samples/soap-request.xml").toURI())), new String(request));
                    zmqSocket.send(FileUtils.readFileToString(new File(getClass().getResource("/samples/soap-reply.xml").toURI())));
                	//zmqSocket.send(new String("ZMQ Server:"+request));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                zmqSocket.close();
            }
        }).start();

        String reply = client.sayHello("Claude");
        System.out.println("[Got ZMQ reply "+reply+"]");
        
//        
//        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
//        
//        factory.setServiceClass(HelloWorldPortType.class);
//        factory.setAddress(address);
//        HelloWorldPortType client = (HelloWorldPortType) factory.create();
//        String reply = client.sayHello("Claude");
//        System.out.println(reply);
//        

    }
    
    
    protected void run() {
//		JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
//		//JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
//		sf.setServiceClass(HelloWorldImpl.class);
//		sf.setAddress("http://localhost:" + "9001" + "/");
//		sf.create();  
    	System.out.println("Starting Server");
    	HelloWorldImpl implementor = new HelloWorldImpl();
    	String address = "http://localhost:9000/helloWorld/";
    	Endpoint.publish(address, implementor);
	}

	

}
