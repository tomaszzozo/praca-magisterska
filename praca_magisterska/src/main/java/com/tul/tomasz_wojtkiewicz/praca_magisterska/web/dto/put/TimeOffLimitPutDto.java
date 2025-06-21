package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
public class TimeOffLimitPutDto {
    @Min(0)
	@NotNull
    private Long id;
    @Min(0)
	@NotNull
    private Integer maxHours;
    @Range(min = 2020, max = 2100)
	@NotNull
    private Integer year;
    @Min(1)
	@NotNull
    private Long typeId;
    @Min(1)
	@NotNull
    private Long employeeId;

	@AssertTrue
	public boolean isMaxHoursNotHigherThanHoursInYear() {
		return maxHours == null || year == null || maxHours <= LocalDate.of(year, 12, 31).getDayOfYear()*24;
	}
}
