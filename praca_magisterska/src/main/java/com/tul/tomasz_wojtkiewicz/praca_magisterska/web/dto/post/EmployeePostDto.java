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
    @Email
    @NotBlank
    @NotNull
    private String email;
    @NotNull
    @Pattern(regexp = "^[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ][a-zA-Z '.,ęółśążźćńĘÓŁŚĄŻŹĆŃ-]+[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ.]$")
    private String firstName;
    @NotNull
    @Pattern(regexp = "^[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ][a-zA-Z '.,ęółśążźćńĘÓŁŚĄŻŹĆŃ-]+[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ.]$")
    private String lastName;
    @NotNull
    @Pattern(regexp = "^\\d{9}$")
    private String phoneNumber;
    @Range(min = 0, max = 3)
    private int accessLevel;
}
