package org.community.service;


import javax.ws.rs.*;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
 */
public interface IPostRequest {
    @POST
    @Consumes("text/plain")
    @Produces("text/plain")
    @Path("/customer")
    public  String addCustomer(String customer);
}
