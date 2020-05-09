package org.jboss.resteasy.test.providers.sse;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class SseAPITest {
    private static final Logger logger = Logger.getLogger(SseAPITest.class);
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseAPITest.class.getSimpleName());
        war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new RuntimePermission("modifyThread")),
                "permissions.xml");
        List<Class<?>> singletons = new ArrayList<Class<?>>();
        singletons.add(SseAPIImpl.class);
        return TestUtil.finishContainerPrepare(war, null, singletons, SseAPI.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseAPITest.class.getSimpleName());
    }

    // test for RESTEASY-2017:SSE doesn't work with inherited annotations
    @Test
    public void testAnnotaitonInherited() throws Exception
    {
       final CountDownLatch latch = new CountDownLatch(1);
       final List<String> results = new ArrayList<String>();
       Client client = ClientBuilder.newBuilder().build();
       WebTarget target = client.target(generateURL("/apitest/events"));
       SseEventSource msgEventSource = SseEventSource.target(target).build();
       try (SseEventSource eventSource = msgEventSource)
       {
          eventSource.register(event -> {
             latch.countDown();
             String msg = event.readData(String.class);
             logger.info("*** Client get message: ****" + msg);
             results.add(msg);
          }, ex -> {
                throw new RuntimeException(ex);
             }) ;
          eventSource.open();

          Client messageClient = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).connectionPoolSize(10).build();
          WebTarget messageTarget = messageClient.target(generateURL("/apitest/send"));
          Response response = messageTarget.request().post(Entity.text("apimsg"));
          Assert.assertEquals("apimsg",response.readEntity(String.class));
          Assert.assertTrue("event source is not opened", eventSource.isOpen());
          boolean result = latch.await(30, TimeUnit.SECONDS);
          Assert.assertTrue("Waiting for event to be delivered has timed out.", result);
          messageClient.close();
       } catch (Exception e) {
           fail(e.getMessage());
       }
       Assert.assertEquals("One event message was expected.", 1, results.size());
       Assert.assertTrue("Expected event contains apimsg, but is:" + results.get(0),
               results.get(0).contains("apimsg"));
       client.close();
    }

}