package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestDtoFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dto")
@Tag("unit")
class TimeOffPutDtoUnitTests {
	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#firstDayAfterLastDay")
		// cases: 4
	void isFirstDayAfterLastDayShouldReturnTrueWhenFirstDayAfterLastDay(LocalDate lastDay, LocalDate firstDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPutDto();
		assertTrue(entity.isFirstDayAfterLastDay());
	}

	@Test
		// cases: 3
	void isFirstDayAfterLastDayShouldReturnFalseWhenEitherValueIsNull() {
		var dto = TimeOffTestDtoFactory.builder().lastDayInclusive(null).firstDay(null).build().asPutDto();
		assertFalse(dto.isFirstDayAfterLastDay());
		dto.setFirstDay(LocalDate.now());
		assertFalse(dto.isFirstDayAfterLastDay());
		dto.setFirstDay(null);
		dto.setLastDayInclusive(LocalDate.now());
		assertFalse(dto.isFirstDayAfterLastDay());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#firstDayBeforeOrSameAsLastDay")
		// cases: 5
	void isFirstDayAfterLastDayShouldReturnFalseWhenFirstDayBeforeOrSameAsLastDay(LocalDate lastDay, LocalDate firstDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPutDto();
		assertFalse(entity.isFirstDayAfterLastDay());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#sameDatesDifferentDays")
		// cases: 8
	void isYearAndMonthEqualShouldReturnTrue(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPutDto();
		assertTrue(entity.isYearAndMonthEqual());
	}

	@Test
		// cases: 3
	void isYearAndMonthEqualShouldReturnTrueIfEitherValueIsNull() {
		var dto = TimeOffTestDtoFactory.builder().lastDayInclusive(null).firstDay(null).build().asPutDto();
		assertTrue(dto.isYearAndMonthEqual());
		dto.setFirstDay(LocalDate.now());
		assertTrue(dto.isYearAndMonthEqual());
		dto.setFirstDay(null);
		dto.setLastDayInclusive(LocalDate.now());
		assertTrue(dto.isYearAndMonthEqual());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#differentDates")
		// cases: 6
	void isYearAndMonthEqualShouldReturnFalse(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPutDto();
		assertFalse(entity.isYearAndMonthEqual());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursCountMoreThanHoursInTimeOff")
		// cases: 3
	void isHoursCountLessThanHoursInTimeOffShouldReturnFalse(int invalidHoursCount, LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(invalidHoursCount).build().asPutDto();
		assertFalse(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#nullableFirstAndLastDayAndHoursCount")
		// cases: 7
	void isHoursCountLessThanHoursInTimeOffShouldReturnFalseShouldReturnTrueIfEitherValueIsNull(LocalDate firstDay, LocalDate lastDay, Integer hoursCount) {
		var dto = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(hoursCount).build().asPutDto();
		assertTrue(dto.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#hoursCountLessOrEqualHoursInTimeOff")
		// cases: 4
	void isHoursCountLessThanHoursInTimeOffShouldReturnTrue(int validHoursCount, LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(validHoursCount).build().asPutDto();
		assertTrue(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#firstOrLastDayWithYearOutsideOfRange")
		// cases: 8
	void isYearInAcceptableRangeShouldReturnFalse(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPutDto();
		assertFalse(entity.isYearInAcceptableRange());
	}

	@Test
		// cases: 3
	void isYearInAcceptableRangeShouldReturnTrueIfEitherValueIsNull() {
		var dto = TimeOffTestDtoFactory.builder().lastDayInclusive(null).firstDay(null).build().asPutDto();
		assertTrue(dto.isYearInAcceptableRange());
		dto.setFirstDay(LocalDate.now());
		assertTrue(dto.isYearInAcceptableRange());
		dto.setFirstDay(null);
		dto.setLastDayInclusive(LocalDate.now());
		assertTrue(dto.isYearInAcceptableRange());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#firstAndLastDayWithYearInRange")
		// cases: 4
	void isYearInAcceptableRangeShouldReturnTrue(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPutDto();
		assertTrue(entity.isYearInAcceptableRange());
	}
}
