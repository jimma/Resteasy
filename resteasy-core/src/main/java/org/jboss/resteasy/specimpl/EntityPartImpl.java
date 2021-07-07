package org.jboss.resteasy.specimpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Providers;

public class EntityPartImpl implements EntityPart {
    protected static final Annotation[] empty = {};
    private MultivaluedMap<String, String> headers = new MultivaluedMapImpl<String, String>();
    private Object entity;
    private Class<?> type;
    private MediaType mediaType;
    private GenericType<?> genericType;
    private String filename;
    private String name;
    private Providers providers;
    public EntityPartImpl(final String name, final Object entity, final Class<?> type,
            final GenericType<?> genericType, final MediaType mediaType,
            final String filename, final MultivaluedMap<String, String> headers) {
        this.name = name;
        this.entity = entity;
        this.type = type;
        this.mediaType = mediaType;
        this.filename = filename;
        this.genericType = genericType;
        this.headers = headers;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Optional<String> getFileName() {
        return Optional.ofNullable(this.filename);
    }

    @Override
    public Object getContent() {
        return this.entity;
    }

    @Override
    public <T> T getContent(Class<T> type)
            throws IllegalArgumentException, IllegalStateException, IOException, WebApplicationException {
        return (T) this.getContent(type, null, this.mediaType);
    }

    @Override
    public <T> T getContent(GenericType<T> type)
            throws IllegalArgumentException, IllegalStateException, IOException, WebApplicationException {
        return (T) this.getContent(type.getRawType(), type.getType(), this.mediaType);
    }

    protected <T> T getContent(Class<T> type, Type genericType, MediaType contentType)
            throws IllegalArgumentException, IllegalStateException, IOException, WebApplicationException {
        MessageBodyReader<T> reader = providers.getMessageBodyReader(type, genericType, empty, contentType);
        if (reader == null)
        {
            throw new RuntimeException("Unable find MessageBodyReader");
            //throw new RuntimeException(Messages.MESSAGES.unableToFindMessageBodyReader(contentType, type.getName()));
        }

        LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());

        return reader.readFrom(type, genericType, empty, contentType, headers, (InputStream) this.entity);


    }

    @Override
    public MultivaluedMap<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    @Override
    public Type getGenericType() {
        if (genericType != null) {
            return genericType.getType();
        }
        return null;
    }
}
