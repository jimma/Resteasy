package org.jboss.resteasy.test.providers.jackson2.perf;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Path("hello")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BasicJsonResource {
    @GET
    @Path("advanced")
    public BasicJsonAdvancedObject advancedGreet(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {
        BasicJsonAdvancedObject hello = new BasicJsonAdvancedObject();
        hello.setLangCode("en");
        hello.setTimeStamp(new Date());
        hello.setGreetingFrom(new BasicJsonPerson("Eugene", "Cuckoo"));
        hello.setGreetingTo(new BasicJsonRunnerPerson("Asics", 66, "Alfred", "Runner"));
        hello.add(new BasicJsonRunnerPerson("Asics", 66, "Alfred", "Runner"));
        hello.add(new BasicJsonRunnerPerson("Mizuno", 70, "Rebecca", "Kid"));
        hello.setOfficial(false);
        hello.setMessage("Dear Mr/Mrs " + lastName + ",   Hello.  Kind regards, Eugene Cuckoo");
        return hello;
    }
}
