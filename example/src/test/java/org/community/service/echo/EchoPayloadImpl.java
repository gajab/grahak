package org.community.service.echo;

import org.community.service.echo.EchoPayload;
import org.community.service.schemas.EchoPayloadResponseT;
import org.community.service.schemas.EchoPayloadT;
import org.community.service.schemas.xml_error.FaultdetailsT;
import org.community.service.schemas.xml_error.GrahakFaultDetails;

import javax.jws.WebParam;

public class EchoPayloadImpl  implements  EchoPayload {
	int hitCount;
	public EchoPayloadResponseT submitEchoPayload(EchoPayloadT echoPayload) throws GrahakFaultDetails {
		EchoPayloadResponseT  response = new EchoPayloadResponseT();
		System.out.println("Number of times EchoPayload called [" + (++hitCount)+"]");
		String responseStr = getStringWithLengthAndFilledWithCharacter(echoPayload.getPayloadSize(), 'X');
		response.setPayload(responseStr);
		try {
			Thread.sleep(echoPayload.getSleepMilliseconds());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

    @Override
    public EchoPayloadResponseT throwException(@WebParam(partName = "Echo_Payload", name = "Echo_Payload", targetNamespace = "http://service.community.org/schemas") EchoPayloadT echoPayloadT) throws GrahakFaultDetails {
        FaultdetailsT faultdetailsT = new FaultdetailsT();
        faultdetailsT.setErrorcode(500);
        faultdetailsT.setMessage("Grahak SoapException Example");
        throw new GrahakFaultDetails("[Example SOAPFaultException]", faultdetailsT);
    }

    protected String getStringWithLengthAndFilledWithCharacter(int length, char charToFill) {
	    char[] array = new char[length];
	    int pos = 0;
	    while (pos < length) {
	        array[pos] = charToFill;
	        pos++;
	    }
	    return new String(array);
	}

}
