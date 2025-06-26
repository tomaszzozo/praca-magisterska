package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestDtoFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("dto")
@Tag("integration")
class TimeOffLimitPutDtoIntegrationTests  implements ConstraintValidation {
	@Test
	void basicValidEntityPassesValidation() {
		assertEquals(0, validateConstraints(TimeOffLimitTestDtoFactory.build().asPutDto()).size());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursInYearNegative")
		// cases: 3
	void maxHoursCanNotBeNegative(int invalidMaxHours) {
		var validation = validateConstraints(TimeOffLimitTestDtoFactory.builder().maxHours(invalidMaxHours).build().asPutDto());
		assertEquals(1, validation.size());
		assertEquals("maxHours", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void maxHoursCanNotBeNull() {
		var validation = validateConstraints(TimeOffLimitTestDtoFactory.builder().maxHours(null).build().asPutDto());
		assertEquals(1, validation.size());
		assertEquals("maxHours", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void defaultMaxHoursIsZero() {
		assertEquals(0, new TimeOffLimitEntity().getMaxHours());
	}

	@Test
		// cases: 1
	void maxHoursMoreThanHoursInYearTriggersCustomValidationAssertion() {
		var validation = validateConstraints(TimeOffLimitTestDtoFactory.builder().year(2025).maxHours(LocalDate.of(2025, 12, 31).getDayOfYear() * 24 + 1).build().asPutDto());
		assertEquals(1, validation.size());
		assertEquals("maxHoursNotHigherThanHoursInYear", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void yearCanNotBeNull() {
		var validation = validateConstraints(TimeOffLimitTestDtoFactory.builder().year(null).build().asPutDto());
		assertEquals(1, validation.size());
		assertEquals("year", validation.iterator().next().getPropertyPath().toString());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
		// cases: 8
	void yearMustBeInRange(int invalidLeaveYear) {
		var validation = validateConstraints(TimeOffLimitTestDtoFactory.builder().year(invalidLeaveYear).build().asPutDto());
		assertEquals(1, validation.size());
		assertEquals("year", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void employeeMustNotBeNull() {
		var validation = validateConstraints(TimeOffLimitTestDtoFactory.builder().employeeId(null).build().asPutDto());
		assertEquals(1, validation.size());
		assertEquals("employeeId", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void timeOffTypeMustNotBeNull() {
		var validation = validateConstraints(TimeOffLimitTestDtoFactory.builder().typeId(null).build().asPutDto());
		assertEquals(1, validation.size());
		assertEquals("typeId", validation.iterator().next().getPropertyPath().toString());
	}
}
