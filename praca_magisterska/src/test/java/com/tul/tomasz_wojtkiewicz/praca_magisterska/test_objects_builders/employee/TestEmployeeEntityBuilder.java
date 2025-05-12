package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;

public class TestEmployeeEntityBuilder {
    public interface Defaults {
        String email = "bruceWayne@gmail.com";
        String firstName = "Bruce";
        String lastName = "Wayne";
        String phoneNumber = "123456789";
        int accessLevel = 3;
    }
    private final EmployeeEntity entity;

    public TestEmployeeEntityBuilder() {
        entity = new EmployeeEntity();
        entity.setFirstName(Defaults.firstName);
        entity.setLastName(Defaults.lastName);
        entity.setEmail(Defaults.email);
        entity.setAccessLevel(Defaults.accessLevel);
        entity.setPhoneNumber(Defaults.phoneNumber);
    }

    public TestEmployeeEntityBuilder withFirstName(String firstName) {
        entity.setFirstName(firstName);
        return this;
    }

    public TestEmployeeEntityBuilder withLastName(String lastName) {
        entity.setLastName(lastName);
        return this;
    }

    public TestEmployeeEntityBuilder withEmail(String email) {
        entity.setEmail(email);
        return this;
    }

    public TestEmployeeEntityBuilder withPhoneNumber(String phoneNumber) {
        entity.setPhoneNumber(phoneNumber);
        return this;
    }

    public TestEmployeeEntityBuilder withAccessLevel(int accessLevel) {
        entity.setAccessLevel(accessLevel);
        return this;
    }

    public EmployeeEntity build() {
        return entity;
    }
}
