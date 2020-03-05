package org.jboss.resteasy.test.microprofile.restclient;

import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(Arquillian.class)
@RunAsClient
public class CountryClientTest {

   @ArquillianResource
   URL url;

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(CountryClientTest.class.getSimpleName());
      war.addClass(CountriesServiceClient.class);
      war.addClass(Country.class);
      war.addClass(NotFoundResponseExceptionMapper.class);
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addClass(CountryResource.class);
      war.addAsManifestResource(new StringAsset("Dependencies: org.eclipse.microprofile.restclient,org.jboss.resteasy.resteasy-rxjava2 services\n"), "MANIFEST.MF");
      war.addAsManifestResource(new StringAsset("org.jboss.resteasy.test.microprofile.restclient.CountriesServiceClient/mp-rest/url=http://localhost:8080/CountryTest\norg.jboss.resteasy.test.microprofile.restclient.CountriesServiceClient/mp-rest/scope=javax.inject.Singleton"), "microprofile-config.properties");
      Archive<?> archive = TestUtil.finishContainerPrepare(war, null);
      archive.as(ZipExporter.class).exportTo(new java.io.File("/home/jimma/tmp/CountryClientTest.war"), true);
      return archive;
   }

   @Deployment
   public static Archive<?> deploy2()
   {
      WebArchive war = TestUtil.prepareArchive("CountryTest");
      war.addClass(Country.class);
      war.addClass(CountryProviderResource.class);
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      //war.addAsManifestResource(new StringAsset("Dependencies: org.eclipse.microprofile.restclient,org.jboscggggggggggggggggggg s.resteasy.resteasy-rxjava2 services\n"), "MANIFEST.MF");
      //war.addAsManifestResource(new StringAsset("org.jboss.resteasy.test.microprofile.restclient.CountriesServiceClient/mp-rest/url=http://localhost:8080/CountryClientTest\norg.jboss.resteasy.test.microprofile.restclient.CountriesServiceClient/mp-rest/scope=javax.inject.Singleton"), "microprofile-config.properties");
      Archive<?> archive = TestUtil.finishContainerPrepare(war, null);
      archive.as(ZipExporter.class).exportTo(new java.io.File("/home/jimma/tmp/CountrycTest.war"), true);
      return archive;
   }
   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, CountryClientTest.class.getSimpleName());
   }

   @Test
   public void testGetCompletionStage() throws Exception
   {
       ResteasyClient client = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).connectionPoolSize(10).build();

       WebTarget base = client.target(generateURL("/country/cdi/USA"));
       Response response = base.request().get();
       Country country = response.readEntity(Country.class);
       Assert.assertEquals("USA", country.name);
   }
}
