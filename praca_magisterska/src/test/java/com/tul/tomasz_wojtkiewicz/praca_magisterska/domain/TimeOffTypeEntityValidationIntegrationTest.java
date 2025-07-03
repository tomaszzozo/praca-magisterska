// Testy integracyjne walidacji beanów TimeOffTypeEntity
package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
// ai tag: integration
class TimeOffTypeEntityValidationIntegrationTest {

	@Autowired
	private Validator validator;

	private TimeOffTypeEntity timeOffType;

	@BeforeEach
	void setup() {
		timeOffType = new TimeOffTypeEntity();
		timeOffType.setName("Sick Leave");
		timeOffType.setCompensationPercentage(50f);
	}

	@Test
	void validTimeOffTypeEntity_ShouldHaveNoViolations() {
		Set<ConstraintViolation<TimeOffTypeEntity>> violations = validator.validate(timeOffType);
		assertTrue(violations.isEmpty());
	}

	@Test
	void nullName_ShouldFailNotBlankValidation() {
		timeOffType.setName(null);
		Set<ConstraintViolation<TimeOffTypeEntity>> violations = validator.validate(timeOffType);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	@Test
	void blankName_ShouldFailNotBlankValidation() {
		timeOffType.setName(" ");
		Set<ConstraintViolation<TimeOffTypeEntity>> violations = validator.validate(timeOffType);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	@Test
	void nullCompensationPercentage_ShouldFailNotNullValidation() {
		timeOffType.setCompensationPercentage(null);
		Set<ConstraintViolation<TimeOffTypeEntity>> violations = validator.validate(timeOffType);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("compensationPercentage")));
	}

	@Test
	void compensationPercentageBelowRange_ShouldFailRangeValidation() {
		timeOffType.setCompensationPercentage(-1f);
		Set<ConstraintViolation<TimeOffTypeEntity>> violations = validator.validate(timeOffType);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("compensationPercentage")));
	}

	@Test
	void compensationPercentageAboveRange_ShouldFailRangeValidation() {
		timeOffType.setCompensationPercentage(101f);
		Set<ConstraintViolation<TimeOffTypeEntity>> violations = validator.validate(timeOffType);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("compensationPercentage")));
	}

	@Test
	void compensationPercentageAtLowerBound_ShouldPassValidation() {
		timeOffType.setCompensationPercentage(0f);
		Set<ConstraintViolation<TimeOffTypeEntity>> violations = validator.validate(timeOffType);
		assertTrue(violations.isEmpty());
	}

	@Test
	void compensationPercentageAtUpperBound_ShouldPassValidation() {
		timeOffType.setCompensationPercentage(100f);
		Set<ConstraintViolation<TimeOffTypeEntity>> violations = validator.validate(timeOffType);
		assertTrue(violations.isEmpty());
	}
}
