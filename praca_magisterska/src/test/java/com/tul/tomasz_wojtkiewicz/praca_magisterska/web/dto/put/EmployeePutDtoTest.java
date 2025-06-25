package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("integration")
// ai tag: unit
class EmployeePutDtoTest {

	private static Validator validator;

	@BeforeAll
	static void setupValidatorInstance() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void shouldPassValidationWhenFieldsAreValid() {
		var dto = new EmployeePutDto();
		dto.setEmail("test@example.com");
		dto.setFirstName("Jan");
		dto.setLastName("Kowalski");
		dto.setPhoneNumber("123123123"); // MISTAKE: +48123123123 (+48) should not be accepted
		dto.setAccessLevel(1);

		var violations = validator.validate(dto);
		assertEquals(0, violations.size());
	}

	@Test
	void shouldFailValidationWhenFieldsAreInvalid() {
		var dto = new EmployeePutDto();
		dto.setEmail("invalid-email");
		dto.setFirstName(""); // invalid
		dto.setLastName(null); // invalid
		dto.setPhoneNumber("123"); // invalid
		dto.setAccessLevel(10); // out of range

		var violations = validator.validate(dto);
		assertEquals(5, violations.size()); // INACCURACY: not sure which violations are actually present
	}
}
