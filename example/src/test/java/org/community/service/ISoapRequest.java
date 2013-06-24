package org.community.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


/**
 */
@WebService
public interface ISoapRequest {
    @WebMethod
    public  void addCustomer(@WebParam(name="customer") String customer);
    public String getCustomer(@WebParam(name="id")Integer id) throws NoIdMatchException;
}
