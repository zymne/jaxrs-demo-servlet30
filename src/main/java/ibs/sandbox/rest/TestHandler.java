package ibs.sandbox.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/test")
public class TestHandler {

    @GET
    public void test1(@QueryParam("q1") String name, @QueryParam("q2") String surname) {
        System.out.println(surname + " " + name);
    }

    @GET
    @Path("/{q1}/{q2}")
    public void test2(@PathParam("q1") String name, @PathParam("q2") String surname) {
        System.out.println(surname + " " + name);
    }
}
