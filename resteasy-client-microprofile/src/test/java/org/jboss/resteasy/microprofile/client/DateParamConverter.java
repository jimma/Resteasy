package org.jboss.resteasy.microprofile.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;

public class DateParamConverter implements ParamConverter<LocalDate> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public LocalDate fromString(String localDateString) {
        return DATE_FORMAT.parse(localDateString, LocalDate::from);
    }

    @Override
    public String toString(LocalDate localDate) {
        return DATE_FORMAT.format(localDate);
    }

}