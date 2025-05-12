package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
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
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Email
    @NotBlank
    @NotNull
    @Column(unique = true)
    private String email;
    @Pattern(regexp = "^[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ][a-zA-Z '.,ęółśążźćńĘÓŁŚĄŻŹĆŃ-]+[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ.]$")
    @NotNull
    private String firstName;
    @NotNull
    @Pattern(regexp = "^[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ][a-zA-Z '.,ęółśążźćńĘÓŁŚĄŻŹĆŃ-]+[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ.]$")
    private String lastName;
    @Pattern(regexp = "^\\d{9}$")
    @NotNull
    @Column(unique = true, length = 9)
    private String phoneNumber;
    @Range(min = 0, max = 3)
    private int accessLevel;
    @OneToMany(mappedBy = "employee")
    @EqualsAndHashCode.Exclude
    private List<TimeOffLimitEntity> yearlyTimeOffLimits;
    @OneToMany(mappedBy = "employee")
    @EqualsAndHashCode.Exclude
    private List<TimeOffEntity> timeOffs;
}
