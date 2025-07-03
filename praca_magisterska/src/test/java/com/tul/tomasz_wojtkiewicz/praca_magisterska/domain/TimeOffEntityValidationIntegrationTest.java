// Testy integracyjne walidacji beanów TimeOffEntity z zależnościami (TimeOffLimitEntity, TimeOffTypeEntity, EmployeeEntity)
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
class TimeOffEntityValidationIntegrationTest {

	@Autowired
	private Validator validator;

	private TimeOffEntity timeOff;
	private TimeOffLimitEntity timeOffLimit;
	private TimeOffTypeEntity timeOffType;
	private EmployeeEntity employee;

	@BeforeEach
	void setup() {
		timeOffLimit = new TimeOffLimitEntity();
		timeOffLimit.setId(1L);
		timeOffLimit.setLeaveYear(2023);
		timeOffLimit.setMaxHours(2000);

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

		timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2023, 5, 1));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 5, 3));
		timeOff.setHoursCount(48);
		timeOff.setComment("Annual leave");
		timeOff.setTimeOffYearlyLimit(timeOffLimit);
		timeOff.setTimeOffType(timeOffType);
		timeOff.setEmployee(employee);
	}

	@Test
	void validTimeOffEntity_ShouldHaveNoViolations() {
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertTrue(violations.isEmpty());
	}

	@Test
	void nullFirstDay_ShouldFailValidation() {
		timeOff.setFirstDay(null);
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstDay")));
	}

	@Test
	void nullLastDayInclusive_ShouldFailValidation() {
		timeOff.setLastDayInclusive(null);
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastDayInclusive")));
	}

	@Test
	void hoursCountLessThanOne_ShouldFailValidation() {
		timeOff.setHoursCount(0);
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hoursCount")));
	}

	@Test
	void nullComment_ShouldFailValidation() {
		timeOff.setComment(null);
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("comment")));
	}

	@Test
	void nullTimeOffYearlyLimit_ShouldFailValidation() {
		timeOff.setTimeOffYearlyLimit(null);
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("timeOffYearlyLimit")));
	}

	@Test
	void nullTimeOffType_ShouldFailValidation() {
		timeOff.setTimeOffType(null);
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("timeOffType")));
	}

	@Test
	void nullEmployee_ShouldFailValidation() {
		timeOff.setEmployee(null);
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employee")));
	}

	@Test
	void firstDayAfterLastDay_ShouldFailAssertFalseValidation() {
		timeOff.setFirstDay(LocalDate.of(2023, 5, 5));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 5, 3));
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be false")));
	}

	@Test
	void differentYearMonth_ShouldFailAssertTrueValidation() {
		timeOff.setFirstDay(LocalDate.of(2023, 5, 31));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 6, 1));
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be true")));
	}

	@Test
	void hoursCountMoreThanAllowed_ShouldFailAssertTrueValidation() {
		timeOff.setHoursCount(1000);
		timeOff.setFirstDay(LocalDate.of(2023, 1, 1));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 1, 1));
		// 1 day * 24 = 24 < 1000 invalid
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be true")));
	}

	@Test
	void yearOutOfRange_ShouldFailAssertTrueValidation() {
		timeOff.setFirstDay(LocalDate.of(2019, 12, 31));
		timeOff.setLastDayInclusive(LocalDate.of(2023, 1, 1));
		Set<ConstraintViolation<TimeOffEntity>> violations = validator.validate(timeOff);
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be true")));
	}
}
