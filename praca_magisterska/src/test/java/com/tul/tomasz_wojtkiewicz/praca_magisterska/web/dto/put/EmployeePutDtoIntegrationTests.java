package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeePutTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name.Name;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer.PhoneNumber;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dto")
@Tag("integration")
class EmployeePutDtoIntegrationTests implements ConstraintValidation {
	@Test
	void given_validDto_when_constraintsValidation_then_noErrors() {
		var dto = EmployeePutTestDtoFactory.build().asDto();
		var validation = validateConstraints(dto);
		assertTrue(validation.isEmpty());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#emails")
	void given_invalidEmail_when_constraintsValidation_then_oneError_and_emailCausedError(String email) {
		var dto = EmployeePutTestDtoFactory.builder().email(email).build().asDto();
		var validation = validateConstraints(dto);
		assertEquals(1, validation.size());
		assertEquals("email", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void given_blankOrNullEmail_when_constraintsValidation_then_oneError_and_emailCausedError() {
		{
			var dto = EmployeePutTestDtoFactory.builder().email(null).build().asDto();
			var validation = validateConstraints(dto);
			assertEquals(1, validation.size());
			assertEquals("email", validation.iterator().next().getPropertyPath().toString());
		}
		{
			var dto = EmployeePutTestDtoFactory.builder().email("").build().asDto();
			var validation = validateConstraints(dto);
			assertEquals(1, validation.size());
			assertEquals("email", validation.iterator().next().getPropertyPath().toString());
		}
	}

	@Test
	void given_invalidFirstName_when_constraintsValidation_then_oneError_and_constraintAnnotationIsName() {
		var validation = validateConstraints(EmployeePutTestDtoFactory.builder().firstName("3").build().asDto());
		assertEquals(1, validation.size());
		assertEquals(Name.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
	}

	@Test
	void given_nullFirstName_when_constraintsValidation_then_oneError_and_firstNameCausedError() {
		var validation = validateConstraints(EmployeePutTestDtoFactory.builder().firstName(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("firstName", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void given_invalidLastName_when_constraintsValidation_then_oneError_and_constraintAnnotationIsName() {
		var validation = validateConstraints(EmployeePutTestDtoFactory.builder().lastName("3").build().asDto());
		assertEquals(1, validation.size());
		assertEquals(Name.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
	}

	@Test
	void given_nullLastName_when_constraintsValidation_then_oneError_and_lastNameCausedError() {
		var validation = validateConstraints(EmployeePutTestDtoFactory.builder().lastName(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("lastName", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void given_invalidPhoneNumber_when_constraintsValidation_then_oneError_and_constraintAnnotationIsPhoneNumber() {
		var validation = validateConstraints(EmployeePutTestDtoFactory.builder().phoneNumber("3").build().asDto());
		assertEquals(1, validation.size());
		assertEquals(PhoneNumber.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
	}

	@Test
	void given_nullPhoneNumber_when_constraintsValidation_then_oneError_and_phoneNumberCausedError() {
		var validation = validateConstraints(EmployeePutTestDtoFactory.builder().phoneNumber(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("phoneNumber", validation.iterator().next().getPropertyPath().toString());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#accessLevels")
	void given_invalidAccessLevel_when_constraintsValidation_then_oneError_and_accessLevelCausedError(Integer invalidAccessLevel) {
		var validation = validateConstraints(EmployeePutTestDtoFactory.builder().accessLevel(invalidAccessLevel).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("accessLevel", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
	void given_nullAccessLevel_when_constraintsValidation_then_oneError_and_accessLevelCausedError() {
		var validation = validateConstraints(EmployeePutTestDtoFactory.builder().accessLevel(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("accessLevel", validation.iterator().next().getPropertyPath().toString());
	}
}
