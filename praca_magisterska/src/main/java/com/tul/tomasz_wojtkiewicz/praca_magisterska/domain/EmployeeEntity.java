package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name.Name;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer.PhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Setter
@Getter
@EqualsAndHashCode
@Entity
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @Name
    @NotNull
    private String firstName;

    @NotNull
    @Name
    private String lastName;

    @PhoneNumber
    @NotNull
    @Column(unique = true, length = 9)
    private String phoneNumber;

    @Range(min = 0, max = 3)
    @NotNull
    private Integer accessLevel;

    @OneToMany(mappedBy = "employee")
    @EqualsAndHashCode.Exclude
    private List<TimeOffLimitEntity> yearlyTimeOffLimits;

    @OneToMany(mappedBy = "employee")
    @EqualsAndHashCode.Exclude
    private List<TimeOffEntity> timeOffs;
}
