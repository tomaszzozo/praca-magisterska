package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueYearAndTimeOffType", columnNames = {"leaveYear", "type_id", "employee_id"})})
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
    @JoinColumn(name = "type_id", nullable = false)
    private TimeOffTypeEntity timeOffType;
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;
    @OneToMany(mappedBy = "timeOffYearlyLimit")
    private List<TimeOffEntity> timeOffs;

    @AssertTrue
    public boolean maxHoursNotHigherThanHoursInYear() {
        return maxHours <= LocalDate.of(leaveYear, 12, 31).getDayOfYear();
    }
}
