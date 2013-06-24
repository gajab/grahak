package org.community.util;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.io.OutputStream;
import java.io.StringWriter;

/** Class which un/marshals objects to XML */
public class XMLUtil {
    private static Marshaller marshaller = null;
    private static Unmarshaller unmarshaller = null;
    private static JAXBContext context = null;

    public static void marshal(Class<?> klass, Object obj, OutputStream os) throws JAXBException {
        try {
            context = JAXBContext.newInstance(klass);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("There was a problem creating a JAXBContext object for formatting the object to XML.");
        }

        try {
            marshaller.marshal(obj, os);
        } catch (JAXBException jaxbe) {
            jaxbe.printStackTrace();
        }
    }

    public static String marshalToString(Class<?> klass, Object obj) throws JAXBException {
        try {
            context = JAXBContext.newInstance(klass);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("There was a problem creating a JAXBContext object for formatting the object to XML.");
        }
        StringWriter sw = new StringWriter();
        String result = null;
        try {
            marshaller.marshal(obj, sw);
            result = sw == null? null: sw.toString();
        } catch (JAXBException jaxbe) {
            jaxbe.printStackTrace();
        }
        return  result;
    }
}
