package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;// Testy jednostkowe dla TimeOffPostDto (walidacja)

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
// ai tag: unit
class TimeOffPostDtoValidationTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post'

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	@BeforeAll
	static void setup() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void tearDown() {
		validatorFactory.close();
	}

	private TimeOffPostDto createValidDto() {
		TimeOffPostDto dto = new TimeOffPostDto();
		dto.setFirstDay(LocalDate.of(2025, 7, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 7, 3));
		dto.setHoursCount(48);
		dto.setTypeId(1L);
		dto.setYearlyLimitId(1L);
		dto.setEmployeeId(1L);
		dto.setComment("Valid comment");
		return dto;
	}

	@Test
		// CASES: 1
	void validDto_shouldHaveNoViolations() {
		TimeOffPostDto dto = createValidDto();
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}

	@Test
		// CASES: 7
	void nullFields_shouldReturnViolations() {
		TimeOffPostDto dto = new TimeOffPostDto();
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstDay")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastDayInclusive")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hoursCount")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("typeId")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("yearlyLimitId")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeId")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("comment")));
	}

	@Test
		// CASES: 1
	void hoursCountLessThanOne_shouldReturnViolation() {
		TimeOffPostDto dto = createValidDto();
		dto.setHoursCount(0);
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hoursCount")));
	}

	@Test
		// CASES: 1
	void typeIdLessThanOne_shouldReturnViolation() {
		TimeOffPostDto dto = createValidDto();
		dto.setTypeId(0L);
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("typeId")));
	}

	@Test
		// CASES: 1
	void yearlyLimitIdLessThanOne_shouldReturnViolation() {
		TimeOffPostDto dto = createValidDto();
		dto.setYearlyLimitId(0L);
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("yearlyLimitId")));
	}

	@Test
		// CASES: 1
	void employeeIdLessThanOne_shouldReturnViolation() {
		TimeOffPostDto dto = createValidDto();
		dto.setEmployeeId(0L);
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeId")));
	}

	@Test
		// CASES: 1
	void firstDayAfterLastDay_shouldReturnViolation() {
		TimeOffPostDto dto = createValidDto();
		dto.setFirstDay(LocalDate.of(2025, 7, 5));
		dto.setLastDayInclusive(LocalDate.of(2025, 7, 3));
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("musi mieć wartość false"))); // MISTAKE: assuming english violation messages
	}

	@Test
		// CASES: 1
	void yearAndMonthNotEqual_shouldReturnViolation() {
		TimeOffPostDto dto = createValidDto();
		dto.setFirstDay(LocalDate.of(2025, 7, 31));
		dto.setLastDayInclusive(LocalDate.of(2025, 8, 1));
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("musi mieć wartość true"))); // MISTAKE: assuming english violation messages
	}

	@Test
		// CASES: 1
	void hoursCountExceedsDaysTimes24_shouldReturnViolation() {
		TimeOffPostDto dto = createValidDto();
		// Period 3 days (1,2,3) * 24 = 72 hours max
		dto.setHoursCount(73);
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("musi mieć wartość true"))); // MISTAKE: assuming english violation messages
	}

	@Test
		// CASES: 2
	void yearOutOfRange_shouldReturnViolation() {
		TimeOffPostDto dto = createValidDto();
		dto.setFirstDay(LocalDate.of(2019, 7, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 7, 1));
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());

		dto.setFirstDay(LocalDate.of(2025, 7, 1));
		dto.setLastDayInclusive(LocalDate.of(2101, 7, 1));
		violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
	}
}
