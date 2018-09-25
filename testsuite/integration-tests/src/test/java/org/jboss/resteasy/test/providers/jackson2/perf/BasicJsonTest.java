package org.jboss.resteasy.test.providers.jackson2.perf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly13;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.core.basic.resource.ApplicationTestScannedApplication;
import org.jboss.resteasy.test.exception.ExceptionHandlingTest;
import org.jboss.resteasy.test.exception.resource.ExceptionHandlingProvider;
import org.jboss.resteasy.test.exception.resource.ExceptionHandlingResource;
import org.jboss.resteasy.test.providers.jackson2.Jackson2Test;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonDatatypeEndPoint;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonDatatypeJacksonProducer;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

@RunWith(Arquillian.class)
@RunAsClient
public class BasicJsonTest
{

   public static List<String> errorResult = new ArrayList<String>();

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(BasicJsonTest.class.getSimpleName());
      Map<String, String> contextParams = new HashMap<>();
      contextParams.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
      return TestUtil.finishContainerPrepare(war, contextParams, BasicJsonAdvancedObject.class,
            BasicJsonResource.class, BasicJsonPerson.class, BasicJsonRunnerPerson.class);
   }

   @Before
   public void init()
   {
      //client = new ResteasyClientBuilder().connectionPoolSize(15).build();
   }

   @After
   public void after() throws Exception
   {
      //client.close();
   }

   private static String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, BasicJsonTest.class.getSimpleName());
   }
   @Test
   public void testSleep() throws Exception {
      Thread.sleep(9999999);
   }

   @org.junit.Ignore
   public void testBasic() throws Exception
   {
      Options opt = new OptionsBuilder().include(this.getClass().getName() + ".*")
            .timeUnit(TimeUnit.SECONDS).warmupTime(TimeValue.seconds(10)).warmupIterations(1)
            .measurementTime(TimeValue.seconds(10)).measurementIterations(1).threads(5).forks(1)
            .shouldFailOnError(true).shouldDoGC(true)
            .build();
      new Runner(opt).run();
      System.out.println("Unexpected results : " + errorResult.size());
   }

   public static class ResteasyBenchmark
   {

      @State(Scope.Benchmark)
      public static class ClientState {
         public ResteasyClient client = new ResteasyClientBuilder().connectionPoolSize(15).build();
         public WebTarget target = client.target("http://localhost:8080/BasicJsonTest/hello/advanced").queryParam("firstName", "Tom").queryParam("lastName", "Jack");
      }
      @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
      public void benchmarkBasicJson(ClientState state) {
         String res = state.target.request().get().readEntity(String.class);
         if(!res.contains("firstName")) {
            errorResult.add(res);
         }
      }
   }

}
