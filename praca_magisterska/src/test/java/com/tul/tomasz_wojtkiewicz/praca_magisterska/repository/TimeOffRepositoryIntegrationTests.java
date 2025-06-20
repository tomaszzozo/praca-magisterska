package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Tag("repository")
@Tag("integration")
class TimeOffRepositoryIntegrationTests {
	@Autowired
	private TimeOffRepository timeOffRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;
	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;

	@Test
	void given_twoTimeOffsWithMatchingFirstDayAndEmployee_when_saveAll_then_throwsDataIntegrityViolationException() {
		var employee = employeeRepository.save(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.save(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).maxHours(100).build().asEntity());
		var timeOff1 = TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit).build().asEntity();
		var timeOff2 = TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit).lastDayInclusive(timeOff1.getLastDayInclusive().plusDays(1)).build().asEntity();
		assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.saveAll(List.of(timeOff1, timeOff2)));
	}

	@Test
	void given_twoTimeOffsWithMatchingLastDayAndEmployee_when_saveAll_then_throwsDataIntegrityViolationException() {
		var employee = employeeRepository.save(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.save(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).maxHours(100).build().asEntity());
		var timeOff1 = TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit).build().asEntity();
		var timeOff2 = TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit).firstDay(timeOff1.getFirstDay().minusDays(1)).build().asEntity();
		assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.saveAll(List.of(timeOff1, timeOff2)));
	}

	@Test
	void given_twoTimeOffsWithMatchingNonUniqueFields_and_differentUniqueFields_when_saveAll_then_timeOffsSaved() {
		var employee = employeeRepository.save(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.save(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).maxHours(100).build().asEntity());
		var timeOff1 = TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit).build().asEntity();
		var timeOff2 = TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit).firstDay(timeOff1.getLastDayInclusive().plusDays(1)).lastDayInclusive(timeOff1.getLastDayInclusive().plusDays(1)).build().asEntity();

		assertDoesNotThrow(() -> timeOffRepository.saveAll(List.of(timeOff1, timeOff2)));
	}

	@Test
	void given_someTimeOffs_when_findAllByYearAndEmployeeId_then_returnsExpectedEntities() {
		var employee1 = employeeRepository.save(EmployeeTestEntityFactory.builder().email("employee1@test.com").phoneNumber("111111112").build().asEntity());
		var employee2 = employeeRepository.save(EmployeeTestEntityFactory.builder().email("employee2@test.com").phoneNumber("111111113").build().asEntity());

		var type1 = timeOffTypeRepository.save(TimeOffTypeTestEntityFactory.builder().name("type 1").build().asEntity());
		var type2 = timeOffTypeRepository.save(TimeOffTypeTestEntityFactory.builder().name("type 2").build().asEntity());

		var limitE1T12025 = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().timeOffType(type1).employee(employee1).leaveYear(2025).maxHours(100).build().asEntity());
		var limitE1T12026 = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().timeOffType(type1).employee(employee1).leaveYear(2026).maxHours(100).build().asEntity());
		var limitE1T22025 = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(employee1).leaveYear(2025).maxHours(100).build().asEntity());
		var limitE1T22026 = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(employee1).leaveYear(2026).maxHours(100).build().asEntity());
		var limitE2T12025 = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().timeOffType(type1).employee(employee2).leaveYear(2025).maxHours(100).build().asEntity());
		var limitE2T12026 = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().timeOffType(type1).employee(employee2).leaveYear(2026).maxHours(100).build().asEntity());
		var limitE2T22025 = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(employee2).leaveYear(2025).maxHours(100).build().asEntity());
		var limitE2T22026 = timeOffLimitRepository.save(TimeOffLimitTestEntityFactory.builder().timeOffType(type2).employee(employee2).leaveYear(2026).maxHours(100).build().asEntity());

		var timeOffE1T12025 = timeOffRepository.save(TimeOffTestEntityFactory.builder().timeOffType(type1).employee(employee1).timeOffYearlyLimit(limitE1T12025).hoursCount(1).firstDay(LocalDate.of(2025, 6, 10)).lastDayInclusive(LocalDate.of(2025, 6, 10)).build().asEntity());
		var timeOffE1T12026 = timeOffRepository.save(TimeOffTestEntityFactory.builder().timeOffType(type1).employee(employee1).timeOffYearlyLimit(limitE1T12026).hoursCount(1).firstDay(LocalDate.of(2026, 6, 10)).lastDayInclusive(LocalDate.of(2026, 6, 10)).build().asEntity());
		var timeOffE1T22025 = timeOffRepository.save(TimeOffTestEntityFactory.builder().timeOffType(type2).employee(employee1).timeOffYearlyLimit(limitE1T22025).hoursCount(1).firstDay(LocalDate.of(2025, 6, 12)).lastDayInclusive(LocalDate.of(2025, 6, 12)).build().asEntity());
		var timeOffE1T22026 = timeOffRepository.save(TimeOffTestEntityFactory.builder().timeOffType(type2).employee(employee1).timeOffYearlyLimit(limitE1T22026).hoursCount(1).firstDay(LocalDate.of(2026, 6, 12)).lastDayInclusive(LocalDate.of(2026, 6, 12)).build().asEntity());
		var timeOffE2T12025 = timeOffRepository.save(TimeOffTestEntityFactory.builder().timeOffType(type1).employee(employee2).timeOffYearlyLimit(limitE2T12025).hoursCount(1).firstDay(LocalDate.of(2025, 6, 10)).lastDayInclusive(LocalDate.of(2025, 6, 10)).build().asEntity());
		var timeOffE2T12026 = timeOffRepository.save(TimeOffTestEntityFactory.builder().timeOffType(type1).employee(employee2).timeOffYearlyLimit(limitE2T12026).hoursCount(1).firstDay(LocalDate.of(2026, 6, 10)).lastDayInclusive(LocalDate.of(2026, 6, 10)).build().asEntity());
		var timeOffE2T22025 = timeOffRepository.save(TimeOffTestEntityFactory.builder().timeOffType(type2).employee(employee2).timeOffYearlyLimit(limitE2T22025).hoursCount(1).firstDay(LocalDate.of(2025, 6, 12)).lastDayInclusive(LocalDate.of(2025, 6, 12)).build().asEntity());
		var timeOffE2T22026 = timeOffRepository.save(TimeOffTestEntityFactory.builder().timeOffType(type2).employee(employee2).timeOffYearlyLimit(limitE2T22026).hoursCount(1).firstDay(LocalDate.of(2026, 6, 12)).lastDayInclusive(LocalDate.of(2026, 6, 12)).build().asEntity());

		{
			var result = timeOffRepository.findAllByYearAndEmployeeId(employee1.getId(), 2025);
			assertEquals(2, result.size());
			assertTrue(result.containsAll(List.of(timeOffE1T12025, timeOffE1T22025)));
		}
		{
			var result = timeOffRepository.findAllByYearAndEmployeeId(employee1.getId(), 2026);
			assertEquals(2, result.size());
			assertTrue(result.containsAll(List.of(timeOffE1T12026, timeOffE1T22026)));
		}
		{
			var result = timeOffRepository.findAllByYearAndEmployeeId(employee2.getId(), 2025);
			assertEquals(2, result.size());
			assertTrue(result.containsAll(List.of(timeOffE2T12025, timeOffE2T22025)));
		}
		{
			var result = timeOffRepository.findAllByYearAndEmployeeId(employee2.getId(), 2026);
			assertEquals(2, result.size());
			assertTrue(result.containsAll(List.of(timeOffE2T12026, timeOffE2T22026)));
		}
	}
}
