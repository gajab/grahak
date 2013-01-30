package org.community.grahak.cxf.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/echoservice")
public class EchoService {
		
	@GET
    @Path("echo/{message}")
    @Produces("text/plain")
    public String echo(@PathParam("message") String message) {
        return message;
    }
}


