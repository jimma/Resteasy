package org.jboss.resteasy.test.providers.sse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.test.providers.sse.resource.SseSmokeUser;
@Provider
public class SseSmokeUserReader implements MessageBodyReader<SseSmokeUser>
{

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
        if (SseSmokeUser.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
   }

   public SseSmokeUser readFrom(Class<SseSmokeUser> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      BufferedReader br = null;
      try
      {
         br = new BufferedReader(new InputStreamReader(entityStream));
         String userInfo = br.readLine();
         String[] items = userInfo.split(";");
         return new SseSmokeUser(items[0], items[1]);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Unable to read Notification.", e);
      }
   }

}
