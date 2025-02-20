package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Setter
@Getter
@Entity
public class EmployeeEntity {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Email(regexp = "^.+@([a-zA-Z0-9.])+[a-zA-Z0-9]+$")
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z '.,-ęółśążźćń]{3,}$")
    @Column(nullable = false)
    private String firstName;
    @NotBlank
    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z '.,-ęółśążźćń]{3,}$")
    private String lastName;
    @NotBlank
    @Pattern(regexp = "^\\d{7,20}$")
    @Column(unique = true, nullable = false, length = 20)
    private String phoneNumber;
    @Range(min = 0, max = 3)
    private int accessLevel;
    @OneToMany(mappedBy = "employee")
    private List<TimeOffTypeYearlyLimitPerEmployeeEntity> yearlyTimeOffLimits;
    @OneToMany(mappedBy = "employee")
    private List<TimeOffEntity> timeOffs;
}
