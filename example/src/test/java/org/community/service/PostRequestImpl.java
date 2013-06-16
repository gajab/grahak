package org.community.service;


import javax.ws.rs.*;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class PostRequestImpl implements IPostRequest {
    @POST
    @Consumes("text/plain")
    @Produces("text/plain")
    @Path("/customer")
    public  String addCustomer(String customer) {
        System.out.println("[Hello "+customer+"]");
        return "[Hello "+ customer+"]";
    }

}
