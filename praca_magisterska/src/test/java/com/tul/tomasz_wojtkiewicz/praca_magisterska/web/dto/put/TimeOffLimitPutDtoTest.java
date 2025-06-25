package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
// ai tag: unit
class TimeOffLimitPutDtoTest {

	private static Validator validator;

	@BeforeAll
	static void setupValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
		// cases: 1
	void shouldPassValidationWhenAllFieldsAreValid() {
		TimeOffLimitPutDto dto = new TimeOffLimitPutDto();
		dto.setId(1L);
		dto.setMaxHours(2000);
		dto.setYear(2025);
		dto.setTypeId(1L);
		dto.setEmployeeId(1L);

		Set violations = validator.validate(dto);
		assertEquals(0, violations.size());
	}

	@Test
		// cases: 1
	void shouldFailValidationWhenFieldsAreInvalid() {
		TimeOffLimitPutDto dto = new TimeOffLimitPutDto();
		dto.setId(-1L); // invalid
		dto.setMaxHours(-100); // invalid
		dto.setYear(2199); // invalid
		dto.setTypeId(0L); // invalid
		dto.setEmployeeId(0L); // invalid

		Set violations = validator.validate(dto);
		assertEquals(5, violations.size()); // INACCURACY: not sure which violations are actually present
	}

	@Test
		// cases: 1
	void shouldFailCustomValidationWhenMaxHoursExceedYearLimit() {
		TimeOffLimitPutDto dto = new TimeOffLimitPutDto();
		dto.setId(1L);
		dto.setMaxHours(9000); // too high
		dto.setYear(2025);
		dto.setTypeId(1L);
		dto.setEmployeeId(1L);

		Set violations = validator.validate(dto);
		assertEquals(1, violations.size()); // INACCURACY: not sure which violations are actually present
	}

	@Test
		// cases: 1
	void shouldPassCustomValidationWhenMaxHoursAtLimitOfLeapYear() {
		TimeOffLimitPutDto dto = new TimeOffLimitPutDto();
		dto.setId(1L);
		dto.setMaxHours(366 * 24); // max valid hours in leap year
		dto.setYear(2024);
		dto.setTypeId(1L);
		dto.setEmployeeId(1L);

		Set violations = validator.validate(dto);
		assertEquals(0, violations.size());
	}
}
