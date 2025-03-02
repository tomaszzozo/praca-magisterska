package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeLimitPerYearAndEmployeeEntity;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

@UtilityClass
public class DefaultTestEntities {
    private static final EmployeeEntity testEmployee;
    private static final TimeOffTypeEntity testTimeOffType;
    private static final TimeOffTypeLimitPerYearAndEmployeeEntity testTimeOffLimit;
    static {
        testEmployee = new EmployeeEntity();
        testEmployee.setAccessLevel(0);
        testEmployee.setFirstName("Tester");
        testEmployee.setLastName("Tester");
        testEmployee.setPhoneNumber("123456789");
        testEmployee.setEmail("tester@test.com");

        testTimeOffType = new TimeOffTypeEntity();
        testTimeOffType.setName("Test time off type");
        testTimeOffType.setCompensationPercentage(0.8f);

        testTimeOffLimit = new TimeOffTypeLimitPerYearAndEmployeeEntity();
        testTimeOffLimit.setLeaveYear(2025);
        testTimeOffLimit.setMaxHours(160);
    }

    public static EmployeeEntity getTestEmployee() {
        var result = new EmployeeEntity();
        BeanUtils.copyProperties(testEmployee, result);
        return result;
    }

    public static TimeOffTypeEntity getTestTimeOffType() {
        var result = new TimeOffTypeEntity();
        BeanUtils.copyProperties(testTimeOffType, result);
        return result;
    }

    public static TimeOffTypeLimitPerYearAndEmployeeEntity getTestTimeOffLimit(TimeOffTypeEntity type, EmployeeEntity employee) {
        var result = new TimeOffTypeLimitPerYearAndEmployeeEntity();
        BeanUtils.copyProperties(testTimeOffLimit, result);
        result.setTimeOffType(type);
        result.setEmployee(employee);
        return result;
    }
}
