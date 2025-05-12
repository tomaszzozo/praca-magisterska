package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_limit.TestTimeOffLimitEntityBuilder;

import java.time.LocalDate;

public class TestTimeOffEntityBuilder {
    public interface Defaults {
        LocalDate firstDay = LocalDate.of(TestTimeOffLimitEntityBuilder.Defaults.leaveYear, 3, 10);
        LocalDate lastDayInclusive = firstDay;
        int hoursCount = 8;
        String comment = "";
    }

    private final TimeOffEntity entity;

    public TestTimeOffEntityBuilder() {
        entity = new TimeOffEntity();
        entity.setTimeOffYearlyLimit(null);
        entity.setEmployee(null);
        entity.setTimeOffType(null);
        entity.setFirstDay(Defaults.firstDay);
        entity.setLastDayInclusive(Defaults.lastDayInclusive);
        entity.setHoursCount(Defaults.hoursCount);
        entity.setComment(Defaults.comment);
    }

    public TestTimeOffEntityBuilder withFirstDay(LocalDate firstDay) {
        entity.setFirstDay(firstDay);
        return this;
    }

    public TestTimeOffEntityBuilder withLastDay(LocalDate lastDay) {
        entity.setLastDayInclusive(lastDay);
        return this;
    }

    public TestTimeOffEntityBuilder withComment(String comment) {
        entity.setComment(comment);
        return this;
    }

    public TestTimeOffEntityBuilder withHoursCount(int hoursCount) {
        entity.setHoursCount(hoursCount);
        return this;
    }

    public TestTimeOffEntityBuilder withLimit(TimeOffLimitEntity limit) {
        entity.setTimeOffYearlyLimit(limit);
        return this;
    }

    public TestTimeOffEntityBuilder withEmployee(EmployeeEntity employee) {
        entity.setEmployee(employee);
        return this;
    }

    public TestTimeOffEntityBuilder withType(TimeOffTypeEntity type) {
        entity.setTimeOffType(type);
        return this;
    }

    public TimeOffEntity build() {
        return entity;
    }
}
