package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffLimitService;

public class TestTimeOffLimitEntityBuilder {
    public interface Defaults {
        int leaveYear = 2025;
        int maxHours = TimeOffLimitEntity.DEFAULT_MAX_HOURS;
    }
    private final TimeOffLimitEntity entity;

    public TestTimeOffLimitEntityBuilder(EmployeeEntity employee, TimeOffTypeEntity type) {
        entity = new TimeOffLimitEntity();
        entity.setLeaveYear(Defaults.leaveYear);
        entity.setTimeOffType(type);
        entity.setMaxHours(Defaults.maxHours);
        entity.setEmployee(employee);
    }

    public TimeOffLimitEntity build() {
        return entity;
    }
}
