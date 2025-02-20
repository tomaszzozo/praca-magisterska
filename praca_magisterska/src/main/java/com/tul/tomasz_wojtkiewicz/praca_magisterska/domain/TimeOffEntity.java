package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueFirstDayAndEmployee", columnNames = {"firstDay", "employee"}), @UniqueConstraint(name = "UniqueLastDayAndEmployee", columnNames = {"lastDayInclusive", "employee"})})
public class TimeOffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    private LocalDate firstDay;
    @Column(nullable = false, unique = true)
    private LocalDate lastDayInclusive;
    @Min(0)
    private int hoursCount;
    @ManyToOne
    @JoinColumn(name = "yearly_limit_id", nullable = false)
    private TimeOffTypeYearlyLimitPerEmployeeEntity timeOffYearlyLimit;
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private TimeOffTypeEntity timeOffType;
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

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
