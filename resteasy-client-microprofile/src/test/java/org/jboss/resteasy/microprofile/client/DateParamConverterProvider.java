package org.jboss.resteasy.microprofile.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Objects;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class DateParamConverterProvider implements ParamConverterProvider {

    private final DateParamConverter converter = new DateParamConverter();

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (Objects.equals(rawType, LocalDate.class)) {
            return (ParamConverter<T>) converter;
        }
        return null;
    }

}