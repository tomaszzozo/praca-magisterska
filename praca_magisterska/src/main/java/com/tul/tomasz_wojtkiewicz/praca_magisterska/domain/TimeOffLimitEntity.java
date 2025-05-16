package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class TimeOffLimitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0)
    @NotNull
    private Integer maxHours = 0;

    @Range(min = 2020, max = 2100)
    @NotNull
    private Integer leaveYear;

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
        return maxHours == null || leaveYear == null || maxHours <= LocalDate.of(leaveYear, 12, 31).getDayOfYear()*24;
    }
}
