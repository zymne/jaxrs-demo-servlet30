package ibs.sandbox.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/greet")
public class Greeting {

    @GET
    public String welcome() {

        return "Welcome to Jersey";
    }
}
