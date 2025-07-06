// Testy integracyjne walidacji beanów TimeOffLimitEntity z zależnościami (TimeOffTypeEntity, EmployeeEntity)
package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
// ai tag: integration
class TimeOffLimitEntityValidationIntegrationTest {

	@Autowired
	private Validator validator;

	private TimeOffLimitEntity limit;
	private TimeOffTypeEntity timeOffType;
	private EmployeeEntity employee;

	@BeforeEach
	void setup() {
		timeOffType = new TimeOffTypeEntity();
		timeOffType.setId(1L);
		timeOffType.setName("Vacation");
		timeOffType.setCompensationPercentage(50f);

		employee = new EmployeeEntity();
		employee.setId(1L);
		employee.setEmail("employee@example.com");
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setPhoneNumber("123456789");
		employee.setAccessLevel(1);

		limit = new TimeOffLimitEntity();
		limit.setMaxHours(500);
		limit.setLeaveYear(2023);
		limit.setTimeOffType(timeOffType);
		limit.setEmployee(employee);
	}

	@Test
		// cases: 1
	void validTimeOffLimitEntity_ShouldHaveNoViolations() {
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertTrue(violations.isEmpty());
	}

	@Test
		// cases: 1
	void nullMaxHours_ShouldPassAssertTrueValidation() {
		limit.setMaxHours(null);
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertFalse(violations.isEmpty()); // MISTAKE: should fail due to @NotNull annotation
	}

	@Test
		// cases: 1
	void nullLeaveYear_ShouldPassAssertTrueValidation() {
		limit.setLeaveYear(null);
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertFalse(violations.isEmpty()); // MISTAKE: should fail due to @NotNull annotation
	}

	@Test
		// cases: 1
	void maxHoursNegative_ShouldFailMinValidation() {
		limit.setMaxHours(-1);
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("maxHours")));
	}

	@Test
		// cases: 1
	void leaveYearOutOfRangeLow_ShouldFailRangeValidation() {
		limit.setLeaveYear(2019);
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("leaveYear")));
	}

	@Test
		// cases: 1
	void leaveYearOutOfRangeHigh_ShouldFailRangeValidation() {
		limit.setLeaveYear(2101);
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("leaveYear")));
	}

	@Test
		// cases: 1
	void nullTimeOffType_ShouldFailNotNullValidation() {
		limit.setTimeOffType(null);
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("timeOffType")));
	}

	@Test
		// cases: 1
	void nullEmployee_ShouldFailNotNullValidation() {
		limit.setEmployee(null);
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employee")));
	}

	@Test
		// cases: 1
	void maxHoursGreaterThanHoursInYear_ShouldFailAssertTrueValidation() {
		int daysInYear = LocalDate.of(limit.getLeaveYear(), 12, 31).getDayOfYear();
		limit.setMaxHours(daysInYear * 24 + 1); // o 1 za dużo
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("musi mieć wartość true"))); // MISTAKE: assuming english violation messages
	}

	@Test
		// cases: 1
	void maxHoursEqualToHoursInYear_ShouldPassAssertTrueValidation() {
		int daysInYear = LocalDate.of(limit.getLeaveYear(), 12, 31).getDayOfYear();
		limit.setMaxHours(daysInYear * 24); // dokładnie limit
		Set<ConstraintViolation<TimeOffLimitEntity>> violations = validator.validate(limit);
		assertTrue(violations.isEmpty());
	}
}
