package org.jboss.resteasy.specimpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
public class EntityPartBuilderImpl implements EntityPart.Builder{
    private String part;
    private MediaType mediaType;
    private GenericType<?> genericType;


    private MultivaluedMap<String, String> headers = new MultivaluedMapImpl<String, String>();
    private Object entity;
    private Class<?> type;
    private String fileName;

    public EntityPartBuilderImpl(final String part) {
        this.part = part;
    }
    @Override
    public EntityPart.Builder mediaType(MediaType mediaType) throws IllegalArgumentException {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public EntityPart.Builder mediaType(String mediaTypeString) throws IllegalArgumentException {
        //TODO: implement this

        return this;
    }

    @Override
    public EntityPart.Builder header(String headerName, String... headerValues) throws IllegalArgumentException {
        headers.put(headerName, Arrays.asList(headerValues));
        return this;
    }

    @Override
    public EntityPart.Builder headers(MultivaluedMap<String, String> newHeaders) throws IllegalArgumentException {
        this.headers = newHeaders;
        return this;
    }

    @Override
    public EntityPart.Builder fileName(String fileName) throws IllegalArgumentException {
        this.fileName = fileName;
        return this;
    }

    @Override
    public EntityPart.Builder content(InputStream content) throws IllegalArgumentException {
        this.entity = content;
        this.type = content.getClass();
        return this;
    }

    @Override
    public <T> EntityPart.Builder content(T content, Class<? extends T> type) throws IllegalArgumentException {
        this.type = type;
        this.entity = content;
        return this;
    }

    @Override
    public <T> EntityPart.Builder content(T content, GenericType<T> type) throws IllegalArgumentException {
        this.entity = content;
        this.genericType = type;
        return this;
    }

    @Override
    public EntityPart build() throws IllegalStateException, IOException, WebApplicationException {
        return new EntityPartImpl(this.part,this.entity, this.type, this.genericType, this.mediaType, this.fileName,
                this.headers);
    }
}
