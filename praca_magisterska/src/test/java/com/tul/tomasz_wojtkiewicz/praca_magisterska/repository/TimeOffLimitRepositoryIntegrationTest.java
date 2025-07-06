// Testy integracyjne TimeOffLimitRepository
package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Tag("integration")
// ai tag: integration
class TimeOffLimitRepositoryIntegrationTest {

	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;

	private EmployeeEntity employee;
	private TimeOffTypeEntity timeOffType;
	private TimeOffLimitEntity timeOffLimit;

	@BeforeEach
	void setup() {
		employee = new EmployeeEntity();
		employee.setEmail("employee@example.com");
		employee.setFirstName("Alice");
		employee.setLastName("Smith");
		employee.setPhoneNumber("111222333");
		employee.setAccessLevel(2);
		employeeRepository.save(employee);

		timeOffType = new TimeOffTypeEntity();
		timeOffType.setName("Vacation");
		timeOffType.setCompensationPercentage(50.0f);
		timeOffTypeRepository.save(timeOffType);

		timeOffLimit = new TimeOffLimitEntity();
		timeOffLimit.setEmployee(employee);
		timeOffLimit.setTimeOffType(timeOffType);
		timeOffLimit.setLeaveYear(2025);
		timeOffLimit.setMaxHours(100);
		timeOffLimitRepository.save(timeOffLimit);
	}

	@Test
		// CASES: 1
	void findAllByLeaveYearAndEmployeeId_ShouldReturnMatchingEntities() {
		List<TimeOffLimitEntity> results = timeOffLimitRepository.findAllByLeaveYearAndEmployeeId(2025, employee.getId());
		assertFalse(results.isEmpty());
		// INACCURACY: overall count should be checked
		assertTrue(results.stream().anyMatch(l -> l.getId().equals(timeOffLimit.getId())));
	}

	@Test
		// CASES: 1
	void findAllByLeaveYearAndEmployeeId_ShouldReturnEmptyList_WhenNoMatch() {
		List<TimeOffLimitEntity> results = timeOffLimitRepository.findAllByLeaveYearAndEmployeeId(2024, employee.getId());
		assertTrue(results.isEmpty());
	}

	@Test
		// CASES: 1
	void existsByLeaveYearAndEmployeeIdAndTimeOffTypeId_ShouldReturnTrue_WhenExists() {
		boolean exists = timeOffLimitRepository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(2025, employee.getId(), timeOffType.getId());
		assertTrue(exists);
	}

	@Test
		// CASES: 1
	void existsByLeaveYearAndEmployeeIdAndTimeOffTypeId_ShouldReturnFalse_WhenNotExists() {
		boolean exists = timeOffLimitRepository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(2024, employee.getId(), timeOffType.getId());
		assertFalse(exists);
	}
}
