package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeePostTestDtoFactory;
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
class EmployeePostDtoIntegrationTests implements ConstraintValidation {
	@Test
		// cases: 1
	void given_validDto_when_constraintsValidation_then_noErrors() {
		var dto = EmployeePostTestDtoFactory.build().asDto();
		var validation = validateConstraints(dto);
		assertTrue(validation.isEmpty());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#emails")
		// cases: 6
	void given_invalidEmail_when_constraintsValidation_then_oneError_and_emailCausedError(String email) {
		var dto = EmployeePostTestDtoFactory.builder().email(email).build().asDto();
		var validation = validateConstraints(dto);
		assertEquals(1, validation.size());
		assertEquals("email", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 2
	void given_blankOrNullEmail_when_constraintsValidation_then_oneError_and_emailCausedError() {
		{
			var dto = EmployeePostTestDtoFactory.builder().email(null).build().asDto();
			var validation = validateConstraints(dto);
			assertEquals(1, validation.size());
			assertEquals("email", validation.iterator().next().getPropertyPath().toString());
		}
		{
			var dto = EmployeePostTestDtoFactory.builder().email("").build().asDto();
			var validation = validateConstraints(dto);
			assertEquals(1, validation.size());
			assertEquals("email", validation.iterator().next().getPropertyPath().toString());
		}
	}

	@Test
		// cases: 1
	void given_invalidFirstName_when_constraintsValidation_then_oneError_and_constraintAnnotationIsName() {
		var validation = validateConstraints(EmployeePostTestDtoFactory.builder().firstName("3").build().asDto());
		assertEquals(1, validation.size());
		assertEquals(Name.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
	}

	@Test
		// cases: 1
	void given_nullFirstName_when_constraintsValidation_then_oneError_and_firstNameCausedError() {
		var validation = validateConstraints(EmployeePostTestDtoFactory.builder().firstName(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("firstName", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void given_invalidLastName_when_constraintsValidation_then_oneError_and_constraintAnnotationIsName() {
		var validation = validateConstraints(EmployeePostTestDtoFactory.builder().lastName("3").build().asDto());
		assertEquals(1, validation.size());
		assertEquals(Name.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
	}

	@Test
		// cases: 1
	void given_nullLastName_when_constraintsValidation_then_oneError_and_lastNameCausedError() {
		var validation = validateConstraints(EmployeePostTestDtoFactory.builder().lastName(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("lastName", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void given_invalidPhoneNumber_when_constraintsValidation_then_oneError_and_constraintAnnotationIsPhoneNumber() {
		var validation = validateConstraints(EmployeePostTestDtoFactory.builder().phoneNumber("3").build().asDto());
		assertEquals(1, validation.size());
		assertEquals(PhoneNumber.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
	}

	@Test
		// cases: 1
	void given_nullPhoneNumber_when_constraintsValidation_then_oneError_and_phoneNumberCausedError() {
		var validation = validateConstraints(EmployeePostTestDtoFactory.builder().phoneNumber(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("phoneNumber", validation.iterator().next().getPropertyPath().toString());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#accessLevels")
		// cases: 6
	void given_invalidAccessLevel_when_constraintsValidation_then_oneError_and_accessLevelCausedError(Integer invalidAccessLevel) {
		var validation = validateConstraints(EmployeePostTestDtoFactory.builder().accessLevel(invalidAccessLevel).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("accessLevel", validation.iterator().next().getPropertyPath().toString());
	}

	@Test
		// cases: 1
	void given_nullAccessLevel_when_constraintsValidation_then_oneError_and_accessLevelCausedError() {
		var validation = validateConstraints(EmployeePostTestDtoFactory.builder().accessLevel(null).build().asDto());
		assertEquals(1, validation.size());
		assertEquals("accessLevel", validation.iterator().next().getPropertyPath().toString());
	}
}
