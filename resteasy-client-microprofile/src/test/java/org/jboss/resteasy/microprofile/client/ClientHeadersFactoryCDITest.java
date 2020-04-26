package org.jboss.resteasy.microprofile.client;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientHeadersFactoryCDITest {

   private static UndertowJaxrsServer server;
   private static WeldContainer container;

   static class Worker {

      @Inject
      @RestClient
      private TestResourceIntf service;

      public String work() {
         return service.hello("Stefano");
      }
   }

   @Path("/")
   @RegisterRestClient(baseUri="http://localhost:8081")
   @RegisterClientHeaders(TestClientHeadersFactory.class)
   @ClientHeaderParam(name="IntfHeader", value="intfValue")
   public interface TestResourceIntf {

      @Path("hello/{h}")
      @GET
      String hello(@PathParam("h") String h);
      @GET
      @Path("date")
      String getDate(@QueryParam("date") LocalDate date);
   }

   @Path("/")
   public static class TestResource {

      @Path("hello/{h}")
      @GET
      public String hello(@PathParam("h") String h) {
         return "hello " + h;
      }
      @GET
      @Path("date")
      public String getDate(@QueryParam("date") LocalDate date) {
          return "QueryParam:" + date;
      }
   }

   @ApplicationScoped
   public static class TestClientHeadersFactory implements ClientHeadersFactory {

      @Inject
      private Counter counter;

      public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {
         counter.count();
         return new MultivaluedHashMap<>();
      }
   }

   @ApplicationScoped
   public static class Counter {

       public static final AtomicInteger COUNT = new AtomicInteger(0);

       public int count() {
           return COUNT.incrementAndGet();
       }
   }

   @ApplicationPath("")
   public static class MyApp extends Application {

      @Override
      public Set<Class<?>> getClasses() {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         classes.add(DateParamConverter.class);
         classes.add(DateParamConverterProvider.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      Weld weld = new Weld();
      weld.addBeanClass(Worker.class);
      weld.addBeanClass(TestResourceIntf.class);
      weld.addBeanClass(TestClientHeadersFactory.class);
      weld.addBeanClass(Counter.class);
      container = weld.initialize();
      server = new UndertowJaxrsServer().start();
      server.deploy(MyApp.class);
   }

   @AfterClass
   public static void stop() throws Exception {
      server.stop();
      container.shutdown();
   }
   @Test
   public void test() {
      String result = container.select(Worker.class).get().work();
      Assert.assertEquals("hello Stefano", result);
      Assert.assertEquals(1, Counter.COUNT.get());
   }
   @Test
   public void testNullParam() throws Exception{
     TestResourceIntf client = org.eclipse.microprofile.rest.client.RestClientBuilder.newBuilder()
               .baseUri(new URI("http://localhost:8081"))
               .register(DateParamConverterProvider.class)
               .build(TestResourceIntf.class);
     Assert.assertEquals("Unexpected result", "QueryParam:2020-01-01", client.getDate(LocalDate.of(2020, 01, 01)));
     Assert.assertEquals("Unexpected result", "QueryParam:null", client.getDate(null));
   }
}
