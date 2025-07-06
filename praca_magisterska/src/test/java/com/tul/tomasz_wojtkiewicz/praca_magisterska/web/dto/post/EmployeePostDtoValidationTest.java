package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;// Testy jednostkowe dla EmployeePostDto (walidacja)

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
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
class EmployeePostDtoValidationTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post'

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

	@Test
		// cases: 1
	void validEmployeePostDto_shouldHaveNoViolations() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("test@example.com");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("123123123"); // MISTAKE: phone number should not accept +48
		dto.setAccessLevel(2);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}

	@Test
		// cases: 1
	void invalidEmail_shouldReturnViolation() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("invalid-email");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("+48123123123");
		dto.setAccessLevel(1);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
		// cases: 1
	void blankEmail_shouldReturnViolation() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("+48123123123");
		dto.setAccessLevel(1);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
		// cases: 1
	void nullFirstName_shouldReturnViolation() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("test@example.com");
		dto.setFirstName(null);
		dto.setLastName("Doe");
		dto.setPhoneNumber("+48123123123");
		dto.setAccessLevel(1);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
	}

	@Test
		// cases: 1
	void nullLastName_shouldReturnViolation() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("test@example.com");
		dto.setFirstName("John");
		dto.setLastName(null);
		dto.setPhoneNumber("+48123123123");
		dto.setAccessLevel(1);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
	}

	@Test
		// cases: 1
	void invalidPhoneNumber_shouldReturnViolation() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("test@example.com");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("invalid-phone");
		dto.setAccessLevel(1);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
	}

	@Test
		// cases: 1
	void nullPhoneNumber_shouldReturnViolation() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("test@example.com");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber(null);
		dto.setAccessLevel(1);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
	}

	@Test
		// cases: 2
	void accessLevelOutOfRange_shouldReturnViolation() {
		EmployeePostDto dtoLow = new EmployeePostDto();
		dtoLow.setEmail("test@example.com");
		dtoLow.setFirstName("John");
		dtoLow.setLastName("Doe");
		dtoLow.setPhoneNumber("+48123123123");
		dtoLow.setAccessLevel(-1);

		Set<ConstraintViolation<EmployeePostDto>> violationsLow = validator.validate(dtoLow);
		assertFalse(violationsLow.isEmpty());
		assertTrue(violationsLow.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accessLevel")));

		EmployeePostDto dtoHigh = new EmployeePostDto();
		dtoHigh.setEmail("test@example.com");
		dtoHigh.setFirstName("John");
		dtoHigh.setLastName("Doe");
		dtoHigh.setPhoneNumber("+48123123123");
		dtoHigh.setAccessLevel(5);

		Set<ConstraintViolation<EmployeePostDto>> violationsHigh = validator.validate(dtoHigh);
		assertFalse(violationsHigh.isEmpty());
		assertTrue(violationsHigh.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accessLevel")));
	}

	@Test
		// cases: 1
	void nullAccessLevel_shouldReturnViolation() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("test@example.com");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setPhoneNumber("+48123123123");
		dto.setAccessLevel(null);

		Set<ConstraintViolation<EmployeePostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accessLevel")));
	}
}
