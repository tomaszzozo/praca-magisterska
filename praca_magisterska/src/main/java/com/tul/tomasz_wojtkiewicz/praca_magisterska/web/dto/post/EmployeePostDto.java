package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name.Name;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer.PhoneNumber;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode
public class EmployeePostDto {
    @Email
    @NotBlank
    private String email;
    @NotNull
    @Name
    private String firstName;
    @NotNull
    @Name
    private String lastName;
    @NotNull
    @PhoneNumber
    private String phoneNumber;
    @Range(min = 0, max = 3)
    private int accessLevel;
}
