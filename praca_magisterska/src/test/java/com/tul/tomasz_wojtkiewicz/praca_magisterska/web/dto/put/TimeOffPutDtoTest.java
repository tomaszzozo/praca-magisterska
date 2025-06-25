package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
// ai tag: unit
class TimeOffPutDtoTest {

	private static Validator validator;

	@BeforeAll
	static void setupValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void shouldPassValidationWhenAllFieldsAreValid() {
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setFirstDay(LocalDate.of(2025, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 3));
		dto.setHoursCount(24);
		dto.setTypeId(1L);
		dto.setYearlyLimitId(1L);
		dto.setEmployeeId(1L);
		dto.setComment("Vacation");

		Set violations = validator.validate(dto);
		assertEquals(0, violations.size());
	}

	@Test
	void shouldFailValidationWhenFirstDayAfterLastDay() {
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setFirstDay(LocalDate.of(2025, 6, 5));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 1));
		dto.setHoursCount(8);
		dto.setTypeId(1L);
		dto.setYearlyLimitId(1L);
		dto.setEmployeeId(1L);
		dto.setComment("Invalid");

		var violations = validator.validate(dto);
		// INACCURACY: not sure which violations are actually present
		// assertEquals(1, violations.size()); MISTAKE: there were and always will be two violations
		assertEquals(2, violations.size());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstDayAfterLastDay")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hoursCountLessThanHoursInTimeOff")));
	}

	@Test
	void shouldFailValidationWhenYearAndMonthAreDifferent() {
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setFirstDay(LocalDate.of(2025, 6, 30));
		dto.setLastDayInclusive(LocalDate.of(2025, 7, 1));
		dto.setHoursCount(8);
		dto.setTypeId(1L);
		dto.setYearlyLimitId(1L);
		dto.setEmployeeId(1L);
		dto.setComment("Invalid");

		Set violations = validator.validate(dto);
		assertEquals(1, violations.size()); // INACCURACY: not sure which violations are actually present
	}

	@Test
	void shouldFailValidationWhenHoursCountExceedsAvailableTime() {
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setFirstDay(LocalDate.of(2025, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 2));
		dto.setHoursCount(100);
		dto.setTypeId(1L);
		dto.setYearlyLimitId(1L);
		dto.setEmployeeId(1L);
		dto.setComment("Too many hours");

		Set violations = validator.validate(dto);
		assertEquals(1, violations.size()); // INACCURACY: not sure which violations are actually present
	}

	@Test
	void shouldFailValidationWhenYearOutOfRange() {
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setFirstDay(LocalDate.of(2105, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2105, 6, 2));
		dto.setHoursCount(8);
		dto.setTypeId(1L);
		dto.setYearlyLimitId(1L);
		dto.setEmployeeId(1L);
		dto.setComment("Invalid year");

		Set violations = validator.validate(dto);
		assertEquals(1, violations.size()); // INACCURACY: not sure which violations are actually present
	}
}
