package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import org.springframework.beans.BeanUtils;

public class TestEmployeePutDtoBuilder {
    private final EmployeePutDto dto;

    public TestEmployeePutDtoBuilder() {
        dto = new EmployeePutDto();
        BeanUtils.copyProperties(new TestEmployeeEntityBuilder().build(), dto);
    }

    public TestEmployeePutDtoBuilder withEmail(String email) {
        dto.setEmail(email);
        return this;
    }

    public TestEmployeePutDtoBuilder withPhoneNumber(String phoneNumber) {
        dto.setPhoneNumber(phoneNumber);
        return this;
    }

    public EmployeePutDto build() {
        return dto;
    }

}
