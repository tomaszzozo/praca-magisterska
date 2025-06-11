package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.Objects;

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

    public static TimeOffLimitEntity getLimitEntity(TimeOffTypeEntity type, EmployeeEntity employee) {
        var result = new TimeOffLimitEntity();
        result.setLeaveYear(NOW.getYear());
        result.setMaxHours(160);
        result.setTimeOffType(type);
        result.setEmployee(employee);
        return result;
    }

    public static TimeOffEntity getTimeOffEntity(TimeOffLimitEntity limit) {
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

    public static TimeOffLimitPutDto getTimeOffLimitPutDto(long typeId, long employeeId) {
        var dto = new TimeOffLimitPutDto();
        dto.setYear(NOW.getYear());
        dto.setMaxHours(200);
        dto.setId(0);
        dto.setTypeId(typeId);
        dto.setEmployeeId(employeeId);
        return dto;
    }

    public static TimeOffPostDto getTimeOffPostDto(Long typeId, Long employeeId, Long limitId) {
        var dto = new TimeOffPostDto();
        dto.setComment("");
        dto.setFirstDay(NOW);
        dto.setLastDayInclusive(NOW);
        dto.setHoursCount(8);
        dto.setTypeId(1);
        dto.setEmployeeId(1);
        dto.setYearlyLimitId(Objects.requireNonNullElse(limitId, 1L));
        dto.setEmployeeId(Objects.requireNonNullElse(employeeId, 1L));
        dto.setTypeId(Objects.requireNonNullElse(typeId, 1L));
        return dto;
    }
}
