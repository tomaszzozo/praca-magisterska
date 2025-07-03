// Testy jednostkowe dla klasy TimeOffEntity (sprawdzające metody walidacyjne i gettery/settery)
package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
// ai tag: unit
class TimeOffEntityUnitTest {

	@Test
	void testGettersAndSetters() {
		TimeOffEntity timeOff = new TimeOffEntity();

		timeOff.setId(1L);
		LocalDate first = LocalDate.of(2023, 5, 1);
		LocalDate last = LocalDate.of(2023, 5, 3);
		timeOff.setFirstDay(first);
		timeOff.setLastDayInclusive(last);
		timeOff.setHoursCount(16);
		timeOff.setComment("Vacation");
		TimeOffLimitEntity limit = new TimeOffLimitEntity();
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		EmployeeEntity employee = new EmployeeEntity();
		timeOff.setTimeOffYearlyLimit(limit);
		timeOff.setTimeOffType(type);
		timeOff.setEmployee(employee);

		assertEquals(1L, timeOff.getId());
		assertEquals(first, timeOff.getFirstDay());
		assertEquals(last, timeOff.getLastDayInclusive());
		assertEquals(16, timeOff.getHoursCount());
		assertEquals("Vacation", timeOff.getComment());
		assertSame(limit, timeOff.getTimeOffYearlyLimit());
		assertSame(type, timeOff.getTimeOffType());
		assertSame(employee, timeOff.getEmployee());
	}

	@Test
	void isFirstDayAfterLastDay_whenFirstDayAfterLastDay_returnsTrue() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2023, 5, 5));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 5, 4));
		assertTrue(timeOff.isFirstDayAfterLastDay());
	}

	@Test
	void isFirstDayAfterLastDay_whenFirstDayBeforeOrEqualLastDay_returnsFalse() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2023, 5, 1));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 5, 5));
		assertFalse(timeOff.isFirstDayAfterLastDay());

		timeOff.setFirstDay(LocalDate.of(2023, 5, 5));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 5, 5));
		assertFalse(timeOff.isFirstDayAfterLastDay());
	}

	@Test
	void isFirstDayAfterLastDay_whenNullDates_returnsFalse() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(null);
		timeOff.setLastDayInclusive(null);
		assertFalse(timeOff.isFirstDayAfterLastDay());

		timeOff.setFirstDay(LocalDate.now());
		assertFalse(timeOff.isFirstDayAfterLastDay());
		timeOff.setFirstDay(null);
		timeOff.setLastDayInclusive(LocalDate.now());
		assertFalse(timeOff.isFirstDayAfterLastDay());
	}

	@Test
	void isYearAndMonthEqual_whenSameMonthAndYear_returnsTrue() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2023, 5, 1));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 5, 31));
		assertTrue(timeOff.isYearAndMonthEqual());
	}

	@Test
	void isYearAndMonthEqual_whenDifferentMonthOrYear_returnsFalse() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2023, 5, 31));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 6, 1));
		assertFalse(timeOff.isYearAndMonthEqual());

		timeOff.setFirstDay(LocalDate.of(2023, 12, 31));
		timeOff.setLastDayInclusive(LocalDate.of(2024, 12, 31));
		assertFalse(timeOff.isYearAndMonthEqual());
	}

	@Test
	void isYearAndMonthEqual_whenNullDates_returnsTrue() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(null);
		timeOff.setLastDayInclusive(null);
		assertTrue(timeOff.isYearAndMonthEqual());

		timeOff.setFirstDay(LocalDate.now());
		timeOff.setLastDayInclusive(null);
		assertTrue(timeOff.isYearAndMonthEqual());

		timeOff.setFirstDay(null);
		timeOff.setLastDayInclusive(LocalDate.now());
		assertTrue(timeOff.isYearAndMonthEqual());
	}

	@Test
	void isHoursCountLessThanHoursInTimeOff_whenValid_returnsTrue() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2023, 5, 1));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 5, 3)); // 3 days
		timeOff.setHoursCount(72); // 3 days * 24 hours = 72
		assertTrue(timeOff.isHoursCountLessThanHoursInTimeOff());

		timeOff.setHoursCount(50);
		assertTrue(timeOff.isHoursCountLessThanHoursInTimeOff());
	}

	@Test
	void isHoursCountLessThanHoursInTimeOff_whenTooLarge_returnsFalse() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2023, 5, 1));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 5, 3)); // 3 days
		timeOff.setHoursCount(73); // > 72
		assertFalse(timeOff.isHoursCountLessThanHoursInTimeOff());
	}

	@Test
	void isHoursCountLessThanHoursInTimeOff_whenNullFields_returnsTrue() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setHoursCount(null);
		assertTrue(timeOff.isHoursCountLessThanHoursInTimeOff());

		timeOff.setHoursCount(10);
		timeOff.setFirstDay(null);
		timeOff.setLastDayInclusive(LocalDate.now());
		assertTrue(timeOff.isHoursCountLessThanHoursInTimeOff());

		timeOff.setFirstDay(LocalDate.now());
		timeOff.setLastDayInclusive(null);
		assertTrue(timeOff.isHoursCountLessThanHoursInTimeOff());
	}

	@Test
	void isYearInAcceptableRange_whenWithinRange_returnsTrue() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2020, 1, 1));
		timeOff.setLastDayInclusive(LocalDate.of(2100, 12, 31));
		assertTrue(timeOff.isYearInAcceptableRange());

		timeOff.setFirstDay(LocalDate.of(2021, 5, 5));
		timeOff.setLastDayInclusive(LocalDate.of(2021, 5, 6));
		assertTrue(timeOff.isYearInAcceptableRange());
	}

	@Test
	void isYearInAcceptableRange_whenOutOfRange_returnsFalse() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2019, 12, 31));
		timeOff.setLastDayInclusive(LocalDate.of(2020, 1, 1));
		assertFalse(timeOff.isYearInAcceptableRange());

		timeOff.setFirstDay(LocalDate.of(2020, 1, 1));
		timeOff.setLastDayInclusive(LocalDate.of(2101, 1, 1));
		assertFalse(timeOff.isYearInAcceptableRange());
	}

	@Test
	void isYearInAcceptableRange_whenNullDates_returnsTrue() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(null);
		timeOff.setLastDayInclusive(null);
		assertTrue(timeOff.isYearInAcceptableRange());

		timeOff.setFirstDay(LocalDate.now());
		timeOff.setLastDayInclusive(null);
		assertTrue(timeOff.isYearInAcceptableRange());

		timeOff.setFirstDay(null);
		timeOff.setLastDayInclusive(LocalDate.now());
		assertTrue(timeOff.isYearInAcceptableRange());
	}
}
