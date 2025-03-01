package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Entity
@Getter
@Setter
public class TimeOffTypeEntity {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;
    @Range(min = 0, max = 100)
    private double compensationPercentage;
    @OneToMany(mappedBy = "timeOffType", cascade = CascadeType.REMOVE)
    private List<TimeOffTypeLimitPerYearAndEmployeeEntity> yearlyLimits;
    @OneToMany(mappedBy = "timeOffType")
    private List<TimeOffEntity> timeOffs;
}
