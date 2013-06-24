package org.community.service;

/**
 */
import javax.jws.WebService;
import java.util.concurrent.ConcurrentHashMap;

@WebService
public class SoapRequestImpl implements ISoapRequest {
    // in memory cache
    private ConcurrentHashMap<Integer,String> customerDB = new ConcurrentHashMap<Integer, String>();
    private int id = 1;

    public void addCustomer(String customer)  {
        //add customer to database
        System.out.println("[Hello "+ customer+"]");
        customerDB.put(id,customer);
        //increment id for next customer
        id = id+1;
    }
    
    public String getCustomer(Integer id) throws NoIdMatchException {
        String customer = customerDB.get(id);
        if (customer == null)
            throw new NoIdMatchException("No Customer exists with Id ["+id+"] in DB");
        return customer;
    }
}

