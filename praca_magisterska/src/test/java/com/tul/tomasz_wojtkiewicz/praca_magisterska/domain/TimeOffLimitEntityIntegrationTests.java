package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Tag("integration")
@Tag("entity")
class TimeOffLimitEntityIntegrationTests implements ConstraintValidation {
	@Test
	void basicValidEntityPassesValidation() {
		assertEquals(0, validateConstraints(TimeOffLimitTestEntityFactory.build().asEntity()).size());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursInYearNegative")
	void maxHoursCanNotBeNegative(int invalidMaxHours) {
		var validation = validateConstraints(TimeOffLimitTestEntityFactory.builder().maxHours(invalidMaxHours).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("maxHours", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void maxHoursCanNotBeNull() {
		var validation = validateConstraints(TimeOffLimitTestEntityFactory.builder().maxHours(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("maxHours", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void defaultMaxHoursIsZero() {
		assertEquals(0, new TimeOffLimitEntity().getMaxHours());
	}

	@Test
	void maxHoursMoreThanHoursInYearTriggersCustomValidationAssertion() {
		var validation = validateConstraints(TimeOffLimitTestEntityFactory.builder().leaveYear(2025).maxHours(LocalDate.of(2025, 12, 31).getDayOfYear() * 24 + 1).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("maxHoursNotHigherThanHoursInYear", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void leaveYearCanNotBeNull() {
		var validation = validateConstraints(TimeOffLimitTestEntityFactory.builder().leaveYear(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("leaveYear", validation.iterator().next().getPropertyPath().toString());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
	void leaveYearMustBeInRange(int invalidLeaveYear) {
		var validation = validateConstraints(TimeOffLimitTestEntityFactory.builder().leaveYear(invalidLeaveYear).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("leaveYear", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void employeeMustNotBeNull() {
		var validation = validateConstraints(TimeOffLimitTestEntityFactory.builder().employee(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("employee", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void timeOffTypeMustNotBeNull() {
		var validation = validateConstraints(TimeOffLimitTestEntityFactory.builder().timeOffType(null).build().asEntity());
		assertEquals(1, validation.size());
		assertEquals("timeOffType", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void equalsReturnsTrueIfBothOEntitiesHaveDifferentTypeOrEmployeeObjectsWithMatchingValues() {
		var type1 = TimeOffTypeTestEntityFactory.build().asEntity();
		var type2 = TimeOffTypeTestEntityFactory.build().asEntity();
		var employee1 = EmployeeTestEntityFactory.build().asEntity();
		var employee2 = EmployeeTestEntityFactory.build().asEntity();
		var limit1 = TimeOffLimitTestEntityFactory.builder().timeOffType(type1).employee(employee1).build().asEntity();
		var limit2 = TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(employee2).build().asEntity();
		assertEquals(limit1, limit2);

		employee2.setEmail(employee1.getEmail()+"a");
		assertNotEquals(limit1, limit2);

		employee2.setEmail(employee1.getEmail());
		type2.setName(type1.getName()+"a");
		assertNotEquals(limit1, limit2);

		type2.setName(type1.getName());
		assertEquals(limit1, limit2);
	}
}
