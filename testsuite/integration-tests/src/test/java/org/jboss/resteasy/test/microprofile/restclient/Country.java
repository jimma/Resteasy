package org.jboss.resteasy.test.microprofile.restclient;

public class Country {

    public String name;
    public String capital;
    public String currency;

    public Country() {
    }

    public Country(final String name, final String capital, final String currency) {
        this.name = name;
        this.capital = capital;
        this.currency = currency;
    }
}