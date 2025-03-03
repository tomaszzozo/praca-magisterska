package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeLimitPerYearAndEmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypeLimitPerYearAndEmployeePutDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@UtilityClass
public class DefaultTestObjects {
    public static final LocalDate NOW = LocalDate.now();

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
        result.setLeaveYear(NOW.getYear());
        result.setMaxHours(160);
        result.setTimeOffType(type);
        result.setEmployee(employee);
        return result;
    }

    public static TimeOffEntity getTimeOffEntity(TimeOffTypeLimitPerYearAndEmployeeEntity limit) {
        var result = new TimeOffEntity();
        result.setFirstDay(LocalDate.of(limit.getLeaveYear(), 6, 6));
        result.setLastDayInclusive(result.getFirstDay());
        result.setHoursCount(8);
        result.setEmployee(limit.getEmployee());
        result.setComment("");
        result.setTimeOffYearlyLimit(limit);
        result.setTimeOffType(limit.getTimeOffType());
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

    public static TimeOffTypePutDto getTimeOffTypePutDto() {
        var dto = new TimeOffTypePutDto();
        dto.setId(0);
        dto.setName("Time off type name");
        dto.setCompensationPercentage(0.8f);
        return dto;
    }

    public static TimeOffTypeLimitPerYearAndEmployeePutDto getTimOffLimitPutDto(long typeId, long employeeId) {
        var dto = new TimeOffTypeLimitPerYearAndEmployeePutDto();
        dto.setYear(NOW.getYear());
        dto.setMaxHours(200);
        dto.setId(0);
        dto.setTypeId(typeId);
        dto.setEmployeeId(employeeId);
        return dto;
    }
}
