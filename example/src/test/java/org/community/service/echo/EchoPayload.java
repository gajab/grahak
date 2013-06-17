package org.community.service.echo;

import org.community.service.schemas.EchoPayloadResponseT;
import org.community.service.schemas.xml_error.GrahakFaultDetails;
import org.community.service.schemas.EchoPayloadT;

import javax.jws.WebParam;

@javax.jws.WebService(name = "Echo_Payload")
@javax.jws.soap.SOAPBinding(parameterStyle = javax.jws.soap.SOAPBinding.ParameterStyle.BARE)
public interface EchoPayload  {
    
    @javax.jws.WebResult(name = "Echo_PayloadResponse", targetNamespace = "http://service.community.org/schemas", partName = "Echo_PayloadResponse")
    @javax.jws.WebMethod(operationName = "SubmitEcho_Payload", action = "svc:SubmitEcho_Payload")
    EchoPayloadResponseT submitEchoPayload(@WebParam(partName = "Echo_Payload", name = "Echo_Payload", targetNamespace = "http://service.community.org/schemas") EchoPayloadT echoPayloadT)
                throws GrahakFaultDetails;

    @javax.jws.WebResult(name = "Echo_PayloadResponse", targetNamespace = "http://service.community.org/schemas", partName = "Echo_PayloadResponse")
    @javax.jws.WebMethod(operationName = "Throw_Exception", action = "svc:Throw_Exception")
    EchoPayloadResponseT throwException(@javax.jws.WebParam(partName = "Echo_Payload", name = "Echo_Payload", targetNamespace = "http://service.community.org/schemas") org.community.service.schemas.EchoPayloadT echoPayloadT)
            throws GrahakFaultDetails;

}
