package ibs.sandbox.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/admin")
public class AdminHandler {

    @GET
    public Response admin() {
        return Response.ok("Welcome to Admin Console").build();
    }

}
