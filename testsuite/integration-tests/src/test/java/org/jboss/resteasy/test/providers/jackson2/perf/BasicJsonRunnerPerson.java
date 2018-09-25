package org.jboss.resteasy.test.providers.jackson2.perf;

public class BasicJsonRunnerPerson extends BasicJsonPerson {

    String shoes;
    Integer distance;

    public BasicJsonRunnerPerson() {
    }

    public BasicJsonRunnerPerson(final String shoes, final Integer distance, final String firstName, final String lastName) {
        this.shoes = shoes;
        this.distance = distance;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getShoes() {
        return shoes;
    }

    public void setShoes(String shoes) {
        this.shoes = shoes;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}

