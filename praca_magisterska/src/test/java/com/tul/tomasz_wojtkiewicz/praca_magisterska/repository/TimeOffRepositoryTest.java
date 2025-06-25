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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Tag("integration")
// ai tag: integration
class TimeOffRepositoryTest {

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

	@BeforeEach
	void setUp() {
		employee = new EmployeeEntity();
		employee.setFirstName("Jan");
		employee.setLastName("Kowalski");
		employee.setEmail("jan@example.com");
		employee.setPhoneNumber("111222333");
		employee.setAccessLevel(1);
		employeeRepository.save(employee);

		timeOffType = new TimeOffTypeEntity();
		timeOffType.setName("Sick Leave");
		timeOffType.setCompensationPercentage(80.0f);
		timeOffTypeRepository.save(timeOffType);

		timeOffLimit = new TimeOffLimitEntity();
		timeOffLimit.setLeaveYear(2025);
		timeOffLimit.setMaxHours(120);
		timeOffLimit.setEmployee(employee);
		timeOffLimit.setTimeOffType(timeOffType);
		timeOffLimitRepository.save(timeOffLimit);
	}

	@Test
		// cases: 1
	void findAllByYearAndEmployeeId_shouldReturnCorrectTimeOffs() {
		TimeOffEntity timeOff = new TimeOffEntity();
		timeOff.setFirstDay(LocalDate.of(2025, 3, 10));
		timeOff.setLastDayInclusive(LocalDate.of(2025, 3, 12));
		timeOff.setHoursCount(24);
		timeOff.setComment("Sick days");
		timeOff.setEmployee(employee);
		timeOff.setTimeOffType(timeOffType);
		timeOff.setTimeOffYearlyLimit(timeOffLimit);
		timeOffRepository.save(timeOff);

		List<TimeOffEntity> results = timeOffRepository.findAllByYearAndEmployeeId(employee.getId(), 2025);

		assertThat(results).hasSize(1);
		assertThat(results.get(0).getComment()).isEqualTo("Sick days");
	}

	@Test
		// cases: 1
	void findAllByYearAndEmployeeId_shouldReturnEmptyList_whenNoTimeOffsInYear() {
		List<TimeOffEntity> results = timeOffRepository.findAllByYearAndEmployeeId(employee.getId(), 2024);

		assertThat(results).isEmpty();
	}
}
