package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class TimeOffTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;
    @OneToMany(mappedBy = "timeOffType")
    private List<TimeOffTypeYearlyLimitPerEmployeeEntity> yearlyLimits;
    @OneToMany(mappedBy = "timeOffType")
    private List<TimeOffEntity> timeOffs;
    @OneToMany(mappedBy = "timeOffType")
    private List<TimeOffTypeCompensationForYearEntity> yearlyCompensations;
}
