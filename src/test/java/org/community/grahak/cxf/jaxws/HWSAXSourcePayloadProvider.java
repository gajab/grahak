package org.community.grahak.cxf.jaxws; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;

import org.apache.cxf.helpers.FileUtils;
import org.apache.cxf.staxutils.StaxUtils;


//The following wsdl file is used.
//wsdlLocation = "trunk/src/test/resources/wsdl/hello_world_rpc_lit.wsdl"
/**
@WebServiceProvider(portName = "SoapPortProviderRPCLit6", serviceName = "SOAPServiceProviderRPCLit", targetNamespace = "http://apache.org/hello_world_rpclit",
                    wsdlLocation = "/wsdl/hello_world_rpc_lit.wsdl")
**/
@WebServiceProvider(portName = "SoapPortProviderRPCLit6", serviceName = "SOAPServiceProviderRPCLit", targetNamespace = "http://apache.org/hello_world_rpclit",
                    wsdlLocation = "hello_world_rpc_lit.wsdl")
@ServiceMode(value = Service.Mode.PAYLOAD)
public class HWSAXSourcePayloadProvider implements Provider<SAXSource> {
    
    private static QName sayHi = new QName("http://apache.org/hello_world_rpclit", "sayHi");
    private static QName greetMe = new QName("http://apache.org/hello_world_rpclit", "greetMe");
    
    @Resource 
    WebServiceContext ctx;

    
    private MessageFactory factory;
    private InputSource sayHiInputSource;
    private InputSource greetMeInputSource;

    public HWSAXSourcePayloadProvider() {

        try {
            factory = MessageFactory.newInstance();
            //InputStream is = getClass().getResourceAsStream("resources/sayHiRpcLiteralResp.xml");
            InputStream is = getClass().getResourceAsStream("resources/sayHiRpcLiteralResp.xml");
            Document sayHiDocument = factory.createMessage(null, is).getSOAPBody().extractContentAsDocument();
            sayHiInputSource = new InputSource(getSOAPBodyFile(sayHiDocument).toURI().toString());
            
            InputStream is2 = getClass().getResourceAsStream("resources/GreetMeRpcLiteralResp.xml");
            Document greetMeDocument = 
                factory.createMessage(null, is2).getSOAPBody().extractContentAsDocument();
            greetMeInputSource = new InputSource(getSOAPBodyFile(greetMeDocument).toURI().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SAXSource invoke(SAXSource request) {
        QName qn = (QName)ctx.getMessageContext().get(MessageContext.WSDL_OPERATION);
        if (qn == null) {
            throw new RuntimeException("No Operation Name");
        }
        SAXSource response = new SAXSource();
        try {
            
            DOMResult domResult = new DOMResult();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(request, domResult);
            Node n = domResult.getNode().getFirstChild();

            while (n.getNodeType() != Node.ELEMENT_NODE) {
                n = n.getNextSibling();
            }
            if (n.getLocalName().equals(sayHi.getLocalPart())) {
                response.setInputSource(sayHiInputSource);
                
            } else if (n.getLocalName().equals(greetMe.getLocalPart())) {
                response.setInputSource(greetMeInputSource);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }
    
    private File getSOAPBodyFile(Document doc) throws Exception {
        File file = FileUtils.createTempFile("cxf-systest", "xml");
        FileOutputStream out = new FileOutputStream(file);
        XMLStreamWriter writer = StaxUtils.createXMLStreamWriter(out);
        StaxUtils.writeDocument(doc, writer, true);
        writer.close();
        return file;
    }

}
