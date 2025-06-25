package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
// ai tag: unit
class EmployeePostDtoTest {

	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void shouldPassValidationWithCorrectData() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("john.doe@example.com");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("123456789"); // MISTAKE: +48123123123 +48 should not be accepted
		dto.setAccessLevel(2);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}

	@Test
	void shouldFailValidationWithInvalidEmail() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("invalid-email");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("+48123456789");
		dto.setAccessLevel(2);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}

	@Test
	void shouldFailValidationWithNullFields() {
		EmployeePostDto dto = new EmployeePostDto();

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertEquals(5, violations.size());
	}

	@Test
	void shouldFailValidationWithInvalidAccessLevel() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("john.doe@example.com");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("+48123456789");
		dto.setAccessLevel(5); // invalid

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}
}
