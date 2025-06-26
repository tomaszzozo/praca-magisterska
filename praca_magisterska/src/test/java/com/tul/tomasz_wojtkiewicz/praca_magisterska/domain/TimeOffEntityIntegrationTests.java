package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@Tag("entity")
class TimeOffEntityIntegrationTests implements ConstraintValidation {
	@Test
		// cases: 1
	void basicValidEntityPassesValidation() {
		assertEquals(0, validateConstraints(TimeOffTestEntityFactory.build().asEntity()).size());
	}

	@Test
		// cases: 1
	void firstDayCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().firstDay(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("firstDay", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void lastDayInclusiveCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().lastDayInclusive(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("lastDayInclusive", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void hoursCountCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().hoursCount(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("hoursCount", validation.iterator().next().getPropertyPath().toString());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
		// cases: 4
	void hoursCountCanNotBeNegativeOrZero(int invalidHoursCount) {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().hoursCount(invalidHoursCount).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("hoursCount", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void commentCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().comment(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("comment", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void timeOffYearlyLimitCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().timeOffYearlyLimit(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("timeOffYearlyLimit", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void timeOffTypeCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().timeOffType(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("timeOffType", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void employeeCanNotBeNull() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().employee(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("employee", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void firstDayAfterLastDayTriggersCustomValidationAssertion() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().firstDay(LocalDate.of(2025, 12, 31)).lastDayInclusive(LocalDate.of(2025, 12, 30)).build().asEntity());
		assertFalse(validation.isEmpty());
		assertTrue(validation.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstDayAfterLastDay")));
	}

	@Test
		// cases: 1
	void monthNotEqualTriggersCustomValidationAssertion() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().firstDay(LocalDate.of(2025, 10, 10)).lastDayInclusive(LocalDate.of(2025, 11, 10)).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("yearAndMonthEqual", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void hoursCountMoreThanHoursInTimeOffTriggersCustomValidationAssertion() {
		var validation = validateConstraints(TimeOffTestEntityFactory.builder().hoursCount(Integer.MAX_VALUE).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("hoursCountLessThanHoursInTimeOff", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void yearOutsideOfRangeTriggersCustomValidationAssertion() {
		var entity = TimeOffTestEntityFactory.build().asEntity();
		entity.setFirstDay(entity.getFirstDay().minusYears(3000));
		entity.setLastDayInclusive(entity.getFirstDay());
		var validation = validateConstraints(entity);
		assertEquals(1, validation.size());
		assertEquals("yearInAcceptableRange", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 4
	void equalsReturnsTrueIfBothObjectsHaveDifferentLimitOrEmployeeOrTypeObjectsWithSameValues() {
		var employee1 = EmployeeTestEntityFactory.build().asEntity();
		var employee2 = EmployeeTestEntityFactory.build().asEntity();
		var type1 = TimeOffTypeTestEntityFactory.build().asEntity();
		var type2 = TimeOffTypeTestEntityFactory.build().asEntity();
		var limit1 = TimeOffLimitTestEntityFactory.builder().employee(employee1).timeOffType(type1).build().asEntity();
		var limit2 = TimeOffLimitTestEntityFactory.builder().employee(employee2).timeOffType(type2).build().asEntity();
		var entity1 = TimeOffTestEntityFactory.builder().employee(employee1).timeOffType(type1).timeOffYearlyLimit(limit1).build().asEntity();
		var entity2 = TimeOffTestEntityFactory.builder().employee(employee2).timeOffType(type2).timeOffYearlyLimit(limit2).build().asEntity();
		assertEquals(entity1, entity2);

		employee1.setEmail(employee2.getEmail()+"a");
		assertNotEquals(entity1, entity2);

		employee1.setEmail(employee2.getEmail());
		type1.setName(type2.getName()+"a");
		assertNotEquals(entity1, entity2);

		type1.setName(type2.getName());
		limit1.setMaxHours(limit2.getMaxHours()+1);
		assertNotEquals(entity1, entity2);

		limit1.setMaxHours(limit2.getMaxHours());
		assertEquals(entity1, entity2);
	}
}
