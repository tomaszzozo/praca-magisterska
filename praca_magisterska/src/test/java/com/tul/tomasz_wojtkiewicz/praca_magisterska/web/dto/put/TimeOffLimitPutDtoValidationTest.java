package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;// Testy jednostkowe dla TimeOffLimitPutDto

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
// ai tag: unit
class TimeOffLimitPutDtoValidationTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put'

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	@BeforeAll
	static void setup() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void tearDown() {
		validatorFactory.close();
	}

	private TimeOffLimitPutDto createValidDto() {
		TimeOffLimitPutDto dto = new TimeOffLimitPutDto();
		dto.setId(1L);
		dto.setMaxHours(100);
		dto.setYear(2025);
		dto.setTypeId(2L);
		dto.setEmployeeId(3L);
		return dto;
	}

	@Test
		// CASES: 1
	void validDto_shouldHaveNoViolations() {
		TimeOffLimitPutDto dto = createValidDto();
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}

	@Test
		// CASES: 5
	void nullFields_shouldReturnViolations() {
		TimeOffLimitPutDto dto = new TimeOffLimitPutDto();
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("maxHours")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("year")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("typeId")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeId")));
	}

	@Test
		// CASES: 1
	void negativeId_shouldReturnViolation() {
		TimeOffLimitPutDto dto = createValidDto();
		dto.setId(-1L);
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}

	@Test
		// CASES: 1
	void negativeMaxHours_shouldReturnViolation() {
		TimeOffLimitPutDto dto = createValidDto();
		dto.setMaxHours(-10);
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}

	@Test
		// CASES: 2
	void yearOutOfRange_shouldReturnViolation() {
		TimeOffLimitPutDto dto = createValidDto();
		dto.setYear(2019);
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());

		dto.setYear(2101);
		violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}

	@Test
		// CASES: 1
	void typeIdLessThanOne_shouldReturnViolation() {
		TimeOffLimitPutDto dto = createValidDto();
		dto.setTypeId(0L);
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}

	@Test
		// CASES: 1
	void employeeIdLessThanOne_shouldReturnViolation() {
		TimeOffLimitPutDto dto = createValidDto();
		dto.setEmployeeId(0L);
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}

	@Test
		// CASES: 1
	void maxHoursHigherThanHoursInYear_shouldFailAssertTrue() {
		TimeOffLimitPutDto dto = createValidDto();
		// 366 days in a leap year * 24 = 8784
		dto.setYear(2024);
		dto.setMaxHours(9000); // greater than hours in 2024
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("musi mieć wartość true"))); // MISTAKE: assuming english violation messages
	}

	@Test
		// CASES: 1
	void maxHoursEqualToHoursInYear_shouldPassAssertTrue() {
		TimeOffLimitPutDto dto = createValidDto();
		// 365 days in 2025 * 24 = 8760
		dto.setYear(2025);
		dto.setMaxHours(8760);
		Set<ConstraintViolation<TimeOffLimitPutDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}
}
