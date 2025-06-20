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

@Tag("integration")
@Tag("repository")
@DataJpaTest
class TimeOffLimitRepositoryIntegrationTests {
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;
	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;

	@Test
	void combinationOfLeaveYearTypeAndEmployeeIsUnique() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity());
		assertThrows(DataIntegrityViolationException.class, () -> timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).leaveYear(limit.getLeaveYear()).build().asEntity()));
	}

	@Test
	void otherFieldsAndCombinationsAreNotUnique() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var employee2 = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.builder().email("a" + employee.getEmail()).phoneNumber(new StringBuilder(employee.getPhoneNumber()).reverse().toString()).build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var type2 = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.builder().name(type.getName() + " second").build().asEntity());
		var limit1 = TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity();
		var limit2 = TimeOffLimitTestEntityFactory.builder().maxHours(80).employee(employee).timeOffType(type).leaveYear(limit1.getLeaveYear() + 1).build().asEntity();
		var limit3 = TimeOffLimitTestEntityFactory.builder().maxHours(LocalDate.of(limit1.getLeaveYear() + 1, 12, 31).getDayOfYear() * 24).employee(employee).timeOffType(type2).leaveYear(limit1.getLeaveYear() + 1).build().asEntity();
		var limit4 = TimeOffLimitTestEntityFactory.builder().employee(employee2).timeOffType(type2).leaveYear(limit1.getLeaveYear() + 1).build().asEntity();
		assertDoesNotThrow(() -> timeOffLimitRepository.saveAllAndFlush(List.of(limit1, limit2, limit3, limit4)));
	}

	@Test
	void timeOffsRelationWorks() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity();
		var timeOff = TimeOffTestEntityFactory.builder().hoursCount(10).employee(employee).timeOffType(type).timeOffYearlyLimit(limit).build().asEntity();
		limit.setTimeOffs(List.of(timeOff));

		timeOffLimitRepository.saveAndFlush(limit);

		var savedLimit = timeOffLimitRepository.findById(limit.getId()).orElseThrow();
		assertEquals(1, savedLimit.getTimeOffs().size());
		assertEquals(10, savedLimit.getTimeOffs().getFirst().getHoursCount());
	}
}
