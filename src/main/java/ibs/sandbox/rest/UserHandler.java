package ibs.sandbox.rest;

import javax.ws.rs.*;

@Path("/user")
public class UserHandler {

    @POST
    @Path("/create")
    @Consumes("application/x-www-form-urlencoded")
    public void createUser(@FormParam("username") String name, @FormParam("password") String password) {
        System.out.println(name);
//        System.out.println(password);
//
//        return Response.ok().build();
    }
}
