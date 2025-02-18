package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@MappedSuperclass
public class EmployeePostDto {
    @Email(regexp = "^.+@([a-zA-Z0-9.])+[a-zA-Z0-9]+$")
    @NotBlank
    @NotNull
    private String email;
    @NotBlank
    @NotNull
    @Pattern(regexp = "^[a-zA-Z '.,-ęółśążźćń]{3,}$")
    private String firstName;
    @NotBlank
    @NotNull
    @Pattern(regexp = "^[a-zA-Z '.,-ęółśążźćń]{3,}$")
    private String lastName;
    @NotBlank
    @Pattern(regexp = "^\\d{7,20}$")
    private String phoneNumber;
    @Range(min = 0, max = 3)
    private int accessLevel;
}
