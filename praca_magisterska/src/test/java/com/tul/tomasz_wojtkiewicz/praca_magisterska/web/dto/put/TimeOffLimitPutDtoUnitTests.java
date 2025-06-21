package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestDtoFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dto")
@Tag("unit")
class TimeOffLimitPutDtoUnitTests {
	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursInYearMoreThanMax")
	void isMaxHoursNotHigherThanHoursInYearReturnsFalseWhenMaxHoursExceedsHoursInYear(int year, int invalidMaxHoursInYear) {
		assertFalse(TimeOffLimitTestDtoFactory.builder().year(year).maxHours(invalidMaxHoursInYear).build().asPutDto().isMaxHoursNotHigherThanHoursInYear());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#hoursInYearLessThanOrMax")
	void isMaxHoursNotHigherThanHoursInYearReturnsTrueWhenMaxHoursEqualsHoursInYear(int year, int validMaxHoursInYear) {
		assertTrue(TimeOffLimitTestDtoFactory.builder().year(year).maxHours(validMaxHoursInYear).build().asPutDto().isMaxHoursNotHigherThanHoursInYear());
	}
}
