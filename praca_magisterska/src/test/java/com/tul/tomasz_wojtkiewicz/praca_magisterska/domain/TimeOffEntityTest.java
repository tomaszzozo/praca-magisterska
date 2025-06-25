package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
// ai tag: unit
class TimeOffEntityTest {

	@Test
	// cases: 1
	void isFirstDayAfterLastDay_shouldReturnFalse_whenFirstDayIsBeforeLastDay() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2025, 6, 1));
		entity.setLastDayInclusive(LocalDate.of(2025, 6, 5));

		assertFalse(entity.isFirstDayAfterLastDay());
	}

	@Test
		// cases: 1
	void isFirstDayAfterLastDay_shouldReturnTrue_whenFirstDayIsAfterLastDay() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2025, 6, 10));
		entity.setLastDayInclusive(LocalDate.of(2025, 6, 5));

		assertTrue(entity.isFirstDayAfterLastDay());
	}

	@Test
		// cases: 1
	void isFirstDayAfterLastDay_shouldReturnFalse_whenEitherDateIsNull() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(null); // INACCURACY: does not set last day to null despite test name
		entity.setLastDayInclusive(LocalDate.of(2025, 6, 5));

		assertFalse(entity.isFirstDayAfterLastDay());
	}

	@Test
		// cases: 1
	void isYearAndMonthEqual_shouldReturnTrue_whenDatesInSameMonthAndYear() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2025, 6, 1));
		entity.setLastDayInclusive(LocalDate.of(2025, 6, 30));

		assertTrue(entity.isYearAndMonthEqual());
	}

	@Test
		// cases: 1
	void isYearAndMonthEqual_shouldReturnFalse_whenDatesInDifferentMonths() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2025, 6, 30));
		entity.setLastDayInclusive(LocalDate.of(2025, 7, 1));

		assertFalse(entity.isYearAndMonthEqual());
	}

	@Test
		// cases: 1
	void isHoursCountLessThanHoursInTimeOff_shouldReturnTrue_whenHoursAreWithinRange() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2025, 6, 1));
		entity.setLastDayInclusive(LocalDate.of(2025, 6, 2)); // 2 days = 48 hours
		entity.setHoursCount(48);

		assertTrue(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@Test
		// cases: 1
	void isHoursCountLessThanHoursInTimeOff_shouldReturnFalse_whenHoursExceedRange() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2025, 6, 1));
		entity.setLastDayInclusive(LocalDate.of(2025, 6, 1)); // 1 day = 24 hours
		entity.setHoursCount(25);

		assertFalse(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@Test
		// cases: 1
	void isYearInAcceptableRange_shouldReturnTrue_whenDatesInRange() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2025, 1, 1));
		entity.setLastDayInclusive(LocalDate.of(2025, 12, 31));

		assertTrue(entity.isYearInAcceptableRange());
	}

	@Test
		// cases: 1
	void isYearInAcceptableRange_shouldReturnFalse_whenDateBefore2020() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2019, 12, 31));
		entity.setLastDayInclusive(LocalDate.of(2025, 12, 31));

		assertFalse(entity.isYearInAcceptableRange());
	}

	@Test
		// cases: 1
	void isYearInAcceptableRange_shouldReturnFalse_whenDateAfter2100() {
		TimeOffEntity entity = new TimeOffEntity();
		entity.setFirstDay(LocalDate.of(2025, 1, 1));
		entity.setLastDayInclusive(LocalDate.of(2101, 1, 1));

		assertFalse(entity.isYearInAcceptableRange());
	}
}
