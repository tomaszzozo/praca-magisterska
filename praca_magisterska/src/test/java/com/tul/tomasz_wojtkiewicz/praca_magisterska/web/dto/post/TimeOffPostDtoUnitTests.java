package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

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
class TimeOffPostDtoUnitTests {
	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#firstDayAfterLastDay")
	void isFirstDayAfterLastDayShouldReturnTrueWhenFirstDayAfterLastDay(LocalDate lastDay, LocalDate firstDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPostDto();
		assertTrue(entity.isFirstDayAfterLastDay());
	}

	@Test
	void isFirstDayAfterLastDayShouldReturnFalseWhenEitherValueIsNull() {
		var dto = TimeOffTestDtoFactory.builder().lastDayInclusive(null).firstDay(null).build().asPostDto();
		assertFalse(dto.isFirstDayAfterLastDay());
		dto.setFirstDay(LocalDate.now());
		assertFalse(dto.isFirstDayAfterLastDay());
		dto.setFirstDay(null);
		dto.setLastDayInclusive(LocalDate.now());
		assertFalse(dto.isFirstDayAfterLastDay());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#firstDayBeforeOrSameAsLastDay")
	void isFirstDayAfterLastDayShouldReturnFalseWhenFirstDayBeforeOrSameAsLastDay(LocalDate lastDay, LocalDate firstDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPostDto();
		assertFalse(entity.isFirstDayAfterLastDay());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#sameDatesDifferentDays")
	void isYearAndMonthEqualShouldReturnTrue(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPostDto();
		assertTrue(entity.isYearAndMonthEqual());
	}

	@Test
	void isYearAndMonthEqualShouldReturnTrueIfEitherValueIsNull() {
		var dto = TimeOffTestDtoFactory.builder().lastDayInclusive(null).firstDay(null).build().asPostDto();
		assertTrue(dto.isYearAndMonthEqual());
		dto.setFirstDay(LocalDate.now());
		assertTrue(dto.isYearAndMonthEqual());
		dto.setFirstDay(null);
		dto.setLastDayInclusive(LocalDate.now());
		assertTrue(dto.isYearAndMonthEqual());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#differentDates")
	void isYearAndMonthEqualShouldReturnFalse(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPostDto();
		assertFalse(entity.isYearAndMonthEqual());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursCountMoreThanHoursInTimeOff")
	void isHoursCountLessThanHoursInTimeOffShouldReturnFalse(int invalidHoursCount, LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(invalidHoursCount).build().asPostDto();
		assertFalse(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#nullableFirstAndLastDayAndHoursCount")
	void isHoursCountLessThanHoursInTimeOffShouldReturnFalseShouldReturnTrueIfEitherValueIsNull(LocalDate firstDay, LocalDate lastDay, Integer hoursCount) {
		var dto = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(hoursCount).build().asPostDto();
		assertTrue(dto.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#hoursCountLessOrEqualHoursInTimeOff")
	void isHoursCountLessThanHoursInTimeOffShouldReturnTrue(int validHoursCount, LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(validHoursCount).build().asPostDto();
		assertTrue(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#firstOrLastDayWithYearOutsideOfRange")
	void isYearInAcceptableRangeShouldReturnFalse(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPostDto();
		assertFalse(entity.isYearInAcceptableRange());
	}

	@Test
	void isYearInAcceptableRangeShouldReturnTrueIfEitherValueIsNull() {
		var dto = TimeOffTestDtoFactory.builder().lastDayInclusive(null).firstDay(null).build().asPostDto();
		assertTrue(dto.isYearInAcceptableRange());
		dto.setFirstDay(LocalDate.now());
		assertTrue(dto.isYearInAcceptableRange());
		dto.setFirstDay(null);
		dto.setLastDayInclusive(LocalDate.now());
		assertTrue(dto.isYearInAcceptableRange());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#firstAndLastDayWithYearInRange")
	void isYearInAcceptableRangeShouldReturnTrue(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestDtoFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asPostDto();
		assertTrue(entity.isYearInAcceptableRange());
	}
}
