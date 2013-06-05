package org.community.grahak.zmq;

import javax.xml.ws.Endpoint;

//import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.community.grahak.annotation.Grahak;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext-test.xml" })
public class ZMQTester {

	@BeforeClass
	public static void beforeClass() {
		startServer();
	}

	private static void startServer() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Starting Server");
				HelloWorldImpl implementor = new HelloWorldImpl();
				Endpoint.publish(
						"zmq:(tcp://*:9000?socketOperation=bind&socketType=rep)",
						implementor);

			}
		}).start();
	}

	//private final static String address = "zmq:(tcp://localhost:9000?socketOperation=connect&socketType=req)";
	
	private final static String address = "zmq://localhost:9000/";
	@Grahak(endpoint = address)
	HelloWorld zmqHelloWorldClient;

	@Test
	public void callClient() throws Exception {
		System.out.println("@@@@@@@@@@@@@@@@@");

		// this does not work. server started but never come out of it and next
		// line of the code does not get executed.
		// new AbstractBusTestServerBase() {
		//
		// @Override
		// protected void run() {
		// System.out.println("Starting Server");
		// HelloWorldImpl implementor = new HelloWorldImpl();
		// Endpoint.publish(
		// "zmq:(tcp://*:9000?socketOperation=bind&socketType=rep)",
		// implementor);
		//
		//
		//
		// }
		// }.start();

		Thread.sleep(1000);

		System.out.println("server started");
		try {
			String reply = zmqHelloWorldClient.sayHello("Claude");
			System.out.println("[Got ZMQ reply " + reply + "]");
		} catch (Exception e) {
			
			System.out.println("EXCEPTION " + e.getMessage());
			e.printStackTrace();
		}

	}

}
