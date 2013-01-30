package org.community.grahak.cxf.jaxws;

import javax.jws.WebService;

@WebService
public interface EchoService { 

	public String echo(String msg);
	public String anotherEcho(String msg);
}

