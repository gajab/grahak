package org.community.grahak.cxf.jaxws;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import javax.xml.ws.Endpoint;

import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.apache.cxf.testutil.common.TestUtil;

public class Server extends AbstractBusTestServerBase {
    public static final String PORT = TestUtil.getPortNumber(Server.class); 

    protected void run() {
        Object implementor = new HWSAXSourcePayloadProvider();
        String address = "http://localhost:" + PORT + "/SOAPServiceProviderRPCLit/SoapPortProviderRPCLit6";
        Endpoint.publish(address, implementor);
    }

    @Test
    //public static void main(String[] args) {
    public void testServer() {
        try {
            Server s = new Server();
            s.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        } finally {
            System.out.println("done!");
        }
    }
}
