package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
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
public class SseEventOrderTest
{

    private static final Logger logger = Logger.getLogger(SseEventOrderTest.class);

    @Deployment
    public static Archive<?> deploy()
    {
        WebArchive war = TestUtil.prepareArchive(SseEventOrderTest.class.getSimpleName());
        war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new RuntimePermission("modifyThread")
        ), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, Arrays.asList(SseResource.class),ExecutorServletContextListener.class);
    }

    private String generateURL(String path)
    {
        return PortProviderUtil.generateURL(path, SseEventOrderTest.class.getSimpleName());
    }

    @Test
    public void testSseOrderMultiTimes() throws Exception {
        for (int i = 0; i < 100; i++) {
            testSseOrder(i);
        }
    }
    public void testSseOrder(int run) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/server-sent-events/sseOrder"));
        SseEventSource eventSource = SseEventSource.target(target).build();
        eventSource.register(event -> {
            String msg = event.readData(String.class);
            results.add(msg);
            if (msg.startsWith("last-msg")) {
                latch.countDown();
            }
        }, ex -> {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        });
        eventSource.open();
        boolean await = latch.await(30, TimeUnit.SECONDS);
        Assert.assertTrue("Waiting for event to be delivered has timed out.", await);
        eventSource.close();
        Assert.assertFalse(eventSource.isOpen());
        for (int i = 0; i < results.size(); i++) {
            Assert.assertTrue("Wrong message order in run " + run + " " + results + " and results.get(" + i  + ") is unexpected :" + results.get(i), results.get(i).endsWith("-" + i));
        }
    }
}