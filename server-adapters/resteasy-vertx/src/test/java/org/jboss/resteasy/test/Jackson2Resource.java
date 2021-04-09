package org.jboss.resteasy.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/products")
public class Jackson2Resource {

   @GET
   @Produces("application/json")
   public Jackson2Product getProduct() {
      return new Jackson2Product(333, "JBoss");
   }

   @POST
   @Produces("application/json")
   @Consumes("application/json")
   @Path("/add")
   public Jackson2Product post(Jackson2Product p) {
      return p;
   }

}
