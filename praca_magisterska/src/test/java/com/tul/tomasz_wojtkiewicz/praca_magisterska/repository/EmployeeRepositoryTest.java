package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Tag("integration")
// ai tag: integration
class EmployeeRepositoryTest {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
		// cases: 1
	void existsByEmail_shouldReturnTrue_whenEmailExists() {
		EmployeeEntity employee = new EmployeeEntity();
		employee.setEmail("test@example.com");
		employee.setFirstName("Jan");
		employee.setLastName("Kowalski");
		employee.setPhoneNumber("123456789");
		employee.setAccessLevel(1);
		employeeRepository.save(employee);

		boolean exists = employeeRepository.existsByEmail("test@example.com");

		assertThat(exists).isTrue();
	}

	@Test
		// cases: 1
	void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
		boolean exists = employeeRepository.existsByEmail("nonexistent@example.com");

		assertThat(exists).isFalse();
	}

	@Test
		// cases: 1
	void existsByPhoneNumber_shouldReturnTrue_whenPhoneNumberExists() {
		EmployeeEntity employee = new EmployeeEntity();
		employee.setEmail("phone@example.com");
		employee.setFirstName("Anna");
		employee.setLastName("Nowak");
		employee.setPhoneNumber("987654321");
		employee.setAccessLevel(2);
		employeeRepository.save(employee);

		boolean exists = employeeRepository.existsByPhoneNumber("987654321");

		assertThat(exists).isTrue();
	}

	@Test
		// cases: 1
	void existsByPhoneNumber_shouldReturnFalse_whenPhoneNumberDoesNotExist() {
		boolean exists = employeeRepository.existsByPhoneNumber("000000000");

		assertThat(exists).isFalse();
	}
}
