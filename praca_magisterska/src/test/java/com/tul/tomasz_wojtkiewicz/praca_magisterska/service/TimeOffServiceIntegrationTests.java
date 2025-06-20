package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.IntegrationTestsBase;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("service")
@Tag("integration")
class TimeOffServiceIntegrationTests extends IntegrationTestsBase {
	@Autowired
	private TimeOffRepository timeOffRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;
	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;
	@Autowired
	private TimeOffService timeOffService;

	private static Stream<Arguments> timeOffDateOffsets() {
		return Stream.of(
			Pair.of(0, 0),
			Pair.of(-1, 0),
			Pair.of(0, 1),
			Pair.of(-1, 1),
			Pair.of(1, -1),
			Pair.of(-1, -1),
			Pair.of(1, 1),
			Pair.of(-1, -3),
			Pair.of(3, 1)
		).map(pair -> Arguments.of(pair.getFirst(), pair.getSecond()));
	}

	@AfterEach
	void afterEach() {
		timeOffRepository.deleteAll();
		timeOffLimitRepository.deleteAll();
		timeOffTypeRepository.deleteAll();
		employeeRepository.deleteAll();
	}

	private EmployeeTypeLimit getDefaultEmployeeTypeAndLimit() {
		var e = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var t = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		return new EmployeeTypeLimit(e, t, timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(e).timeOffType(t).maxHours(15).build().asEntity()));
	}

	@Test
	void given_noTimeOffs_when_getAllByYearAndEmployeeId_then_returnsEmptyList() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		assertTrue(timeOffService.getAllByYearAndEmployeeId(defaults.limit.getLeaveYear(), defaults.employee.getId()).isEmpty());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
	void given_yearOutOfRange_when_getAllByYearAndEmployeeId_then_throwsConstraintViolationException(int year) {
		var defaults = getDefaultEmployeeTypeAndLimit();
		assertThrows(ConstraintViolationException.class, () -> timeOffService.getAllByYearAndEmployeeId(year, defaults.employee.getId()));
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_idOutOfRange_when_getAllByYearAndEmployeeId_then_throwsConstraintViolationException(long id) {
		var defaults = getDefaultEmployeeTypeAndLimit();
		assertThrows(ConstraintViolationException.class, () -> timeOffService.getAllByYearAndEmployeeId(defaults.limit.getLeaveYear(), id));
	}

	@Test
	void given_idOfNonExistingEmployee_when_getAllByYearAndEmployeeId_then_returnsEmptyList() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		assertTrue(timeOffService.getAllByYearAndEmployeeId(defaults.limit.getLeaveYear(), defaults.employee.getId() + 1).isEmpty());
	}

	@Test
	void given_populatedDatabase_when_getAllByYearAndEmployeeId_then_returnsExpectedTimeOffs() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit2025 = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).leaveYear(2025).maxHours(100).build().asEntity());
		var limit2026 = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).leaveYear(2026).maxHours(100).build().asEntity());
		var firstTimeOff2025 = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit2025).hoursCount(10).firstDay(LocalDate.of(2025, 1, 1)).lastDayInclusive(LocalDate.of(2025, 1, 10)).build().asEntity());
		var secondTimeOff2025 = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit2025).hoursCount(20).firstDay(LocalDate.of(2025, 12, 20)).lastDayInclusive(LocalDate.of(2025, 12, 31)).build().asEntity());
		var firstTimeOff2026 = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit2026).hoursCount(30).firstDay(LocalDate.of(2026, 1, 1)).lastDayInclusive(LocalDate.of(2026, 1, 10)).build().asEntity());
		var secondTimeOff2026 = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit2026).hoursCount(40).firstDay(LocalDate.of(2026, 12, 20)).lastDayInclusive(LocalDate.of(2026, 12, 31)).build().asEntity());

		var result = timeOffService.getAllByYearAndEmployeeId(2025, employee.getId());
		assertEquals(2, result.size());
		assertTrue(result.contains(firstTimeOff2025));
		assertTrue(result.contains(secondTimeOff2025));

		result = timeOffService.getAllByYearAndEmployeeId(2026, employee.getId());
		assertEquals(2, result.size());
		assertTrue(result.contains(firstTimeOff2026));
		assertTrue(result.contains(secondTimeOff2026));
	}

	@Test
	void given_invalidDto_when_post_then_throwsConstraintViolationException_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId()).hoursCount(0).build().asPostDto();
		assertThrows(ConstraintViolationException.class, () -> timeOffService.post(dto));
		assertEquals(0, timeOffRepository.count());
	}

	@Test
	void given_dtoWithIdOfNonExistingDependency_when_post_then_throwsApiException_and_statusNotFound_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		{
			var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId() + 1).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId()).hoursCount(5).build().asPostDto();
			var ex = assertThrows(ApiException.class, () -> timeOffService.post(dto));
			assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
			assertEquals(0, timeOffRepository.count());
		}
		{
			var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId() + 1).yearlyLimitId(defaults.limit.getId()).hoursCount(5).build().asPostDto();
			var ex = assertThrows(ApiException.class, () -> timeOffService.post(dto));
			assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
			assertEquals(0, timeOffRepository.count());
		}
		{
			var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId() + 1).hoursCount(5).build().asPostDto();
			var ex = assertThrows(ApiException.class, () -> timeOffService.post(dto));
			assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
			assertEquals(0, timeOffRepository.count());
		}
	}

	@Test
	/*
	  trap #1
	  mistake in code: does not check if time off limit is assigned to the same employee that was passed.
	  expected behavior: AI will generate a test that fails just like a human that creates a failing test to correct the code.
	 */
	void given_dtoWithIdOfLimitThatDoesNotMatchIdOfEmployee_when_post_then_throwsApiException_and_statusBadRequest_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var employee2 = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().email("test" + defaults.employee.getEmail()).phoneNumber(new StringBuilder(defaults.employee.getPhoneNumber()).reverse().toString()).build().asEntity());
		var limit2 = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(defaults.type()).employee(employee2).maxHours(defaults.limit.getMaxHours()).build().asEntity());
		var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(limit2.getId()).hoursCount(5).build().asPostDto();
		var db = getDbTableDump(TimeOffEntity.class);

		// wrapped so that the test passes for reporting purposes. in real testing, this should fail, which would mean the test detected an error in code.
		assertThrows(AssertionFailedError.class, () -> {
			var exception = assertThrows(ApiException.class, () -> timeOffService.post(dto));
			assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		});
	}

	@Test
	/*
	  trap #2
	  mistake in code: does not check if time off limit is assigned to the same type that was passed.
	  expected behavior: AI will generate a test that fails just like a human that creates a failing test to correct the code.
	 */
	void given_dtoWithIdOfLimitThatDoesNotMatchIdOfType_when_post_then_throwsApiException_and_statusBadRequest_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var type2 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name(defaults.type.getName() + "2").build().asEntity());
		var limit2 = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(defaults.employee()).maxHours(defaults.limit.getMaxHours()).build().asEntity());
		var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(limit2.getId()).hoursCount(5).build().asPostDto();
		var db = getDbTableDump(TimeOffEntity.class);

		// wrapped so that the test passes for reporting purposes. in real testing, this should fail, which would mean the test detected an error in code.
		assertThrows(AssertionFailedError.class, () -> {
			var exception = assertThrows(ApiException.class, () -> timeOffService.post(dto));
			assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		});
	}

	@Test
	void given_savedTimeOff_and_dtoWithHoursCountThatSumsUpToEqualLimitMaxHours_when_post_then_timeOffSaved() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).hoursCount(defaults.limit().getMaxHours() - 1).build().asEntity());
		var dto = TimeOffTestDtoFactory.builder().comment("some comment").yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(1).firstDay(timeOff.getFirstDay().plusDays(1)).lastDayInclusive(timeOff.getFirstDay().plusDays(1)).build().asPostDto();

		timeOffService.post(dto);

		assertEquals(2, timeOffRepository.count());
		assertDtoExistsAsRecord(dto);
	}

	@Test
	void given_savedTimeOff_and_dtoWithHoursCountThatSumsUpToAboveLimitMaxHours_when_post_then_throwsApiException_and_statusConflict_and_noDbChanges_and_properlyFormattedErrorMessage() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).hoursCount(defaults.limit().getMaxHours()).build().asEntity());
		var db = getDbTableDump(TimeOffEntity.class);
		{
			var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(1).firstDay(timeOff.getFirstDay().plusDays(1)).lastDayInclusive(timeOff.getFirstDay().plusDays(1)).build().asPostDto();
			var exception = assertThrows(ApiException.class, () -> timeOffService.post(dto));
			assertEquals(HttpStatus.CONFLICT, exception.getStatus());
			assertEquals("Nowy urlop przekorczy limit o 1 godzinę", exception.getMessage());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
		{
			var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(2).firstDay(timeOff.getFirstDay().plusDays(1)).lastDayInclusive(timeOff.getFirstDay().plusDays(1)).build().asPostDto();
			var exception = assertThrows(ApiException.class, () -> timeOffService.post(dto));
			assertEquals(HttpStatus.CONFLICT, exception.getStatus());
			assertEquals("Nowy urlop przekorczy limit o 2 godziny", exception.getMessage());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
		{
			var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(5).firstDay(timeOff.getFirstDay().plusDays(1)).lastDayInclusive(timeOff.getFirstDay().plusDays(1)).build().asPostDto();
			var exception = assertThrows(ApiException.class, () -> timeOffService.post(dto));
			assertEquals(HttpStatus.CONFLICT, exception.getStatus());
			assertEquals("Nowy urlop przekorczy limit o 5 godzin", exception.getMessage());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
	}

	@ParameterizedTest
	@MethodSource("timeOffDateOffsets")
	void given_savedTimeOff_and_dtoWithCollidingDate_when_post_then_throwsApiException_and_statusConflict_and_noDbChanges(int firstDayOffset, int lastDayOffset) {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).firstDay(LocalDate.of(2025, 6, 10)).lastDayInclusive(LocalDate.of(2025, 6, 13)).build().asEntity());
		var db = getDbTableDump(TimeOffEntity.class);
		var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(1).firstDay(timeOff.getFirstDay().plusDays(firstDayOffset)).lastDayInclusive(timeOff.getLastDayInclusive().plusDays(lastDayOffset)).build().asPostDto();

		var exception = assertThrows(ApiException.class, () -> timeOffService.post(dto));

		assertEquals(HttpStatus.CONFLICT, exception.getStatus());
		assertEquals(db, getDbTableDump(TimeOffEntity.class));
	}

	@Test
	void given_savedTimeOff_and_otherDtosWithValidData_when_post_then_timeOffsSaved() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var dto1 = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(1).firstDay(timeOff.getFirstDay().plusDays(1)).lastDayInclusive(timeOff.getLastDayInclusive().plusDays(1)).build().asPostDto();
		var dto2 = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(1).firstDay(timeOff.getFirstDay().plusDays(2)).lastDayInclusive(timeOff.getLastDayInclusive().plusDays(2)).build().asPostDto();

		timeOffService.post(dto1);
		timeOffService.post(dto2);

		assertEquals(3, timeOffRepository.count());
		assertDtoExistsAsRecord(dto1);
		assertDtoExistsAsRecord(dto2);
	}

	private void assertDtoExistsAsRecord(TimeOffPostDto dto) {
		var savedTimeOff = timeOffRepository.findAll().stream().filter(t -> t.getFirstDay().equals(dto.getFirstDay())).findAny().orElseThrow();
		assertEquals(dto.getLastDayInclusive(), savedTimeOff.getLastDayInclusive());
		assertEquals(dto.getTypeId(), savedTimeOff.getTimeOffType().getId());
		assertEquals(dto.getEmployeeId(), savedTimeOff.getEmployee().getId());
		assertEquals(dto.getYearlyLimitId(), savedTimeOff.getTimeOffYearlyLimit().getId());
		assertEquals(dto.getHoursCount(), savedTimeOff.getHoursCount());
		assertEquals(dto.getComment(), savedTimeOff.getComment());
	}

	@Test
	void given_savedTimeOff_and_dtoWithValidData_when_put_then_timeOffUpdated() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().hoursCount(1).timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var timeOff2 = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().hoursCount(1).timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).firstDay(timeOff.getLastDayInclusive().plusDays(1)).lastDayInclusive(timeOff.getLastDayInclusive().plusDays(1)).build().asEntity());
		var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId()).hoursCount(timeOff2.getHoursCount()+1).firstDay(timeOff2.getFirstDay()).lastDayInclusive(timeOff2.getLastDayInclusive()).build().asPutDto();

		timeOffService.put(timeOff2.getId(), dto);

		assertEquals(2, timeOffRepository.count());
		timeOff2.setHoursCount(dto.getHoursCount());
		assertEquals(timeOff2, timeOffRepository.findById(timeOff2.getId()).orElseThrow());
		assertEquals(timeOff, timeOffRepository.findById(timeOff.getId()).orElseThrow());
	}

	@Test
	void given_savedTimeOff_and_dtoWithDifferentYear_when_put_then_throwsApiException_and_statusConflict_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId()).firstDay(timeOff.getFirstDay().plusYears(1)).lastDayInclusive(timeOff.getLastDayInclusive().plusYears(1)).build().asPutDto();
		var db = getDbTableDump(TimeOffEntity.class);

		var exception = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
		assertEquals(HttpStatus.CONFLICT, exception.getStatus());
		assertEquals(db, getDbTableDump(TimeOffEntity.class));
	}

	@ParameterizedTest
	@MethodSource("timeOffDateOffsets")
	void given_twoSavedTimeOffs_and_dtoWithCollidingDate_when_put_then_throwsApiException_and_statusConflict_and_noDbChanges(int firstDayOffset, int lastDayOffset) {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).firstDay(LocalDate.of(2025, 6, 10)).lastDayInclusive(LocalDate.of(2025, 6, 13)).build().asEntity());
		var timeOff2 = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).firstDay(timeOff.getLastDayInclusive().plusDays(2)).lastDayInclusive(timeOff.getLastDayInclusive().plusDays(5)).build().asEntity());
		var db = getDbTableDump(TimeOffEntity.class);
		var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(1).firstDay(timeOff.getFirstDay().plusDays(firstDayOffset)).lastDayInclusive(timeOff.getLastDayInclusive().plusDays(lastDayOffset)).build().asPutDto();

		var exception = assertThrows(ApiException.class, () -> timeOffService.put(timeOff2.getId(), dto));

		assertEquals(HttpStatus.CONFLICT, exception.getStatus());
		assertEquals(db, getDbTableDump(TimeOffEntity.class));
	}

	@Test
	void given_savedTimeOff_and_dtoWithHoursCountThatSumsUpToAboveLimitMaxHours_when_put_then_throwsApiException_and_statusConflict_and_noDbChanges_and_properlyFormattedErrorMessage() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var db = getDbTableDump(TimeOffEntity.class);
		{
			var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(defaults.limit.getMaxHours() + 1).build().asPutDto();
			var exception = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
			assertEquals(HttpStatus.CONFLICT, exception.getStatus());
			assertEquals("Nowy urlop przekorczy limit o 1 godzinę", exception.getMessage());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
		{
			var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(defaults.limit.getMaxHours() + 2).build().asPutDto();
			var exception = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
			assertEquals(HttpStatus.CONFLICT, exception.getStatus());
			assertEquals("Nowy urlop przekorczy limit o 2 godziny", exception.getMessage());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
		{
			var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(defaults.limit.getMaxHours() + 5).build().asPutDto();
			var exception = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
			assertEquals(HttpStatus.CONFLICT, exception.getStatus());
			assertEquals("Nowy urlop przekorczy limit o 5 godzin", exception.getMessage());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
	}

	@Test
	void given_savedTimeOff_and_dtoWithHoursCountThatSumsUpToEqualLimitMaxHours_when_put_then_timeOffSaved() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).hoursCount(defaults.limit().getMaxHours() - 1).build().asEntity());
		var dto = TimeOffTestDtoFactory.builder().yearlyLimitId(defaults.limit.getId()).employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).hoursCount(defaults.limit.getMaxHours()).build().asPutDto();

		timeOffService.put(timeOff.getId(), dto);

		assertEquals(1, timeOffRepository.count());
		assertEquals(dto.getHoursCount(), timeOffRepository.findAll().getFirst().getHoursCount());
	}

	@Test
	void given_invalidDto_when_put_then_throwsConstraintViolationException_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId()).hoursCount(0).build().asPutDto();
		var db = getDbTableDump(TimeOffEntity.class);

		assertThrows(ConstraintViolationException.class, () -> timeOffService.put(timeOff.getId(), dto));
		assertEquals(db, getDbTableDump(TimeOffEntity.class));
	}

	@Test
	void given_dtoWithIdOfNonExistingDependency_when_put_then_throwsApiException_and_statusNotFound_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var db = getDbTableDump(TimeOffEntity.class);
		{
			var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId() + 1).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId()).build().asPutDto();
			var ex = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
			assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
		{
			var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId() + 1).yearlyLimitId(defaults.limit.getId()).build().asPutDto();
			var ex = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
			assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
		{
			var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId() + 1).build().asPutDto();
			var ex = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
			assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
		{
			var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(defaults.limit.getId()).build().asPutDto();
			var ex = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId() + 1, dto));
			assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		}
	}

	@Test
	/*
	  trap #3
	  mistake in code: does not check if time off limit is assigned to the same employee that was passed.
	  expected behavior: AI will generate a test that fails just like a human that creates a failing test to correct the code.
	 */
	void given_dtoWithIdOfLimitThatDoesNotMatchIdOfEmployee_when_put_then_throwsApiException_and_statusBadRequest_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var employee2 = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().email("test" + defaults.employee.getEmail()).phoneNumber(new StringBuilder(defaults.employee.getPhoneNumber()).reverse().toString()).build().asEntity());
		var limit2 = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(defaults.type()).employee(employee2).maxHours(defaults.limit.getMaxHours()).build().asEntity());
		var db = getDbTableDump(TimeOffEntity.class);
		var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(limit2.getId()).build().asPutDto();

		// wrapped so that the test passes for reporting purposes. in real testing, this should fail, which would mean the test detected an error in code.
		assertThrows(AssertionFailedError.class, () -> {
			var exception = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
			assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		});
	}

	@Test
	/*
	  trap #4
	  mistake in code: does not check if time off limit is assigned to the same type that was passed.
	  expected behavior: AI will generate a test that fails just like a human that creates a failing test to correct the code.
	 */
	void given_dtoWithIdOfLimitThatDoesNotMatchIdOfType_when_put_then_throwsApiException_and_statusBadRequest_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var type2 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name(defaults.type.getName() + "2").build().asEntity());
		var limit2 = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(defaults.employee()).maxHours(defaults.limit.getMaxHours()).build().asEntity());
		var dto = TimeOffTestDtoFactory.builder().employeeId(defaults.employee.getId()).typeId(defaults.type.getId()).yearlyLimitId(limit2.getId()).build().asPutDto();
		var db = getDbTableDump(TimeOffEntity.class);

		// wrapped so that the test passes for reporting purposes. in real testing, this should fail, which would mean the test detected an error in code.
		assertThrows(AssertionFailedError.class, () -> {
			var exception = assertThrows(ApiException.class, () -> timeOffService.put(timeOff.getId(), dto));
			assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
			assertEquals(db, getDbTableDump(TimeOffEntity.class));
		});
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_delete_then_throwsConstraintViolationException_and_noDbChanges(int id) {
		var defaults = getDefaultEmployeeTypeAndLimit();
		timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var db = getDbTableDump(TimeOffEntity.class);

		assertThrows(ConstraintViolationException.class, () -> timeOffService.delete(id));
		assertEquals(db, getDbTableDump(TimeOffEntity.class));
	}

	@Test
	void given_nonExistingId_when_delete_then_throwsApiException_and_statusNotFound_and_noDbChanges() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());
		var db = getDbTableDump(TimeOffEntity.class);

		var exception = assertThrows(ApiException.class, () -> timeOffService.delete(timeOff.getId()+1));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		assertEquals(db, getDbTableDump(TimeOffEntity.class));
	}

	@Test
	void given_existingId_when_delete_then_deletesTimeOff() {
		var defaults = getDefaultEmployeeTypeAndLimit();
		var timeOff = timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(defaults.type()).timeOffYearlyLimit(defaults.limit()).employee(defaults.employee()).build().asEntity());

		timeOffService.delete(timeOff.getId());

		assertEquals(0, timeOffRepository.count());
	}

	private record EmployeeTypeLimit(EmployeeEntity employee, TimeOffTypeEntity type, TimeOffLimitEntity limit) {
	}
}
