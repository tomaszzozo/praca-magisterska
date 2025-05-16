package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class TimeOffTypePutDto {
    @Min(0)
    private long id;
    @NotBlank
    private String name;
    private @Range(min = 0, max = 100) float compensationPercentage;
}
