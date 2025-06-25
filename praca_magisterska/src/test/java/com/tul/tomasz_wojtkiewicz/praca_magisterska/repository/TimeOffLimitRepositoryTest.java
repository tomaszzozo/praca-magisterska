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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Tag("integration")
// ai tag: integration
class TimeOffLimitRepositoryTest {

	@Autowired
	private TimeOffLimitRepository timeOffLimitRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;

	private EmployeeEntity employee;
	private TimeOffTypeEntity type;

	@BeforeEach
	void setUp() {
		employee = new EmployeeEntity();
		employee.setFirstName("Adam");
		employee.setLastName("Nowak");
		employee.setEmail("adam@example.com");
		employee.setPhoneNumber("123123123");
		employee.setAccessLevel(1);
		employeeRepository.save(employee);

		type = new TimeOffTypeEntity();
		type.setName("Vacation");
		type.setCompensationPercentage(100.0f);
		timeOffTypeRepository.save(type);
	}

	@Test
	void findAllByLeaveYearAndEmployeeId_shouldReturnMatchingEntities() {
		TimeOffLimitEntity limit = new TimeOffLimitEntity();
		limit.setLeaveYear(2025);
		limit.setMaxHours(160);
		limit.setEmployee(employee);
		limit.setTimeOffType(type);
		timeOffLimitRepository.save(limit);

		List<TimeOffLimitEntity> results = timeOffLimitRepository.findAllByLeaveYearAndEmployeeId(2025, employee.getId());

		assertThat(results).hasSize(1);
		assertThat(results.get(0).getMaxHours()).isEqualTo(160);
	}

	@Test
	void existsByLeaveYearAndEmployeeIdAndTimeOffTypeId_shouldReturnTrue_whenRecordExists() {
		TimeOffLimitEntity limit = new TimeOffLimitEntity();
		limit.setLeaveYear(2025);
		limit.setMaxHours(120);
		limit.setEmployee(employee);
		limit.setTimeOffType(type);
		timeOffLimitRepository.save(limit);

		boolean exists = timeOffLimitRepository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(
			2025, employee.getId(), type.getId()
		);

		assertThat(exists).isTrue();
	}

	@Test
	void existsByLeaveYearAndEmployeeIdAndTimeOffTypeId_shouldReturnFalse_whenNoRecordExists() {
		boolean exists = timeOffLimitRepository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(
			2024, 999L, 999L
		);

		assertThat(exists).isFalse();
	}
}
