// Testy integracyjne TimeOffRepository
package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Tag("integration")
// ai tag: integration
class TimeOffRepositoryIntegrationTest {

	@Autowired
	private TimeOffRepository timeOffRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;

	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;

	private EmployeeEntity employee;
	private TimeOffTypeEntity timeOffType;
	private TimeOffLimitEntity timeOffLimit;
	private TimeOffEntity timeOff1;
	private TimeOffEntity timeOff2;

	@BeforeEach
	void setup() {
		employee = new EmployeeEntity();
		employee.setEmail("emp@example.com");
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setPhoneNumber("123456789");
		employee.setAccessLevel(1);
		employeeRepository.save(employee);

		timeOffType = new TimeOffTypeEntity();
		timeOffType.setName("Sick Leave");
		timeOffType.setCompensationPercentage(75.0f);
		timeOffTypeRepository.save(timeOffType);

		timeOffLimit = new TimeOffLimitEntity();
		timeOffLimit.setEmployee(employee);
		timeOffLimit.setTimeOffType(timeOffType);
		timeOffLimit.setLeaveYear(2024);
		timeOffLimit.setMaxHours(200);
		timeOffLimitRepository.save(timeOffLimit);

		timeOff1 = new TimeOffEntity();
		timeOff1.setEmployee(employee);
		timeOff1.setTimeOffType(timeOffType);
		timeOff1.setTimeOffYearlyLimit(timeOffLimit);
		timeOff1.setFirstDay(LocalDate.of(2024, 5, 10));
		timeOff1.setLastDayInclusive(LocalDate.of(2024, 5, 12));
		timeOff1.setHoursCount(24);
		timeOff1.setComment("Family emergency");
		timeOffRepository.save(timeOff1);

		timeOff2 = new TimeOffEntity();
		timeOff2.setEmployee(employee);
		timeOff2.setTimeOffType(timeOffType);
		timeOff2.setTimeOffYearlyLimit(timeOffLimit);
		timeOff2.setFirstDay(LocalDate.of(2023, 7, 1));
		timeOff2.setLastDayInclusive(LocalDate.of(2023, 7, 3));
		timeOff2.setHoursCount(24);
		timeOff2.setComment("Vacation");
		timeOffRepository.save(timeOff2);
	}

	@Test
	void findAllByYearAndEmployeeId_ShouldReturnOnlyTimeOffInSpecifiedYear() {
		List<TimeOffEntity> results = timeOffRepository.findAllByYearAndEmployeeId(employee.getId(), 2024);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(timeOff1.getId(), results.get(0).getId());
	}

	@Test
	void findAllByYearAndEmployeeId_ShouldReturnEmptyList_WhenNoMatches() {
		List<TimeOffEntity> results = timeOffRepository.findAllByYearAndEmployeeId(employee.getId(), 2025);
		assertNotNull(results);
		assertTrue(results.isEmpty());
	}
}
