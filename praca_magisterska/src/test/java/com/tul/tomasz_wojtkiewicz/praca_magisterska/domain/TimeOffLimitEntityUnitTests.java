package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@Tag("entity")
class TimeOffLimitEntityUnitTests {
	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursInYearMoreThanMax")
	void isMaxHoursNotHigherThanHoursInYearReturnsFalseWhenMaxHoursExceedsHoursInYear(int year, int invalidMaxHoursInYear) {
		assertFalse(TimeOffLimitTestEntityFactory.builder().leaveYear(year).maxHours(invalidMaxHoursInYear).build().asEntity().isMaxHoursNotHigherThanHoursInYear());
	}

	@Test
	void isMaxHoursNotHigherThanHoursInYearReturnsTrueIfEitherValueIsNull() {
		var entity = TimeOffLimitTestEntityFactory.builder().maxHours(null).leaveYear(null).build().asEntity();
		assertTrue(entity.isMaxHoursNotHigherThanHoursInYear());
		entity.setMaxHours(366*25);
		assertTrue(entity.isMaxHoursNotHigherThanHoursInYear());
		entity.setMaxHours(null);
		entity.setLeaveYear(2000);
		assertTrue(entity.isMaxHoursNotHigherThanHoursInYear());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#hoursInYearLessThanOrMax")
	void isMaxHoursNotHigherThanHoursInYearReturnsTrueWhenMaxHoursEqualsHoursInYear(int year, int validMaxHoursInYear) {
		assertTrue(TimeOffLimitTestEntityFactory.builder().leaveYear(year).maxHours(validMaxHoursInYear).build().asEntity().isMaxHoursNotHigherThanHoursInYear());
	}

	@Test
	void equalsShouldCompareFieldsOtherThanTimeOffs() {
		var type = new TimeOffTypeEntity();
		var employee = new EmployeeEntity();
		var entity1 = TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity();
		var entity2 = TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity();
		assertEquals(entity1, entity2);

		entity1.setTimeOffs(List.of(new TimeOffEntity()));
		assertEquals(entity1, entity2);

		entity1.setLeaveYear(entity2.getLeaveYear()+1);
		assertNotEquals(entity1, entity2);

		entity1.setLeaveYear(entity2.getLeaveYear());
		entity1.setMaxHours(entity2.getMaxHours()+1);
		assertNotEquals(entity1, entity2);

		entity1.setMaxHours(entity2.getMaxHours());
		entity1.setId(1L);
		assertNotEquals(entity1, entity2);

		entity1.setId(entity2.getId());
		entity1.setTimeOffType(null);
		assertNotEquals(entity1, entity2);
		entity2.setTimeOffType(null);
		assertEquals(entity1, entity2);

		entity1.setEmployee(null);
		assertNotEquals(entity1, entity2);
		entity2.setEmployee(null);
		assertEquals(entity1, entity2);
	}
}
