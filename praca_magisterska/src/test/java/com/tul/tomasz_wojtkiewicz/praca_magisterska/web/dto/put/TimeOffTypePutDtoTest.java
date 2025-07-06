package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// INACCURACY: class should be package-private
@Tag("integration")
// ai tag: unit
public class TimeOffTypePutDtoTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put'

	private static Validator validator;

	@BeforeAll
	static void setupValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
		// cases: 1
	void testValidDto() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName("Vacation");
		dto.setCompensationPercentage(50.0f);

		Set<ConstraintViolation<TimeOffTypePutDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}

	@Test
		// cases: 1
	void testIdNegativeValue() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(-1L);
		dto.setName("Vacation");
		dto.setCompensationPercentage(50.0f);

		Set<ConstraintViolation<TimeOffTypePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
	}

	@Test
		// cases: 1
	void testNameBlank() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName(" ");
		dto.setCompensationPercentage(50.0f);

		Set<ConstraintViolation<TimeOffTypePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	@Test
		// cases: 1
	void testNameNull() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName(null);
		dto.setCompensationPercentage(50.0f);

		Set<ConstraintViolation<TimeOffTypePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	@Test
		// cases: 1
	void testCompensationPercentageNull() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName("Vacation");
		dto.setCompensationPercentage(null);

		Set<ConstraintViolation<TimeOffTypePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("compensationPercentage")));
	}

	@Test
		// cases: 1
	void testCompensationPercentageBelowMin() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName("Vacation");
		dto.setCompensationPercentage(-1.0f);

		Set<ConstraintViolation<TimeOffTypePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("compensationPercentage")));
	}

	@Test
		// cases: 1
	void testCompensationPercentageAboveMax() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName("Vacation");
		dto.setCompensationPercentage(150.0f);

		Set<ConstraintViolation<TimeOffTypePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("compensationPercentage")));
	}
}
