package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeOffTypePostDto {
    @NotBlank
    @NotNull
    private String name;
}
