package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.DelegatingOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

@Provider
@Produces("multipart/form-data")
public class ListEntityPartWriter implements AsyncMessageBodyWriter<List<EntityPart>> {
    protected static final byte[] DOUBLE_DASH_BYTES = "--".getBytes(StandardCharsets.US_ASCII);
    protected static final byte[] LINE_SEPARATOR_BYTES = "\r\n".getBytes(StandardCharsets.US_ASCII);
    protected String boundary = UUID.randomUUID().toString();

    @Context
    protected Providers providers;

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return List.class.isAssignableFrom(type);

    }

    public long getSize(List<EntityPart> list, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(List<EntityPart> list, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        httpHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, mediaType.toString() + "; boundary=" + this.boundary);
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.US_ASCII);

        for (EntityPart entityPart : list) {
            MultivaluedMap<String, Object> partHeaders = new MultivaluedMapImpl<String, Object>();
            partHeaders.putSingle("Content-Disposition", "form-data; name=\""
                    + entityPart.getName() + "\""
                    + (entityPart.getFileName().isPresent() ? "; filename=\"" + entityPart.getFileName().get() + "\""  : ""));
            writePart(entityStream, entityPart, boundaryBytes, partHeaders);
        }

        entityStream.write(boundaryBytes);
        entityStream.write(DOUBLE_DASH_BYTES);
    }

    protected void writePart(OutputStream entityStream, EntityPart part, byte[] boundaryBytes,
            MultivaluedMap<String, Object> headers) throws IOException {
        entityStream.write(boundaryBytes);
        entityStream.write(LINE_SEPARATOR_BYTES);
        part.getHeaders().forEach(headers::putSingle);
        headers.putSingle(HttpHeaderNames.CONTENT_TYPE, part.getMediaType().toString());

        Object entity = part.getContent();
        Class<?> entityType = part.getType();
        Type entityGenericType = part.getGenericType();
        MessageBodyWriter writer = providers.getMessageBodyWriter(entityType, entityGenericType, null, part.getMediaType());
        OutputStream partStream = new DelegatingOutputStream(entityStream) {
            @Override
            public void close() throws IOException {
            }
        };
        writer.writeTo(entity, entityType, entityGenericType, null, part.getMediaType(), headers,
                new HeaderFlushedOutputStream(headers, partStream));
        entityStream.write(LINE_SEPARATOR_BYTES);
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(List<EntityPart> entityParts, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
        httpHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, mediaType.toString() + "; boundary=" + this.boundary);
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.US_ASCII);

        return asyncWriteParts(entityParts, entityStream, boundaryBytes)
                .thenCompose(v -> entityStream.asyncWrite(boundaryBytes))
                .thenCompose(v -> entityStream.asyncWrite(DOUBLE_DASH_BYTES));
    }

    protected CompletionStage<Void> asyncWriteParts(List<EntityPart> list,
            AsyncOutputStream entityStream,
            byte[] boundaryBytes) {
        CompletionStage<Void> ret = CompletableFuture.completedFuture(null);
        for (EntityPart part : list) {
            MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
            ret = ret.thenCompose(v -> asyncWritePart(entityStream, boundaryBytes, part, headers));
        }
        return ret;
    }

    protected CompletionStage<Void> asyncWritePart(AsyncOutputStream entityStream, byte[] boundaryBytes, EntityPart part,
            MultivaluedMap<String, Object> headers) {
        part.getHeaders().forEach(headers::putSingle);
        headers.putSingle(HttpHeaderNames.CONTENT_TYPE, part.getMediaType().toString());

        Object entity = part.getContent();
        Class<?> entityType = part.getClass();
        Type entityGenericType = part.getClass();
        AsyncMessageBodyWriter writer = (AsyncMessageBodyWriter) providers
                .getMessageBodyWriter(entityType, entityGenericType, null, part.getMediaType());
        return entityStream.asyncWrite(boundaryBytes).thenCompose(v -> entityStream.asyncWrite(LINE_SEPARATOR_BYTES))
                .thenCompose(v -> writer.asyncWriteTo(entity, entityType, entityGenericType, null, part.getMediaType(), headers,
                        new HeaderFlushedAsyncOutputStream(headers, entityStream)))
                .thenCompose(v -> entityStream.asyncWrite(LINE_SEPARATOR_BYTES));
    }
}
