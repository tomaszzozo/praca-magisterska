// Testy integracyjne EmployeeRepository
package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Tag("integration")
// ai tag: integration
class EmployeeRepositoryIntegrationTest {

	@Autowired
	private EmployeeRepository employeeRepository;

	private EmployeeEntity employee;

	@BeforeEach
	void setup() {
		employee = new EmployeeEntity();
		employee.setEmail("test@example.com");
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setPhoneNumber("123456789");
		employee.setAccessLevel(1);
		employeeRepository.save(employee);
	}

	@Test
	void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
		assertTrue(employeeRepository.existsByEmail("test@example.com"));
	}

	@Test
	void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
		assertFalse(employeeRepository.existsByEmail("notfound@example.com"));
	}

	@Test
	void existsByPhoneNumber_ShouldReturnTrue_WhenPhoneNumberExists() {
		assertTrue(employeeRepository.existsByPhoneNumber("123456789"));
	}

	@Test
	void existsByPhoneNumber_ShouldReturnFalse_WhenPhoneNumberDoesNotExist() {
		assertFalse(employeeRepository.existsByPhoneNumber("987654321"));
	}
}
