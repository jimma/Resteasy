package org.jboss.resteasy.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class VertxHelloTest {
    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        VertxContainer.start().getRegistry().addPerRequestResource(Jackson2Resource.class);
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        VertxContainer.stop();
    }

    //@Test
    public void testBasic() throws Exception {
        String[] command = {"/bin/bash", "-c", "wrk -c 30 -d 1m -t 30 http://localhost:8081/vertx/hello"};
        Process process = new ProcessBuilder(command).start();
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
        reader.lines().forEach(string -> System.out.println(string));
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
    }

    @Test
    public void testJason() throws Exception {
        String[] command = {"/bin/bash", "-c", "wrk -c 30 -d 1m -t 30 http://localhost:8081/products"};
        Process process = new ProcessBuilder(command).start();
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
        reader.lines().forEach(string -> System.out.println(string));
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
    }
    //wrk

    @Test
    public void testJasonPost() throws Exception {
        String postLua = VertxHelloTest.class.getClassLoader().getResource("./wrk/productpost.lua").getFile();
        String[] command = {"/bin/bash", "-c", "wrk -c 30 -d 1m -t 30 -s " + postLua + " http://localhost:8081/products/add"};
        Process process = new ProcessBuilder(command).start();
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
        reader.lines().forEach(string -> System.out.println(string));
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
    }


    //@Test
    public void testJasonSmoke() throws Exception {
        /*for (int i =0 ; i < 5; i++) {
            WebTarget target = client.target("http://localhost:8081/products");
            String val = target.request().get(String.class);
            //Assert.assertEquals("hello world", val);
        }*/
        //Thread.sleep(100000000);
        WebTarget target = client.target("http://localhost:8081/products");
        String val = target.request().get(String.class);
        Assert.assertEquals("hello world", val);
    }
}
