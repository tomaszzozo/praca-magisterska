package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class TimeOffTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @Range(min = 0, max = 100)
    @NotNull
    private Float compensationPercentage;

    @OneToMany(mappedBy = "timeOffType", cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    private List<TimeOffLimitEntity> yearlyLimits;

    @OneToMany(mappedBy = "timeOffType")
    @EqualsAndHashCode.Exclude
    private List<TimeOffEntity> timeOffs;
}
