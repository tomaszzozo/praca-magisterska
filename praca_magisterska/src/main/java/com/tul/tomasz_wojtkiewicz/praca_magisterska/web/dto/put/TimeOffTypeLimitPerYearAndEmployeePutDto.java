package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@Getter
@Setter
public class TimeOffTypeLimitPerYearAndEmployeePutDto {
    @Min(0)
    private long id;
    @Min(0)
    private int maxHours;
    @Range(min = 2020, max = 2100)
    private int year;
    @Min(1)
    private long typeId;
    @Min(1)
    private long employeeId;

    @AssertFalse
    public boolean isMaxHoursHigherThanHoursInYear() {
        return maxHours > LocalDate.of(year, 12, 31).getDayOfYear()*24;
    }
}
