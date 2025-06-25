package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
// ai tag: unit
class TimeOffPostDtoTest {

	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	private TimeOffPostDto createValidDto() {
		TimeOffPostDto dto = new TimeOffPostDto();
		dto.setFirstDay(LocalDate.of(2025, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 3));
		dto.setHoursCount(24); // 3 days * 24 = 72 max
		dto.setTypeId(1L);
		dto.setYearlyLimitId(1L);
		dto.setEmployeeId(1L);
		dto.setComment("Vacation");
		return dto;
	}

	@Test
	void shouldPassValidationWithCorrectData() {
		TimeOffPostDto dto = createValidDto();
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}

	@Test
	void shouldFailIfFirstDayAfterLastDay() {
		TimeOffPostDto dto = createValidDto();
		dto.setFirstDay(LocalDate.of(2025, 6, 5));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 3));
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		// MISTAKE: no check for other violations
//		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be false"))); MISTAKE: more than one violation can contain that message
		assertEquals(2, violations.size());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstDayAfterLastDay")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hoursCountLessThanHoursInTimeOff")));
	}

	@Test
	void shouldFailIfDifferentMonth() {
		TimeOffPostDto dto = createValidDto();
		dto.setFirstDay(LocalDate.of(2025, 6, 30));
		dto.setLastDayInclusive(LocalDate.of(2025, 7, 1));
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		// MISTAKE: no check for other violations
//		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be true"))); MISTAKE: more than one violation can contain that message
		assertEquals(1, violations.size());
		assertEquals("yearAndMonthEqual", violations.iterator().next().getPropertyPath().toString());
	}

	@Test
	void shouldFailIfHoursExceedAvailableTime() {
		TimeOffPostDto dto = createValidDto();
		dto.setHoursCount(1000); // More than 72
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		// MISTAKE: no check for other violations
//		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be true"))); MISTAKE: more than one violation can contain that message
		assertEquals(1, violations.size());
		assertEquals("hoursCountLessThanHoursInTimeOff", violations.iterator().next().getPropertyPath().toString());
	}

	@Test
	void shouldFailIfYearIsOutOfRange() {
		TimeOffPostDto dto = createValidDto();
		dto.setFirstDay(LocalDate.of(2019, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2019, 6, 3));
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		// MISTAKE: no check for other violations
//		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be true"))); MISTAKE: more than one violation can contain that message
		assertEquals(1, violations.size());
		assertEquals("yearInAcceptableRange", violations.iterator().next().getPropertyPath().toString());
	}

	@Test
	void shouldFailWithNullFields() {
		TimeOffPostDto dto = new TimeOffPostDto();
		Set<ConstraintViolation<TimeOffPostDto>> violations = validator.validate(dto);
		assertEquals(7, violations.size());
	}
}
