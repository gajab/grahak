package org.community.service.schemas.xml_error;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.community.service.schemas.xml_error package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Faultdetails_QNAME = new QName("http://service.community.org/schemas/xml_error/", "faultdetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.community.service.schemas.xml_error
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FaultdetailsT }
     * 
     */
    public FaultdetailsT createFaultdetailsT() {
        return new FaultdetailsT();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultdetailsT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.community.org/schemas/xml_error/", name = "faultdetails")
    public JAXBElement<FaultdetailsT> createFaultdetails(FaultdetailsT value) {
        return new JAXBElement<FaultdetailsT>(_Faultdetails_QNAME, FaultdetailsT.class, null, value);
    }

}
