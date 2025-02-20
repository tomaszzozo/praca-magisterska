package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueYearAndTimeOffType", columnNames = {"year", "timeOffType"})})
public class TimeOffTypeCompensationForYearEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Range(min = 0, max = 100)
    private double compensationPercentage;
    @Range(min = 2020, max = 2100)
    private int year;
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private TimeOffTypeEntity timeOffType;
}
