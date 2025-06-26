package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("entity")
class TimeOffTypeEntityIntegrationTests implements ConstraintValidation {
	@Test
		// cases: 1
	void basicValidEntityPassesValidation() {
		assertEquals(0, validateConstraints(TimeOffTypeTestEntityFactory.build().asEntity()).size());
	}

	@Test
		// cases: 1
	void nameCanNotBeNull() {
		var validation = validateConstraints(TimeOffTypeTestEntityFactory.builder().name(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("name", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void nameCanNotBeBlank() {
		var validation = validateConstraints(TimeOffTypeTestEntityFactory.builder().name("").build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("name", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void compensationPercentageCanNotBeNull() {
		var validation = validateConstraints(TimeOffTypeTestEntityFactory.builder().compensationPercentage(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("compensationPercentage", validation.iterator().next().getPropertyPath().toString());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#compensationPercentages")
		// cases: 6
	void entityWithInvalidCompensationPercentageDoesNotPassTheValidation(Float invalidCompensationPercentage) {
		var validation = validateConstraints(TimeOffTypeTestEntityFactory.builder().compensationPercentage(invalidCompensationPercentage).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("compensationPercentage", validation.iterator().next().getPropertyPath().toString());
	}
}
