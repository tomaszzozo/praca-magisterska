package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypePutTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
@Tag("service")
class TimeOffTypeServiceIntegrationTests {
	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;
	@Autowired
	private TimeOffTypeService timeOffTypeService;
	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;
	@Autowired
	private TimeOffRepository timeOffRepository;
	@Autowired
	private EmployeeRepository employeeRepository;

	@AfterEach
	void afterEach() {
		timeOffRepository.deleteAll();
		timeOffLimitRepository.deleteAll();
		timeOffTypeRepository.deleteAll();
		employeeRepository.deleteAll();
	}

	@Test
	void given_emptyDatabase_when_getAll_then_returnsEmptyList() {
		assertTrue(timeOffTypeService.getAll().isEmpty());
	}

	@Test
	void given_multipleSavedEntities_when_getAll_then_returnsListOfSavedEntities() {
		var type1 = TimeOffTypeTestEntityFactory.builder().name("sick leave").compensationPercentage(80f).build().asEntity();
		var type2 = TimeOffTypeTestEntityFactory.builder().name("maternity leave").compensationPercentage(70f).build().asEntity();
		var type3 = TimeOffTypeTestEntityFactory.builder().name("on demand leave").compensationPercentage(100f).build().asEntity();
		timeOffTypeRepository.saveAllAndFlush(List.of(type1, type2, type3));

		var result = timeOffTypeService.getAll();

		assertEquals(3, result.size());
		assertTrue(result.contains(type1));
		assertTrue(result.contains(type2));
		assertTrue(result.contains(type3));
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_getById_then_throwsConstraintViolationException(int invalidId) {
		assertThrows(ConstraintViolationException.class, () -> timeOffTypeService.getById(invalidId));
	}

	@Test
	void given_validId_and_savedTypeWithDifferentId_when_getById_then_throwsApiException_and_statusCodeNotFound() {
		var type = timeOffTypeRepository.save(TimeOffTypeTestEntityFactory.build().asEntity());
		var exception = assertThrows(ApiException.class, () -> timeOffTypeService.getById(type.getId() + 1));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	void given_validId_and_savedTypeWithMatchingId_when_getById_then_returnsSavedType() {
		var type = timeOffTypeRepository.save(TimeOffTypeTestEntityFactory.build().asEntity());
		var result = timeOffTypeService.getById(type.getId());
		assertEquals(type, result);
	}

	@Test
	void given_invalidDto_when_iCallPutAll_then_throwsConstraintViolationException() {
		var dto = TimeOffTypePutTestDtoFactory.builder().name("").build().asDto();
		assertThrows(ConstraintViolationException.class, () -> timeOffTypeService.putAll(List.of(dto)));
	}

	@Test
	void given_emptyDtoList_and_notEmptyDatabase_when_iCallPutAll_then_allEntitiesAreDeleted() {
		var type1 = TimeOffTypeTestEntityFactory.builder().name("sick leave").compensationPercentage(80f).build().asEntity();
		var type2 = TimeOffTypeTestEntityFactory.builder().name("maternity leave").compensationPercentage(70f).build().asEntity();
		var type3 = TimeOffTypeTestEntityFactory.builder().name("on demand leave").compensationPercentage(100f).build().asEntity();
		timeOffTypeRepository.saveAllAndFlush(List.of(type1, type2, type3));

		timeOffTypeService.putAll(List.of());

		assertEquals(0, timeOffTypeRepository.count());
	}

	@Test
	void given_validDto_and_typeSavedWithMatchingId_and_otherTypesSaved_when_iCallPutAll_then_typesWithNonMatchingIdsAreDeleted_and_typeWithMatchingIdIsUpdated() {
		var type1 = TimeOffTypeTestEntityFactory.builder().name("sick leave").compensationPercentage(80f).build().asEntity();
		var type2 = TimeOffTypeTestEntityFactory.builder().name("maternity leave").compensationPercentage(70f).build().asEntity();
		var type3 = TimeOffTypeTestEntityFactory.builder().name("on demand leave").compensationPercentage(100f).build().asEntity();
		timeOffTypeRepository.saveAllAndFlush(List.of(type1, type2, type3));

		var dto = TimeOffTypePutTestDtoFactory.builder().id(type1.getId()).name("updated sick leave").compensationPercentage(100f).build().asDto();
		timeOffTypeService.putAll(List.of(dto));

		assertEquals(1, timeOffTypeRepository.count());
		var result = timeOffTypeRepository.findById(type1.getId()).orElseThrow();
		assertEquals(dto.getName(), result.getName());
		assertEquals(dto.getCompensationPercentage(), result.getCompensationPercentage());
	}

	@Test
	void given_validDto_and_typeSavedWithSameData_when_iCallPutAll_then_noUpdatesAreMade() {
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var dto = TimeOffTypePutTestDtoFactory.builder().id(type.getId()).name(type.getName()).compensationPercentage(type.getCompensationPercentage()).build().asDto();

		timeOffTypeService.putAll(List.of(dto));

		assertEquals(1, timeOffTypeRepository.count());
		var result = timeOffTypeRepository.findById(type.getId()).orElseThrow();
		assertEquals(type, result);
	}

	@Test
	void given_noDtos_and_typeSavedWithDependentTimeOffs_when_iCallPutAll_then_throwsApiException_with_statusCodeConflict() {
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
		timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(type).timeOffYearlyLimit(limit).employee(employee).build().asEntity());

		var result = assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of()));
		assertEquals(HttpStatus.CONFLICT, result.getStatus());
	}

	@Test
	void given_validDto_and_typeSavedWithMatchingId_and_otherTypeSavedWithMatchingName_when_iCallPutAll_then_throwsApiException_with_statusCodeConflict_and_databaseIsNotUpdated() {
		var type1 = TimeOffTypeTestEntityFactory.builder().name("sick leave").compensationPercentage(80f).build().asEntity();
		var type2 = TimeOffTypeTestEntityFactory.builder().name("maternity leave").compensationPercentage(70f).build().asEntity();
		timeOffTypeRepository.saveAllAndFlush(List.of(type1, type2));

		var dto1 = TimeOffTypePutTestDtoFactory.builder().id(type2.getId()).name(type2.getName()).compensationPercentage(10f).build().asDto();
		var dto2 = TimeOffTypePutTestDtoFactory.builder().id(type1.getId()).name(type2.getName()).build().asDto();

		var result = assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of(dto1, dto2)));
		assertEquals(HttpStatus.CONFLICT, result.getStatus());
		assertEquals(70f, timeOffTypeRepository.findById(type2.getId()).orElseThrow().getCompensationPercentage());
	}

	@Test
	void given_validDtos_and_savedTypeWithMatchingName_when_iCallPutAll_then_savedTypeIsDeleted_and_newTypesAreCreated() {
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().compensationPercentage(100f).build().asEntity());
		var dto1 = TimeOffTypePutTestDtoFactory.builder().id(0L).name(type.getName()).compensationPercentage(0f).build().asDto();
		var dto2 = TimeOffTypePutTestDtoFactory.builder().id(0L).name(type.getName() + " version 2").compensationPercentage(0f).build().asDto();

		timeOffTypeService.putAll(List.of(dto1, dto2));

		assertEquals(2, timeOffTypeRepository.count());
		assertTrue(timeOffTypeRepository.findAll().stream().anyMatch(t -> t.getName().equals(dto1.getName()) && t.getCompensationPercentage() == dto1.getCompensationPercentage()));
		assertTrue(timeOffTypeRepository.findAll().stream().anyMatch(t -> t.getName().equals(dto2.getName()) && t.getCompensationPercentage() == dto2.getCompensationPercentage()));
		assertTrue(timeOffRepository.findAll().stream().noneMatch(t -> Objects.equals(t.getId(), type.getId())));
	}

	@Test
	void given_validDto_and_otherValidDtoWithSameName_when_iCallPutAll_then_throwsApiException_with_statusCodeConflict() {
		var dto1 = TimeOffTypePutTestDtoFactory.builder().compensationPercentage(100f).build().asDto();
		var dto2 = TimeOffTypePutTestDtoFactory.builder().compensationPercentage(50f).build().asDto();

		var result = assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of(dto1, dto2)));
		assertEquals(HttpStatus.CONFLICT, result.getStatus());
	}

	@Test
	void given_validDtoWithNonZeroId_and_emptyDatabase_when_iCallPutAll_then_throwsApiException_with_statusNotFound() {
		var dto = TimeOffTypePutTestDtoFactory.builder().id(1L).build().asDto();

		var result = assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of(dto)));
		assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
	}

	@Test
	void given_validDtoWithMatchingId_and_validDtoWithZeroId_when_putAll_then_existingTypeIsUpdated_and_newTypeIsCreated() {
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().compensationPercentage(100f).build().asEntity());
		var dto1 = TimeOffTypePutTestDtoFactory.builder().id(type.getId()).name(type.getName()).compensationPercentage(0f).build().asDto();
		var dto2 = TimeOffTypePutTestDtoFactory.builder().id(0L).name(type.getName() + " version 2").compensationPercentage(50f).build().asDto();

		timeOffTypeService.putAll(List.of(dto1, dto2));
		assertEquals(2, timeOffTypeRepository.count());
		assertTrue(timeOffTypeRepository.findAll().stream().anyMatch(t -> t.getId() == dto1.getId() && t.getName().equals(dto1.getName()) && t.getCompensationPercentage() == dto1.getCompensationPercentage()));
		assertTrue(timeOffTypeRepository.findAll().stream().anyMatch(t -> t.getName().equals(dto2.getName()) && t.getCompensationPercentage() == dto2.getCompensationPercentage()));
	}

	@Test
	void given_emptyDtoList_and_emptyDatabase_when_putAll_then_noErrors_and_noDbUpdates() {
		assertDoesNotThrow(() -> timeOffTypeService.putAll(List.of()));
		assertEquals(0, timeOffTypeRepository.count());
	}
}
