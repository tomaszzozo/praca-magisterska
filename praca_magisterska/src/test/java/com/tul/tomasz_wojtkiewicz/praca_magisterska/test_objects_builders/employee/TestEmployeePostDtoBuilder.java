package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import org.springframework.beans.BeanUtils;

public class TestEmployeePostDtoBuilder {
    private final EmployeePostDto dto;

    public TestEmployeePostDtoBuilder() {
        dto = new EmployeePostDto();
        BeanUtils.copyProperties(new TestEmployeeEntityBuilder().build(), dto);
    }

    public EmployeePostDto build() {
        return dto;
    }
}
