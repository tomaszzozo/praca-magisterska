package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class TimeOffPostDto {
    @NotNull
    private LocalDate firstDay;
    @NotNull
    private LocalDate lastDayInclusive;
    @Min(0)
    private int hoursCount;
    @Min(1)
    private long typeId;
    @Min(1)
    private long yearlyLimitId;
    @Min(1)
    private long employeeId;
    @NotNull
    private String comment;

    @AssertFalse
    public boolean firstDayNotAfterLastDay() {
        return firstDay.isAfter(lastDayInclusive);
    }

    @AssertTrue
    public boolean datesHaveSameYearAndMonth() {
        return firstDay.withDayOfMonth(1).equals(lastDayInclusive.withDayOfMonth(1));
    }

    @AssertTrue
    public boolean hoursCountLessThanDaysInTimeOff() {
        return hoursCount <= firstDay.until(lastDayInclusive.plusDays(1), ChronoUnit.HOURS);
    }

}
