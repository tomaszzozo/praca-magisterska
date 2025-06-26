package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestDtoFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("dto")
@Tag("unit")
class TimeOffPostDtoIntegrationTests implements ConstraintValidation {
	@Test
		// cases: 1
	void basicValidEntityPassesValidation() {
		assertEquals(0, validateConstraints(TimeOffTestDtoFactory.build().asPostDto()).size());
	}

	@Test
		// cases: 1
	void firstDayCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().firstDay(null).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("firstDay", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void lastDayInclusiveCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().lastDayInclusive(null).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("lastDayInclusive", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void hoursCountCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().hoursCount(null).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("hoursCount", validation.iterator().next().getPropertyPath().toString());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
		// cases: 4
	void hoursCountCanNotBeNegativeOrZero(int invalidHoursCount) {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().hoursCount(invalidHoursCount).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("hoursCount", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void commentCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().comment(null).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("comment", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void timeOffYearlyLimitIdCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().yearlyLimitId(null).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("yearlyLimitId", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void timeOffTypeIdCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().typeId(null).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("typeId", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void employeeIdCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().employeeId(null).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("employeeId", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void firstDayAfterLastDayTriggersCustomValidationAssertion() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().firstDay(LocalDate.of(2025, 12, 31)).lastDayInclusive(LocalDate.of(2025, 12, 30)).build().asPostDto());
		assertFalse(validation.isEmpty());
		assertTrue(validation.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstDayAfterLastDay")));
	}

	@Test
		// cases: 1
	void monthNotEqualTriggersCustomValidationAssertion() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().firstDay(LocalDate.of(2025, 10, 10)).lastDayInclusive(LocalDate.of(2025, 11, 10)).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("yearAndMonthEqual", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void hoursCountMoreThanHoursInTimeOffTriggersCustomValidationAssertion() {
		var validation = validateConstraints(TimeOffTestDtoFactory.builder().hoursCount(Integer.MAX_VALUE).build().asPostDto());
		assertEquals(1, validation.size());
		assertEquals("hoursCountLessThanHoursInTimeOff", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void yearOutsideOfRangeTriggersCustomValidationAssertion() {
		var entity = TimeOffTestDtoFactory.build().asPostDto();
		entity.setFirstDay(entity.getFirstDay().minusYears(3000));
		entity.setLastDayInclusive(entity.getFirstDay());
		var validation = validateConstraints(entity);
		assertEquals(1, validation.size());
		assertEquals("yearInAcceptableRange", validation.iterator().next().getPropertyPath().toString());
	}
}
