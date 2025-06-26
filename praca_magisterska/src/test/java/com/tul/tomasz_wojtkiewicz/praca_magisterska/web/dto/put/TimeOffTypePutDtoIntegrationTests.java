package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypePutTestDtoFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("dto")
@Tag("integration")
class TimeOffTypePutDtoIntegrationTests implements ConstraintValidation {
	@Test
		// cases: 1
	void basicValidEntityPassesValidation() {
		assertEquals(0, validateConstraints(TimeOffTypePutTestDtoFactory.build().asDto()).size());
	}

	@Test
		// cases: 1
	void nameCanNotBeNull() {
		var validation = validateConstraints(TimeOffTypePutTestDtoFactory.builder().name(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("name", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void nameCanNotBeBlank() {
		var validation = validateConstraints(TimeOffTypePutTestDtoFactory.builder().name("").build().asDto());
		assertEquals(1, validation.size());
		assertEquals("name", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void compensationPercentageCanNotBeNull() {
		var validation = validateConstraints(TimeOffTypePutTestDtoFactory.builder().compensationPercentage(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("compensationPercentage", validation.iterator().next().getPropertyPath().toString());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#compensationPercentages")
		// cases: 6
	void entityWithInvalidCompensationPercentageDoesNotPassTheValidation(Float invalidCompensationPercentage) {
		var validation = validateConstraints(TimeOffTypePutTestDtoFactory.builder().compensationPercentage(invalidCompensationPercentage).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("compensationPercentage", validation.iterator().next().getPropertyPath().toString());
	}

}
