package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueFirstDayAndEmployee", columnNames = {"firstDay", "employee_id"}), @UniqueConstraint(name = "UniqueLastDayAndEmployee", columnNames = {"lastDayInclusive", "employee_id"})})
public class TimeOffEntity {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private LocalDate firstDay;
    @NotNull
    private LocalDate lastDayInclusive;
    @Min(0)
    private int hoursCount;
    @NotNull
    private String comment;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "yearly_limit_id")
    private TimeOffTypeLimitPerYearAndEmployeeEntity timeOffYearlyLimit;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "type_id")
    private TimeOffTypeEntity timeOffType;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;

    @AssertFalse
    public boolean isFirstDayAfterLastDay() {
        return firstDay.isAfter(lastDayInclusive);
    }

    @AssertTrue
    public boolean isYearAndMonthEqual() {
        return firstDay.withDayOfMonth(1).equals(lastDayInclusive.withDayOfMonth(1));
    }

    @AssertTrue
    public boolean isHoursCountLessThanDaysInTimeOff() {
        return hoursCount <= firstDay.until(lastDayInclusive.plusDays(1), ChronoUnit.DAYS)*24;
    }

    @AssertTrue
    public boolean isYearInAcceptableRange() {
        return firstDay.getYear() >= 2020 && firstDay.getYear() <= 2100 && lastDayInclusive.getYear() >= 2020 && lastDayInclusive.getYear() <= 2100;
    }
}
