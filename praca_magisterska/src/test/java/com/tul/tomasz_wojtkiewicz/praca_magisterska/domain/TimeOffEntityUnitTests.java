package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@Tag("entity")
class TimeOffEntityUnitTests {


	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#firstDayAfterLastDay")
	void isFirstDayAfterLastDayShouldReturnTrueWhenFirstDayAfterLastDay(LocalDate lastDay, LocalDate firstDay) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asEntity();
		assertTrue(entity.isFirstDayAfterLastDay());
	}

	@Test
	void isFirstDayAfterLastDayShouldReturnFalseWhenEitherValueIsNull() {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(null).firstDay(null).build().asEntity();
		assertFalse(entity.isFirstDayAfterLastDay());
		entity.setFirstDay(LocalDate.now());
		assertFalse(entity.isFirstDayAfterLastDay());
		entity.setFirstDay(null);
		entity.setLastDayInclusive(LocalDate.now());
		assertFalse(entity.isFirstDayAfterLastDay());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#firstDayBeforeOrSameAsLastDay")
	void isFirstDayAfterLastDayShouldReturnFalseWhenFirstDayBeforeOrSameAsLastDay(LocalDate lastDay, LocalDate firstDay) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asEntity();
		assertFalse(entity.isFirstDayAfterLastDay());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#sameDatesDifferentDays")
	void isYearAndMonthEqualShouldReturnTrue(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asEntity();
		assertTrue(entity.isYearAndMonthEqual());
	}

	@Test
	void isYearAndMonthEqualShouldReturnTrueIfEitherValueIsNull() {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(null).firstDay(null).build().asEntity();
		assertTrue(entity.isYearAndMonthEqual());
		entity.setFirstDay(LocalDate.now());
		assertTrue(entity.isYearAndMonthEqual());
		entity.setFirstDay(null);
		entity.setLastDayInclusive(LocalDate.now());
		assertTrue(entity.isYearAndMonthEqual());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#differentDates")
	void isYearAndMonthEqualShouldReturnFalse(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asEntity();
		assertFalse(entity.isYearAndMonthEqual());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursCountMoreThanHoursInTimeOff")
	void isHoursCountLessThanHoursInTimeOffShouldReturnFalse(int invalidHoursCount, LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(invalidHoursCount).build().asEntity();
		assertFalse(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#nullableFirstAndLastDayAndHoursCount")
	void isHoursCountLessThanHoursInTimeOffShouldReturnFalseShouldReturnTrueIfEitherValueIsNull(LocalDate firstDay, LocalDate lastDay, Integer hoursCount) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(hoursCount).build().asEntity();
		assertTrue(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#hoursCountLessOrEqualHoursInTimeOff")
	void isHoursCountLessThanHoursInTimeOffShouldReturnTrue(int validHoursCount, LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).hoursCount(validHoursCount).build().asEntity();
		assertTrue(entity.isHoursCountLessThanHoursInTimeOff());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#firstOrLastDayWithYearOutsideOfRange")
	void isYearInAcceptableRangeShouldReturnFalse(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asEntity();
		assertFalse(entity.isYearInAcceptableRange());
	}

	@Test
	void isYearInAcceptableRangeShouldReturnTrueIfEitherValueIsNull() {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(null).firstDay(null).build().asEntity();
		assertTrue(entity.isYearInAcceptableRange());
		entity.setFirstDay(LocalDate.now());
		assertTrue(entity.isYearInAcceptableRange());
		entity.setFirstDay(null);
		entity.setLastDayInclusive(LocalDate.now());
		assertTrue(entity.isYearInAcceptableRange());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#firstAndLastDayWithYearInRange")
	void isYearInAcceptableRangeShouldReturnTrue(LocalDate firstDay, LocalDate lastDay) {
		var entity = TimeOffTestEntityFactory.builder().lastDayInclusive(lastDay).firstDay(firstDay).build().asEntity();
		assertTrue(entity.isYearInAcceptableRange());
	}

	@Test
	void equalsShouldCompareFieldsOtherThanEmployeeAndLimitAndType() {
		var type = new TimeOffTypeEntity();
		var employee = new EmployeeEntity();
		var limit = new TimeOffLimitEntity();
		var entity1 = TimeOffTestEntityFactory.builder().timeOffYearlyLimit(limit).employee(employee).timeOffType(type).build().asEntity();
		var entity2 = TimeOffTestEntityFactory.builder().timeOffYearlyLimit(limit).employee(employee).timeOffType(type).build().asEntity();
		assertEquals(entity1, entity2);

		entity1.setEmployee(null);
		assertNotEquals(entity1, entity2);
		entity2.setEmployee(null);
		assertEquals(entity1, entity2);

		entity1.setTimeOffType(null);
		assertNotEquals(entity1, entity2);
		entity2.setTimeOffType(null);
		assertEquals(entity1, entity2);

		entity1.setTimeOffYearlyLimit(null);
		assertNotEquals(entity1, entity2);
		entity2.setTimeOffYearlyLimit(null);
		assertEquals(entity1, entity2);

		entity1.setId(1L);
		assertNotEquals(entity1, entity2);

		entity1.setId(entity2.getId());
		entity1.setHoursCount(entity2.getHoursCount() + 1);
		assertNotEquals(entity1, entity2);

		entity1.setHoursCount(entity2.getHoursCount());
		entity1.setComment(entity2.getComment() + "a");
		assertNotEquals(entity1, entity2);

		entity1.setComment(entity2.getComment());
		entity1.setFirstDay(entity2.getFirstDay().plusDays(1));
		assertNotEquals(entity1, entity2);

		entity1.setFirstDay(entity2.getFirstDay());
		entity1.setLastDayInclusive(entity2.getLastDayInclusive().plusDays(1));
		assertNotEquals(entity1, entity2);
	}
}
