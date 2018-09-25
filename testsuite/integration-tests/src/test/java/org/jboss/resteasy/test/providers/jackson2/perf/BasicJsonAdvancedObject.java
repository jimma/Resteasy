package org.jboss.resteasy.test.providers.jackson2.perf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BasicJsonAdvancedObject {
    Date timeStamp;
    BasicJsonPerson greetingFrom;
    BasicJsonPerson greetingTo;
    BasicJsonRunnerPerson runnerPerson;
    List<BasicJsonPerson> personList;
    String langCode;
    boolean official;
    String message;

    public BasicJsonAdvancedObject() {
        personList = new ArrayList<BasicJsonPerson>();
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setGreetingFrom(BasicJsonPerson greetingFrom) {
        this.greetingFrom = greetingFrom;
    }

    public void setGreetingTo(BasicJsonPerson greetingTo) {
        this.greetingTo = greetingTo;
    }

    public void setRunnerPerson(BasicJsonRunnerPerson runnerPerson) {
        this.runnerPerson = runnerPerson;
    }

    public void setPersonList(List<BasicJsonPerson> personList) {
        this.personList = personList;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public BasicJsonPerson getGreetingFrom() {
        return greetingFrom;
    }

    public BasicJsonPerson getGreetingTo() {
        return greetingTo;
    }

    public BasicJsonRunnerPerson getRunnerPerson() {
        return runnerPerson;
    }

    public List<BasicJsonPerson> getPersonList() {
        return personList;
    }

    public String getLangCode() {
        return langCode;
    }

    public boolean isOfficial() {
        return official;
    }

    public String getMessage() {
        return message;
    }

    public void add(BasicJsonPerson person) {
        personList.add(person);
    }

}
