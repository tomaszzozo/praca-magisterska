package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.IntegrationTestsBase;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitPutTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
@Tag("service")
class TimeOffLimitServiceIntegrationTests extends IntegrationTestsBase {
	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;
	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;
	@Autowired
	private TimeOffLimitService timeOffLimitService;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private TimeOffRepository timeOffRepository;

	@AfterEach
	void afterEach() {
		timeOffRepository.deleteAll();
		timeOffLimitRepository.deleteAll();
		timeOffTypeRepository.deleteAll();
		employeeRepository.deleteAll();
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_getById_then_throwsConstraintViolationException(int invalidId) {
		assertThrows(ConstraintViolationException.class, () -> timeOffLimitService.getById(invalidId));
	}

	@Test
	void given_validId_and_savedEntityWithDifferentId_when_getById_then_throwsApiException_and_statusCodeIsNotFound() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity());
		var exception = assertThrows(ApiException.class, () -> timeOffLimitService.getById(limit.getId() + 1));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	void given_validId_and_savedEntityWithGivenId_when_iCallGetById_then_savedEntityIsReturned() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity());
		var result = timeOffLimitService.getById(limit.getId());
		assertEquals(limit, result);
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_and_validYear_when_getAllByYearAndEmployeeId_then_throwsConstraintViolationException(int invalidId) {
		assertThrows(ConstraintViolationException.class, () -> timeOffLimitService.getAllByYearAndEmployeeId(2030, invalidId));
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
	void given_validId_and_invalidYear_when_getAllByYearAndEmployeeId_then_throwsConstraintViolationException(int invalidYear) {
		assertThrows(ConstraintViolationException.class, () -> timeOffLimitService.getAllByYearAndEmployeeId(invalidYear, 1));
	}

	@Test
	void given_validInput_and_noSavedEmployee_when_getAllByYearAndEmployeeId_then_throwsApiException_with_statusCodeNotFound() {
		var exception = assertThrows(ApiException.class, () -> timeOffLimitService.getAllByYearAndEmployeeId(2030, 1));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	void given_validInput_and_savedEmployee_and_noSavedTypes_when_getAllByYearAndEmployeeId_then_returnsEmptyList() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		assertTrue(timeOffLimitService.getAllByYearAndEmployeeId(2030, employee.getId()).isEmpty());
	}

	@Test
	void given_validInput_and_savedEmployee_and_savedType_and_noSavedLimits_when_getAllByYearAndEmployeeId_then_returnsListWithDefaultLimit() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());

		var result = timeOffLimitService.getAllByYearAndEmployeeId(2030, employee.getId());
		assertEquals(1, result.size());
		assertEquals(type, result.getFirst().getTimeOffType());
		assertEquals(employee, result.getFirst().getEmployee());
		assertEquals(2030, result.getFirst().getLeaveYear());
		assertEquals(TimeOffLimitEntity.DEFAULT_MAX_HOURS, result.getFirst().getMaxHours());
	}

	@Test
	void given_validInput_and_savedLimit_when_getAllByYearAndEmployeeId_then_returnsListWithExistingLimit() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).maxHours(TimeOffLimitEntity.DEFAULT_MAX_HOURS + 1).build().asEntity());

		var result = timeOffLimitService.getAllByYearAndEmployeeId(limit.getLeaveYear(), employee.getId());
		assertEquals(1, result.size());
		assertEquals(limit, result.getFirst());
	}

	@Test
	void given_validInput_and_savedLimit_and_twoSavedTypes_when_getAllByYearAndEmployeeId_then_returnsListWithExistingAndDefaultLimit() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type1 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var type2 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name(type1.getName() + " version 2").build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type1).maxHours(TimeOffLimitEntity.DEFAULT_MAX_HOURS + 1).build().asEntity());

		var result = timeOffLimitService.getAllByYearAndEmployeeId(limit.getLeaveYear(), employee.getId());
		assertEquals(2, result.size());
		assertTrue(result.contains(limit));
		result.remove(limit);

		assertEquals(type2, result.getFirst().getTimeOffType());
		assertEquals(employee, result.getFirst().getEmployee());
		assertEquals(limit.getLeaveYear(), result.getFirst().getLeaveYear());
		assertEquals(TimeOffLimitEntity.DEFAULT_MAX_HOURS, result.getFirst().getMaxHours());
	}

	@Test
	void given_emptyList_and_savedLimit_when_putAll_then_noChanges() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).maxHours(TimeOffLimitEntity.DEFAULT_MAX_HOURS + 1).build().asEntity());
		var dbLimitsBefore = getDbTableDump(TimeOffLimitEntity.class);

		assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of()));
		assertEquals(dbLimitsBefore, getDbTableDump(TimeOffLimitEntity.class));
	}

	@Test
	void given_emptyRepository_and_validDtoWithId0_and_savedType_when_putAll_then_limitIsCreated() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var dto = TimeOffLimitPutTestDtoFactory.builder().employeeId(employee.getId()).typeId(type.getId()).build().asDto();

		assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto)));

		assertEquals(1, timeOffLimitRepository.count());
		var result = timeOffLimitRepository.findAll().getFirst();
		assertEquals(dto.getMaxHours(), result.getMaxHours());
		assertEquals(dto.getYear(), result.getLeaveYear());
		assertEquals(dto.getEmployeeId(), employee.getId());
		assertEquals(dto.getTypeId(), result.getTimeOffType().getId());
	}

	@Test
	void given_savedLimit_and_validDtoWithIdOfSavedLimit_when_putAll_then_onlyMaxHoursIsUpdated() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity());
		var dto = TimeOffLimitPutTestDtoFactory.builder().employeeId(employee.getId()).typeId(type.getId()).id(limit.getId()).maxHours(limit.getMaxHours() + 1).year(limit.getLeaveYear() + 1).build().asDto();

		assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto)));

		assertEquals(1, timeOffLimitRepository.count());
		var result = timeOffLimitRepository.findAll().getFirst();
		assertEquals(dto.getMaxHours(), result.getMaxHours());
		assertEquals(limit.getLeaveYear(), result.getLeaveYear());
		assertEquals(dto.getEmployeeId(), employee.getId());
		assertEquals(dto.getTypeId(), result.getTimeOffType().getId());
	}

	@Test
	void given_validDtoWithIdOfSavedLimit_and_invalidDtoWithId0_when_putAll_then_throwsConstraintViolationException_and_noChanges() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type1 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name("type 1").build().asEntity());
		var type2 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name("type 2").build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type1).build().asEntity());
		var dbLimitBefore = getDbTableDump(TimeOffLimitEntity.class);
		var dto1 = TimeOffLimitPutTestDtoFactory.builder().id(limit.getId()).employeeId(employee.getId()).typeId(type1.getId()).maxHours(limit.getMaxHours() + 1).build().asDto();
		var dto2 = TimeOffLimitPutTestDtoFactory.builder().id(0L).employeeId(employee.getId()).typeId(type2.getId()).maxHours(-1).build().asDto();

		assertThrows(ConstraintViolationException.class, () -> timeOffLimitService.putAll(List.of(dto1, dto2)));
		assertEquals(dbLimitBefore, getDbTableDump(TimeOffLimitEntity.class));
	}

	@Test
	void given_validDtoWithId0_and_invalidDtoWithIdOfSavedLimit_when_putAll_then_throwsConstraintViolationException_and_noChanges() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type1 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name("type 1").build().asEntity());
		var type2 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name("type 2").build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type2).maxHours(TimeOffLimitEntity.DEFAULT_MAX_HOURS + 1).build().asEntity());
		var dbLimitBefore = getDbTableDump(TimeOffLimitEntity.class);

		var dto1 = TimeOffLimitPutTestDtoFactory.builder().id(0L).employeeId(employee.getId()).typeId(type1.getId()).maxHours(limit.getMaxHours() + 1).build().asDto();
		var dto2 = TimeOffLimitPutTestDtoFactory.builder().id(limit.getId()).employeeId(employee.getId()).typeId(type2.getId()).maxHours(-1).build().asDto();
		assertThrows(ConstraintViolationException.class, () -> timeOffLimitService.putAll(List.of(dto1, dto2)));
		assertEquals(dbLimitBefore, getDbTableDump(TimeOffLimitEntity.class));
	}

	@Test
	void given_validDtoWithNonZeroId_and_noSavedLimits_when_putAll_then_throwsApiException_and_statusCodeIsNotFound_and_noUpdates() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var dto = TimeOffLimitPutTestDtoFactory.builder().id(1L).employeeId(employee.getId()).typeId(type.getId()).build().asDto();

		var exception = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto)));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		assertEquals(0, timeOffLimitRepository.count());
	}

	@Test
	void given_validDtoWithIdOfSavedLimit_and_validDtoWithId0YearEmployeeAndTimeOffTypeOfSavedLimit_when_putAll_then_throwsApiException_and_statusCodeIsConflict_and_noUpdates() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).maxHours(30).build().asEntity());
		var dbLimitBefore = getDbTableDump(TimeOffLimitEntity.class);
		var dto = TimeOffLimitPutTestDtoFactory.builder().id(limit.getId()).employeeId(employee.getId()).typeId(type.getId()).maxHours(31).build().asDto();
		var dto2 = TimeOffLimitPutTestDtoFactory.builder().id(0L).employeeId(employee.getId()).typeId(type.getId()).year(dto.getYear()).maxHours(32).build().asDto();

		var exception = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto, dto2)));

		assertEquals(HttpStatus.CONFLICT, exception.getStatus());
		assertEquals(dbLimitBefore, getDbTableDump(TimeOffLimitEntity.class));
	}

	@Test
	void given_validDtoWithIdOfSavedLimit_and_validDtoWithIdOfSavedLimitAndNewMaxHoursLessThanInSavedTimeOff_when_putAll_then_throwsApiException_and_statusCodeIsConflict_and_noUpdates() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name("type 1").build().asEntity());
		var type2 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name("type 2").build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).maxHours(3).build().asEntity());
		var limit2 = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(employee).maxHours(4).build().asEntity());
		timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type2).timeOffYearlyLimit(limit2).hoursCount(limit2.getMaxHours()).build().asEntity());
		var dbLimitsBefore = getDbTableDump(TimeOffLimitEntity.class);
		var dbTimeOffsBefore = getDbTableDump(TimeOffEntity.class);
		var dto = TimeOffLimitPutTestDtoFactory.builder().id(limit.getId()).employeeId(employee.getId()).typeId(type.getId()).maxHours(5).build().asDto();
		var dto2 = TimeOffLimitPutTestDtoFactory.builder().id(limit2.getId()).employeeId(employee.getId()).typeId(type2.getId()).maxHours(limit2.getMaxHours() - 1).build().asDto();

		var exception = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto, dto2)));

		assertEquals(HttpStatus.CONFLICT, exception.getStatus());
		assertEquals(dbLimitsBefore, getDbTableDump(TimeOffLimitEntity.class));
		assertEquals(dbTimeOffsBefore, getDbTableDump(TimeOffEntity.class));
	}

	@Test
	void given_twoSavedLimits_and_validDtoWithIdOfFirstSavedLimitChangedMaxHoursButYearEmployeeAndTimeOffTypeOfSecondSavedLimit_when_putAll_then_onlyYearOfFirstSavedLimitIsChanged() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name("type 1").compensationPercentage(10f).build().asEntity());
		var type2 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name("type 2").compensationPercentage(20f).build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).maxHours(3).build().asEntity());
		var limit2 = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(employee).maxHours(4).build().asEntity());
		var dto = TimeOffLimitPutTestDtoFactory.builder().id(limit.getId()).employeeId(employee.getId()).typeId(type2.getId()).maxHours(5).build().asDto();

		assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto)));

		assertEquals(limit2, timeOffLimitRepository.findById(limit2.getId()).orElseThrow());
		var updatedLimit = timeOffLimitRepository.findById(limit.getId()).orElseThrow();
		assertEquals(5, updatedLimit.getMaxHours());
		assertEquals(type.getId(), updatedLimit.getTimeOffType().getId());
	}

	@Test
	void given_twoDtosWithSameDataAndId0_when_putAll_then_throwsApiException_and_statusCodeConflict_and_noDbChanges() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var dto = TimeOffLimitPutTestDtoFactory.builder().id(0L).employeeId(employee.getId()).typeId(type.getId()).build().asDto();
		var dbBefore = getDbTableDump(TimeOffLimitEntity.class);

		var result = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto, dto)));
		assertEquals(HttpStatus.CONFLICT, result.getStatus());
		assertEquals(dbBefore, getDbTableDump(TimeOffLimitEntity.class));
	}

	@Test
	void given_twoDtosWithSameDataAndSameExistingId_when_putAll_then_secondDtoUsedToUpdateLimit() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
		var dto1 = TimeOffLimitPutTestDtoFactory.builder().id(limit.getId()).employeeId(employee.getId()).typeId(type.getId()).maxHours(limit.getMaxHours()+1).build().asDto();
		var dto2 = TimeOffLimitPutTestDtoFactory.builder().id(limit.getId()).employeeId(employee.getId()).typeId(type.getId()).maxHours(limit.getMaxHours()+2).build().asDto();

		assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto1, dto2)));
		assertEquals(1, timeOffLimitRepository.count());
		var result = timeOffLimitRepository.findAll().getFirst();
		assertEquals(dto2.getMaxHours(), result.getMaxHours());
	}

	@Test
	void given_dtoWithNotExistingEmployeeId_when_putAll_then_throwsApiException_and_statusCodeIsNotFound() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var dto = TimeOffLimitPutTestDtoFactory.builder().id(0L).employeeId(employee.getId()+1).typeId(type.getId()).build().asDto();

		var exception = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto)));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	void given_dtoWithNotExistingTypeId_when_putAll_then_throwsApiException_and_statusCodeIsNotFound() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var dto = TimeOffLimitPutTestDtoFactory.builder().id(0L).employeeId(employee.getId()).typeId(type.getId()+1).build().asDto();

		var exception = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto)));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	void given_dtoWithExistingLimitIdButNonExistingEmployeeAndTypeId_when_putAll_then_dtoIsUpdated() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
		var dto = TimeOffLimitPutTestDtoFactory.builder().id(limit.getId()).employeeId(employee.getId()+1).typeId(type.getId()+1).maxHours(limit.getMaxHours()+1).build().asDto();

		assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto)));
		assertEquals(1, timeOffLimitRepository.count());
		var result = timeOffLimitRepository.findAll().getFirst();
		assertEquals(dto.getMaxHours(), result.getMaxHours());
		assertEquals(employee.getId(), result.getEmployee().getId());
		assertEquals(type.getId(), result.getTimeOffType().getId());
	}
}
