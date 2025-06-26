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

@Tag("integration")
@Tag("repository")
@SpringBootTest
class EmployeeRepositoryIntegrationDeletionTests {
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
	void canNotDeleteIfDependentTimeOffLimitExists() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
		assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.deleteById(employee.getId()));
	}

	@Test
		// cases: 1
	void canNotDeleteIfDependentTimeOffExists() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
		var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
		timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().timeOffType(type).timeOffYearlyLimit(limit).employee(employee).build().asEntity());
		assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.deleteById(employee.getId()));
	}

	@Test
		// cases: 1
	void canDeleteEntityIfNoDependentEntitiesExist() {
		var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
		assertDoesNotThrow(() -> employeeRepository.deleteById(employee.getId()));
	}
}
