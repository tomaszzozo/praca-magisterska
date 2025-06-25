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
class TimeOffTypePutDtoTest {

	private static Validator validator;

	@BeforeAll
	static void setupValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
		// cases: 1
	void shouldPassValidationWhenAllFieldsAreValid() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName("Vacation");
		dto.setCompensationPercentage(80.0f);

		Set violations = validator.validate(dto);
		assertEquals(0, violations.size());
	}

	@Test
		// cases: 1
	void shouldFailValidationWhenFieldsAreInvalid() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(-1L); // invalid
		dto.setName(""); // invalid
		dto.setCompensationPercentage(120.0f); // out of range

		Set violations = validator.validate(dto);
		assertEquals(3, violations.size()); // INACCURACY: not sure which violations are actually present
	}

	@Test
		// cases: 1
	void shouldFailValidationWhenCompensationPercentageIsNull() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName("Sick Leave");
		dto.setCompensationPercentage(null);

		Set violations = validator.validate(dto);
		assertEquals(1, violations.size()); // INACCURACY: not sure which violations are actually present
	}
}
