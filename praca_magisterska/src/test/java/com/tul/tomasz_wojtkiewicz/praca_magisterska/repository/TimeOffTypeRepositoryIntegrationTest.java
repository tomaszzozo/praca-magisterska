// Testy integracyjne TimeOffTypeRepository
package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Tag("integration")
// ai tag: unit
class TimeOffTypeRepositoryIntegrationTest {

	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;

	@Test
	void existsByName_ShouldReturnTrue_WhenEntityWithNameExists() {
		TimeOffTypeEntity entity = new TimeOffTypeEntity();
		entity.setName("Holiday");
		entity.setCompensationPercentage(100f);
		timeOffTypeRepository.save(entity);

		assertTrue(timeOffTypeRepository.existsByName("Holiday"));
	}

	@Test
	void existsByName_ShouldReturnFalse_WhenEntityWithNameDoesNotExist() {
		assertFalse(timeOffTypeRepository.existsByName("NonExistingName"));
	}
}
