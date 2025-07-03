// Testy integracyjne walidacji beanów EmployeeEntity z użyciem Validator Spring Boot
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

@Tag("integration")
// ai tag: integration
@SpringBootTest
class EmployeeEntityValidationIntegrationTest {

	@Autowired
	private Validator validator;

	private EmployeeEntity validEmployee;

	@BeforeEach
	void setup() {
		validEmployee = new EmployeeEntity();
		validEmployee.setEmail("valid@example.com");
		validEmployee.setFirstName("John");
		validEmployee.setLastName("Doe");
		validEmployee.setPhoneNumber("123456789");
		validEmployee.setAccessLevel(2);
	}

	@Test
	void validEmployee_ShouldHaveNoViolations() {
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertTrue(violations.isEmpty());
	}

	@Test
	void emailBlank_ShouldFailValidation() {
		validEmployee.setEmail("");
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
	void emailInvalidFormat_ShouldFailValidation() {
		validEmployee.setEmail("invalid-email");
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
	void firstNameNull_ShouldFailValidation() {
		validEmployee.setFirstName(null);
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
	}

	@Test
	void lastNameNull_ShouldFailValidation() {
		validEmployee.setLastName(null);
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
	}

	@Test
	void phoneNumberNull_ShouldFailValidation() {
		validEmployee.setPhoneNumber(null);
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
	}

	@Test
	void phoneNumberWrongLength_ShouldFailValidation() {
		validEmployee.setPhoneNumber("1234");
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
	}

	@Test
	void accessLevelBelowMin_ShouldFailValidation() {
		validEmployee.setAccessLevel(-1);
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accessLevel")));
	}

	@Test
	void accessLevelAboveMax_ShouldFailValidation() {
		validEmployee.setAccessLevel(5);
		Set<ConstraintViolation<EmployeeEntity>> violations = validator.validate(validEmployee);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accessLevel")));
	}
}
