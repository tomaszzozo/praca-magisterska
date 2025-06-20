package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@Tag("entity")
class TimeOffLimitEntityUnitTests {
	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursInYearMoreThanMax")
	void isMaxHoursNotHigherThanHoursInYearReturnsFalseWhenMaxHoursExceedsHoursInYear(int year, int invalidMaxHoursInYear) {
		assertFalse(TimeOffLimitTestEntityFactory.builder().leaveYear(year).maxHours(invalidMaxHoursInYear).build().asEntity().isMaxHoursNotHigherThanHoursInYear());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#hoursInYearLessThanOrMax")
	void isMaxHoursNotHigherThanHoursInYearReturnsTrueWhenMaxHoursEqualsHoursInYear(int year, int validMaxHoursInYear) {
		assertTrue(TimeOffLimitTestEntityFactory.builder().leaveYear(year).maxHours(validMaxHoursInYear).build().asEntity().isMaxHoursNotHigherThanHoursInYear());
	}
}
