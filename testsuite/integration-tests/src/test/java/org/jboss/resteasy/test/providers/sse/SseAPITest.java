package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
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
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseAPITest.class.getSimpleName());
        war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new RuntimePermission("modifyThread")),
                "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, SseAPIImpl.class, SseAPI.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseAPITest.class.getSimpleName());
    }

    // test for RESTEASY-2017:SSE doesn't work with inherited annotations
    @Test
    public void testAnnotaitonInherited() throws Exception {
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/apitest/events"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();

        try (SseEventSource eventSource = msgEventSource) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            eventSource.register(event -> {
                countDownLatch.countDown();
                results.add(event.readData(String.class));
            }, e -> {
                throw new RuntimeException(e);
            });
            eventSource.open();
            Client sendClient = ClientBuilder.newClient();
            WebTarget sendTarget = sendClient.target(generateURL("/apitest/send"));
            Response response = sendTarget.request().get();
            Assert.assertEquals(204,response.getStatus());
            sendClient.close();
            boolean result = countDownLatch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue("Waiting for event to be delivered has timed out.", result);
        }
        Assert.assertEquals("One event message was expected.", 1, results.size());
        Assert.assertTrue("Expected event contains:AnnotationInheritedEvent, but is:" + results.get(0),
                results.get(0).contains("AnnotationInheritedEvent"));
        client.close();
    }

}
