package org.community.grahak.spring.bean.factory.annotation;

import java.util.List;
import java.util.Map;

import org.community.grahak.annotation.Grahak;
import org.community.grahak.cxf.jaxws.EchoService;
import org.springframework.stereotype.Component;


@Component
public class EchoDAO  
{
	@Grahak(channel = "EchoServiceChannel", serviceName="Echo")
	public EchoService echoService = null;

	@Grahak(channel = "EchoServiceChannel", serviceName="Echo")
	public EchoService anotherEchoService = null;


	public String echo() {
		String message = "Zdrastvuiytiye";
		String serverEcho = echoService.echo(message);
        return serverEcho;
	
	}
	
	public String anotherEcho()
	{
		String message = "Mulchanie eta ne khorosho";
		String serverEcho = anotherEchoService.echo(message);

        return serverEcho;
	}
}
