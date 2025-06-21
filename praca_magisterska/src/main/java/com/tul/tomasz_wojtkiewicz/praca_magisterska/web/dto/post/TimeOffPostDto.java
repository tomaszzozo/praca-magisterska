package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@EqualsAndHashCode
public class TimeOffPostDto {
    @NotNull
    private LocalDate firstDay;
    @NotNull
    private LocalDate lastDayInclusive;
    @Min(1)
	@NotNull
    private Integer hoursCount;
    @Min(1)
	@NotNull
    private Long typeId;
    @Min(1)
	@NotNull
    private Long yearlyLimitId;
    @Min(1)
	@NotNull
    private Long employeeId;
    @NotNull
    private String comment;

	@AssertFalse
	public boolean isFirstDayAfterLastDay() {
		if (firstDay == null || lastDayInclusive == null) {
			return false;
		}
		return firstDay.isAfter(lastDayInclusive);
	}

	@AssertTrue
	public boolean isYearAndMonthEqual() {
		return firstDay == null || lastDayInclusive == null || firstDay.withDayOfMonth(1).equals(lastDayInclusive.withDayOfMonth(1));
	}

	@AssertTrue
	public boolean isHoursCountLessThanHoursInTimeOff() {
		return hoursCount == null || firstDay == null || lastDayInclusive == null || hoursCount <= firstDay.until(lastDayInclusive.plusDays(1), ChronoUnit.DAYS)*24;
	}

	@AssertTrue
	public boolean isYearInAcceptableRange() {
		return firstDay == null || lastDayInclusive == null || firstDay.getYear() >= 2020 && firstDay.getYear() <= 2100 && lastDayInclusive.getYear() >= 2020 && lastDayInclusive.getYear() <= 2100;
	}
}
