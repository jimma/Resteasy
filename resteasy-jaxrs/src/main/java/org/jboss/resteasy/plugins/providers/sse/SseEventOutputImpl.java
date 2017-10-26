package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponseWriter;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl;
import org.jboss.resteasy.core.interception.jaxrs.ResponseContainerRequestContext;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;


public class SseEventOutputImpl extends GenericType<OutboundSseEvent> implements SseEventSink
{
   private final MessageBodyWriter<OutboundSseEvent> writer;
   private final ResteasyAsynchronousContext asyncContext;
   private final HttpResponse response;
   private final HttpRequest request;
   private volatile boolean closed;
   private final Map<Class<?>, Object> contextDataMap;
   private boolean responseFlushed = false;
   
   public SseEventOutputImpl(final MessageBodyWriter<OutboundSseEvent> writer)
   {
      this.writer = writer; 
      contextDataMap = ResteasyProviderFactory.getContextDataMap();

      request = ResteasyProviderFactory.getContextData(org.jboss.resteasy.spi.HttpRequest.class);
      asyncContext = request.getAsyncContext();

      if (!asyncContext.isSuspended()) {
         try
         {
            asyncContext.suspend();
         }
         catch (IllegalStateException ex)
         {
            LogMessages.LOGGER.failedToSetRequestAsync();
         }
      }

      response =  ResteasyProviderFactory.getContextData(HttpResponse.class);
   }
   
   @Override
   public synchronized void close()
   {
      closed = true;
      if (asyncContext.isSuspended() && asyncContext.getAsyncResponse() != null) {
         if (asyncContext.isSuspended()) {
            //resume(null) will call into AbstractAsynchronousResponse.internalResume(Throwable exc)
            //The null is valid reference for Throwable:http://stackoverflow.com/questions/17576922/why-can-i-throw-null-in-java
            //Response header will be set with original one
            asyncContext.getAsyncResponse().resume(Response.noContent().build());
         }
      }
      
   }

   protected synchronized void flushResponseToClient()
   {
      if (!responseFlushed) {
         //set back to client 200 OK to implies the SseEventOutput is ready
         BuiltResponse jaxrsResponse = (BuiltResponse)Response.ok().type(MediaType.SERVER_SENT_EVENTS).build();
         try
         {
            ServerResponseWriter.writeNomapResponse(jaxrsResponse, request, response, ResteasyProviderFactory.getInstance(), true);
            response.getOutputStream().write(SseConstants.EOL);
            response.getOutputStream().write(SseConstants.EOL);
            response.flushBuffer();
            responseFlushed = true;
         }
         catch (IOException e)
         {
            close();
            throw new ProcessingException(Messages.MESSAGES.failedToCreateSseEventOutput(), e);
         }
      }
   }
   
   @Override
   public boolean isClosed()
   {
      return closed;
   }
   
   @Override
   public CompletionStage<?> send(OutboundSseEvent event)
   {
      return send(event, (a, b) -> {});
   }

   //We need this to make it async enough
   public CompletionStage<?> send(OutboundSseEvent event, BiConsumer<SseEventSink, Throwable> errorConsumer)
   {
      if (closed)
      {
         throw new IllegalStateException(Messages.MESSAGES.sseEventSinkIsClosed());
      }
      flushResponseToClient();
      try
      {
         writeEvent(event);
         
      }
      catch (Exception ex)
      {
         errorConsumer.accept(this, ex);
         return CompletableFuture.completedFuture(ex);
      }
      return CompletableFuture.completedFuture(event);
   }
   
 
   protected synchronized void writeEvent(OutboundSseEvent event) throws IOException
   {
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
      try
      {
         if (event != null)
         {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            writer.writeTo(event, event.getClass(), null, new Annotation[]{}, event.getMediaType(), null, bout);
            response.getOutputStream().write(bout.toByteArray());
            response.flushBuffer();
         }
      }
      catch (IOException e)
      {
         //The connection could be broken or closed. whenever IO error happens, mark closed to true to 
         //stop event writing 
         close();
         LogMessages.LOGGER.failedToWriteSseEvent(event.toString(), e);
         throw e;
      }
      catch (Exception e) {
         LogMessages.LOGGER.failedToWriteSseEvent(event.toString(), e);
         throw new ProcessingException(e);
      }
      finally {
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }
   
   
}
