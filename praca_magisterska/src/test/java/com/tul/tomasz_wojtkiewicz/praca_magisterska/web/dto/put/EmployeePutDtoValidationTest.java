package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;// Testy jednostkowe dla EmployeePutDto (dziedziczenie bez dodatkowej logiki)

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
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
class EmployeePutDtoValidationTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put'

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

	private EmployeePutDto createValidDto() {
		EmployeePutDto dto = new EmployeePutDto();
		dto.setEmail("test@example.com");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("123123123"); // MISTAKE: +48 is not accepted
		dto.setAccessLevel(2);
		return dto;
	}

	@Test
		// CASES: 1
	void validDto_shouldHaveNoViolations() {
		EmployeePutDto dto = createValidDto();
		Set<ConstraintViolation<EmployeePutDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}

	@Test
		// CASES: 1
	void invalidEmail_shouldReturnViolation() {
		EmployeePutDto dto = createValidDto();
		dto.setEmail("invalid-email");
		Set<ConstraintViolation<EmployeePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
		// CASES: 1
	void nullFirstName_shouldReturnViolation() {
		EmployeePutDto dto = createValidDto();
		dto.setFirstName(null);
		Set<ConstraintViolation<EmployeePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
	}

	@Test
		// CASES: 1
	void nullLastName_shouldReturnViolation() {
		EmployeePutDto dto = createValidDto();
		dto.setLastName(null);
		Set<ConstraintViolation<EmployeePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
	}

	@Test
		// CASES: 1
	void nullPhoneNumber_shouldReturnViolation() {
		EmployeePutDto dto = createValidDto();
		dto.setPhoneNumber(null);
		Set<ConstraintViolation<EmployeePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
	}

	@Test
		// CASES: 2
	void accessLevelOutOfRange_shouldReturnViolation() {
		EmployeePutDto dto = createValidDto();
		dto.setAccessLevel(-1);
		Set<ConstraintViolation<EmployeePutDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());

		dto.setAccessLevel(4);
		violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}
}
