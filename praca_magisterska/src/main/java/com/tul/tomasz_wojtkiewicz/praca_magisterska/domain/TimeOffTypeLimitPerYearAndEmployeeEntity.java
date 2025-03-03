package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueYearAndTimeOffType", columnNames = {"leaveYear", "type_id", "employee_id"})})
@EqualsAndHashCode
public class TimeOffTypeLimitPerYearAndEmployeeEntity {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Min(0)
    private int maxHours;
    @Range(min = 2020, max = 2100)
    private int leaveYear;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "type_id")
    private TimeOffTypeEntity timeOffType;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;
    @OneToMany(mappedBy = "timeOffYearlyLimit")
    @EqualsAndHashCode.Exclude
    private List<TimeOffEntity> timeOffs;

    @AssertTrue
    public boolean isMaxHoursNotHigherThanHoursInYear() {
        return maxHours <= LocalDate.of(leaveYear, 12, 31).getDayOfYear()*24;
    }
}
