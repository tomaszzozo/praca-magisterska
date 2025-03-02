package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeLimitPerYearAndEmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

@UtilityClass
public class DefaultTestObjects {
    public static EmployeeEntity getEmployeeEntity() {
        var result = new EmployeeEntity();
        result.setAccessLevel(0);
        result.setFirstName("Tester");
        result.setLastName("Tester");
        result.setPhoneNumber("123456789");
        result.setEmail("tester@test.com");
        return result;
    }

    public static TimeOffTypeEntity getTimeOffTypeEntity() {
        var result = new TimeOffTypeEntity();
        result.setName("Test time off type");
        result.setCompensationPercentage(0.8f);
        return result;
    }

    public static TimeOffTypeLimitPerYearAndEmployeeEntity getLimitEntity(TimeOffTypeEntity type, EmployeeEntity employee) {
        var result = new TimeOffTypeLimitPerYearAndEmployeeEntity();
        result.setLeaveYear(2025);
        result.setMaxHours(160);
        result.setTimeOffType(type);
        result.setEmployee(employee);
        return result;
    }

    public static EmployeePostDto getEmployeePostDto() {
        var dto = new EmployeePostDto();
        dto.setFirstName("Name");
        dto.setLastName("Name");
        dto.setAccessLevel(0);
        dto.setEmail("email@email.com");
        dto.setPhoneNumber("123456789");
        return dto;
    }

    public static EmployeePutDto getEmployeePutDto() {
        var dto = new EmployeePutDto();
        BeanUtils.copyProperties(getEmployeePostDto(), dto);
        return dto;
    }
}
