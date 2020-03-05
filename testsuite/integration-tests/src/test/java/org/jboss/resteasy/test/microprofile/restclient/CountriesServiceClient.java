package org.jboss.resteasy.test.microprofile.restclient;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.concurrent.CompletionStage;

@Path("/api")
@RegisterRestClient
@RegisterProvider(NotFoundResponseExceptionMapper.class)
public interface CountriesServiceClient {

    @GET
    @Path("/name/{name}")
    @Produces("application/json")
    Country getByName(@PathParam("name") String name);

    @GET
    @Path("/name/{name}")
    @Produces("application/json")
    CompletionStage<Country> getByNameAsync(@PathParam("name") String name);
}