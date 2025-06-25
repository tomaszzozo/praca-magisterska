package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
// ai tag: unit
class TimeOffLimitEntityTest {

	@Test
		// cases: 1
	void isMaxHoursNotHigherThanHoursInYear_shouldReturnTrue_whenMaxHoursIsNull() {
		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setMaxHours(null);
		entity.setLeaveYear(2025);

		assertTrue(entity.isMaxHoursNotHigherThanHoursInYear());
	}

	@Test
		// cases: 1
	void isMaxHoursNotHigherThanHoursInYear_shouldReturnTrue_whenLeaveYearIsNull() {
		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setMaxHours(1000);
		entity.setLeaveYear(null);

		assertTrue(entity.isMaxHoursNotHigherThanHoursInYear());
	}

	@Test
		// cases: 1
	void isMaxHoursNotHigherThanHoursInYear_shouldReturnTrue_whenMaxHoursIsLessThanOrEqualToHoursInYear() {
		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setLeaveYear(2024); // 2024 is a leap year, 366 days
		entity.setMaxHours(366 * 24);

		assertTrue(entity.isMaxHoursNotHigherThanHoursInYear());
	}

	@Test
		// cases: 1
	void isMaxHoursNotHigherThanHoursInYear_shouldReturnFalse_whenMaxHoursIsGreaterThanHoursInYear() {
		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setLeaveYear(2023); // 365 days
		entity.setMaxHours(365 * 24 + 1);

		assertFalse(entity.isMaxHoursNotHigherThanHoursInYear());
	}
}
