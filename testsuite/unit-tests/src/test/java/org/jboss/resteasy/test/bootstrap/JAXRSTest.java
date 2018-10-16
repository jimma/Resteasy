package org.jboss.resteasy.test.bootstrap;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.JAXRS;
import javax.ws.rs.JAXRS.Instance;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.Assert;
import org.junit.Test;

public class JAXRSTest
{

   @Test
   public void testJAXRS() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8080)
            .rootPath("bootstrap").build();
      CompletionStage<Instance> instance = JAXRS.start(new StandaloneApplication(), configuration);
      try
      {
         CompletionStage<Void> request = instance.thenAccept(ins -> {
            try (Client client = ClientBuilder.newClient())
            {
               Assert.assertEquals("BootStrapApi", client.target("http://localhost:8080/bootstrap/produces/string")
                     .request().get(String.class));
            }
         });
         request.toCompletableFuture().get();
      }
      finally
      {
         if (instance.toCompletableFuture().isCompletedExceptionally())
         {
            Assert.fail("Failed to start server with bootstrap api");
         }
         else
         {
            instance.toCompletableFuture().get().stop();
         }
      }
   }

   @Test
   public void testFailedStartJAXRS() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8080).rootPath("error")
            .build();
      CompletionStage<Instance> instance = JAXRS.start(new ErrorApplication(), configuration);
      try
      {
         instance.toCompletableFuture().get();
      }
      catch (Exception e)
      {
         Assert.assertTrue("Server failed is expected", e.getMessage().indexOf("Could not find constructor") > -1);
      }
      
   }

   public class StandaloneApplication extends javax.ws.rs.core.Application
   {
      public final Set<Object> singletons = new HashSet<Object>();

      public Set<Object> getSingletons()
      {
         singletons.add(new StringResource());
         return singletons;
      }

   }

   public class ErrorApplication extends javax.ws.rs.core.Application
   {
      // Error applicaton the StringResource requires a constructor
      public final Set<Class<?>> classes = new HashSet<Class<?>>();

      public Set<Class<?>> getClasses()
      {
         classes.add(StringResource.class);
         return classes;
      }
   }

   @Path("/")
   public class StringResource
   {
      @GET
      @Path("produces/string")
      @Produces("text/plain")
      public String produceString()
      {
         return "BootStrapApi";
      }
   }
}
