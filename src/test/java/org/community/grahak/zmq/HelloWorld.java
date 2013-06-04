package org.community.grahak.zmq;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface HelloWorld {
	  public String sayHello(@WebParam(name = "firstName") String firstName) ;
}
