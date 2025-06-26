package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestDtoFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dto")
@Tag("unit")
class TimeOffLimitPutDtoUnitTests {
	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursInYearMoreThanMax")
		// cases: 6
	void isMaxHoursNotHigherThanHoursInYearReturnsFalseWhenMaxHoursExceedsHoursInYear(int year, int invalidMaxHoursInYear) {
		assertFalse(TimeOffLimitTestDtoFactory.builder().year(year).maxHours(invalidMaxHoursInYear).build().asPutDto().isMaxHoursNotHigherThanHoursInYear());
	}

	@Test
		// cases: 3
	void isMaxHoursNotHigherThanHoursInYearReturnsTrueIfEitherValueIsNull() {
		var dto = TimeOffLimitTestDtoFactory.builder().maxHours(null).year(null).build().asPutDto();
		assertTrue(dto.isMaxHoursNotHigherThanHoursInYear());
		dto.setMaxHours(366*25);
		assertTrue(dto.isMaxHoursNotHigherThanHoursInYear());
		dto.setMaxHours(null);
		dto.setYear(2000);
		assertTrue(dto.isMaxHoursNotHigherThanHoursInYear());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#hoursInYearLessThanOrMax")
		// cases: 5
	void isMaxHoursNotHigherThanHoursInYearReturnsTrueWhenMaxHoursEqualsHoursInYear(int year, int validMaxHoursInYear) {
		assertTrue(TimeOffLimitTestDtoFactory.builder().year(year).maxHours(validMaxHoursInYear).build().asPutDto().isMaxHoursNotHigherThanHoursInYear());
	}
}
