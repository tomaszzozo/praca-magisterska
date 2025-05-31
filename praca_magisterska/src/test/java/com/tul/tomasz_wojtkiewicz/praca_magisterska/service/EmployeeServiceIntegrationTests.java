package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeePostTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeePutTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
@Tag("service")
class EmployeeServiceIntegrationTests {
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeRepository employeeRepository;

	@AfterEach
	void afterEach() {
		employeeRepository.deleteAll();
	}

	@Test
	void given_noSavedEntities_when_iCallGetAll_then_iGetEmptyList() {
		assertTrue(employeeService.getAll().isEmpty());
	}

	@Test
	void given_multipleSavedEntities_when_iCallGetAll_then_iGetListOfSavedEntities() {
		var employee1 = EmployeeTestEntityFactory.builder().email("e1@employee.com").phoneNumber("100000000").build().asEntity();
		var employee2 = EmployeeTestEntityFactory.builder().email("e2@employee.com").phoneNumber("200000000").build().asEntity();
		var employee3 = EmployeeTestEntityFactory.builder().email("e3@employee.com").phoneNumber("300000000").build().asEntity();
		employeeRepository.saveAll(List.of(employee1, employee2, employee3));

		var result = employeeService.getAll();

		assertEquals(3, result.size());
		assertTrue(result.contains(employee1));
		assertTrue(result.contains(employee2));
		assertTrue(result.contains(employee3));
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_iCallGetById_then_throwsConstraintViolationException(int invalidId) {
		assertThrows(ConstraintViolationException.class, () -> employeeService.getById(invalidId));
	}

	@Test
	void given_validId_and_savedEntityWithDifferentId_when_iCallGetById_then_throwsApiException_and_statusCodeIsNotFound() {
		var employee = employeeRepository.save(EmployeeTestEntityFactory.build().asEntity());
		var exception = assertThrows(ApiException.class, () -> employeeService.getById(employee.getId() + 1));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	void given_validId_and_savedEntityWithGivenId_when_iCallGetById_then_savedEntityIsReturned() {
		var employee = employeeRepository.save(EmployeeTestEntityFactory.build().asEntity());
		var result = employeeService.getById(employee.getId());
		assertEquals(employee, result);
	}

	@Test
	void given_invalidDto_when_iCallPost_then_throwsConstraintViolationException() {
		var dto = EmployeePostTestDtoFactory.builder().email("INVALID_MAIL").build().asDto();
		assertThrows(ConstraintViolationException.class, () -> employeeService.post(dto));
	}

	@Test
	void given_validDto_and_savedEmployeeWithSameEmail_when_iCallPost_then_throwsApiException() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().phoneNumber("111111111").build().asEntity());
		var dto = EmployeePostTestDtoFactory.builder().phoneNumber("222222222").email(employee.getEmail()).build().asDto();

		var result = assertThrows(ApiException.class, () -> employeeService.post(dto));
		assertEquals(HttpStatus.CONFLICT, result.getStatus());
	}

	@Test
	void given_validDto_and_savedEmployeeWithSamePhoneNumber_when_iCallPost_then_throwsApiException() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().email("employee@domain.com").build().asEntity());
		var dto = EmployeePostTestDtoFactory.builder().phoneNumber(employee.getPhoneNumber()).email("employee2@domain.com").build().asDto();

		var result = assertThrows(ApiException.class, () -> employeeService.post(dto));
		assertEquals(HttpStatus.CONFLICT, result.getStatus());
	}

	@Test
	void given_validDto_and_emptyDatabase_when_iCallPost_then_newEmployeeExistsInDatabase_and_itHasExpectedValues() {
		var dto = EmployeePostTestDtoFactory.build().asDto();
		employeeService.post(dto);

		assertEquals(1, employeeRepository.count());
		var result = employeeRepository.findAll().getFirst();
		assertEquals(dto.getEmail(), result.getEmail());
		assertEquals(dto.getPhoneNumber(), result.getPhoneNumber());
		assertEquals(dto.getAccessLevel(), result.getAccessLevel());
		assertEquals(dto.getFirstName(), result.getFirstName());
		assertEquals(dto.getLastName(), result.getLastName());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_and_validDto_when_iCallPut_then_throwsConstraintViolationException(int invalidId) {
		var dto = EmployeePutTestDtoFactory.build().asDto();
		assertThrows(ConstraintViolationException.class, () -> employeeService.put(invalidId, dto));
	}

	@Test
	void given_validId_and_invalidDto_and_employeeWithSameIdInDatabase_when_iCallPut_then_throwsConstraintViolationException() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var dto = EmployeePutTestDtoFactory.builder().email("INVALID_MAIL").build().asDto();
		assertThrows(ConstraintViolationException.class, () -> employeeService.put(employee.getId(), dto));
	}

	@Test
	void given_validId_and_validDto_and_emptyDatabase_when_iCallPut_then_throwsApiException_with_codeNotFound() {
		var dto = EmployeePutTestDtoFactory.build().asDto();
		var result = assertThrows(ApiException.class, () -> employeeService.put(1, dto));
		assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
	}

	@Test
	void given_validId_and_validDto_and_employeeWithSameIdInDatabase_and_employeeWithDifferentIdButSameMailAsDto_when_iCallPut_then_throwsApiException_with_codeConflict() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().email("employee@gmail.com").phoneNumber("111111111").build().asEntity());
		var employee2 = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().email("employee2@gmail.com").phoneNumber("222222222").build().asEntity());
		var dto = EmployeePutTestDtoFactory.builder().email(employee2.getEmail()).phoneNumber(employee.getPhoneNumber()).build().asDto();

		var result = assertThrows(ApiException.class, () -> employeeService.put(employee.getId(), dto));
		assertEquals(HttpStatus.CONFLICT, result.getStatus());
	}

	@Test
	void given_validId_and_validDto_and_employeeWithSameIdInDatabase_and_employeeWithDifferentIdButSamePhoneNumberAsDto_when_iCallPut_then_throwsApiException_with_codeConflict() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().email("employee@gmail.com").phoneNumber("111111111").build().asEntity());
		var employee2 = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().email("employee2@gmail.com").phoneNumber("222222222").build().asEntity());
		var dto = EmployeePutTestDtoFactory.builder().email(employee.getEmail()).phoneNumber(employee2.getPhoneNumber()).build().asDto();

		var result = assertThrows(ApiException.class, () -> employeeService.put(employee.getId(), dto));
		assertEquals(HttpStatus.CONFLICT, result.getStatus());
	}

	@Test
	void given_validDto_and_employeeWithSameIdInDatabase_when_iCallPut_then_employeeIsUpdatedInDatabase_and_itHasExpectedValues() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().firstName("Tester").lastName("Testinger").email("tester@test.com").phoneNumber("123456789").accessLevel(0).build().asEntity());
		var dto = EmployeePutTestDtoFactory.builder().firstName(employee.getLastName()).lastName(employee.getFirstName()).accessLevel(employee.getAccessLevel() + 1).email("test@tester.com").phoneNumber("987654321").build().asDto();
		employeeService.put(employee.getId(), dto);

		assertEquals(1, employeeRepository.count());
		var result = employeeRepository.findAll().getFirst();
		assertEquals(dto.getEmail(), result.getEmail());
		assertEquals(dto.getPhoneNumber(), result.getPhoneNumber());
		assertEquals(dto.getAccessLevel(), result.getAccessLevel());
		assertEquals(dto.getFirstName(), result.getFirstName());
		assertEquals(dto.getLastName(), result.getLastName());
	}

	@Test
	void given_validDto_and_employeeWithSameIdAndDataInDatabase_when_iCallPut_then_employeeDataDoesNotChange() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var dto = EmployeePutTestDtoFactory.builder().firstName(employee.getFirstName()).lastName(employee.getLastName()).email(employee.getEmail()).phoneNumber(employee.getPhoneNumber()).accessLevel(employee.getAccessLevel()).build().asDto();
		employeeService.put(employee.getId(), dto);

		assertEquals(1, employeeRepository.count());
		var result = employeeRepository.findAll().getFirst();
		assertEquals(employee.getEmail(), result.getEmail());
		assertEquals(employee.getPhoneNumber(), result.getPhoneNumber());
		assertEquals(employee.getAccessLevel(), result.getAccessLevel());
		assertEquals(employee.getFirstName(), result.getFirstName());
		assertEquals(employee.getLastName(), result.getLastName());
	}
}
