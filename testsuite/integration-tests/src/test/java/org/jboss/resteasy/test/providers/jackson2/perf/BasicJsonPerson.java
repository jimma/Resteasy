package org.jboss.resteasy.test.providers.jackson2.perf;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BasicJsonPerson.class, name = "basic"),
        @JsonSubTypes.Type(value = BasicJsonRunnerPerson.class, name = "runner")
})
public class BasicJsonPerson {
    String firstName;

    public BasicJsonPerson() {
    }

    public BasicJsonPerson(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

