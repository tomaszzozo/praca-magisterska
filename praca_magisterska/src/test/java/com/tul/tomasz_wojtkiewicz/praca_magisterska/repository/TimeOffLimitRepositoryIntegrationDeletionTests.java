package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Tag("integration")
@Tag("repository")
class TimeOffLimitRepositoryIntegrationDeletionTests {
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;
	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;
	@Autowired
	private TimeOffRepository timeOffRepository;

	@AfterEach
	void afterEach() {
		timeOffRepository.deleteAll();
		timeOffLimitRepository.deleteAll();
		timeOffTypeRepository.deleteAll();
		employeeRepository.deleteAll();
	}

	@Test
		// cases: 1
	void canNotDeleteIfDependentTimeOffsExists() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
		timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffYearlyLimit(limit).timeOffType(type).employee(employee).build().asEntity());
		assertThrows(DataIntegrityViolationException.class, () -> timeOffLimitRepository.deleteById(limit.getId()));
	}

	@Test
		// cases: 1
	void canDeleteIfNoDependentTimeOffsExists() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
		assertDoesNotThrow(() -> timeOffLimitRepository.deleteById(limit.getId()));
	}
}
