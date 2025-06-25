package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Tag("integration")
// ai tag: integration
class TimeOffTypeRepositoryTest {

	@Autowired
	private TimeOffTypeRepository timeOffTypeRepository;

	@Test
		// cases: 1
	void existsByName_shouldReturnTrue_whenNameExists() {
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setName("Vacation");
		type.setCompensationPercentage(100.0f);
		timeOffTypeRepository.save(type);

		boolean exists = timeOffTypeRepository.existsByName("Vacation");

		assertThat(exists).isTrue();
	}

	@Test
		// cases: 1
	void existsByName_shouldReturnFalse_whenNameDoesNotExist() {
		boolean exists = timeOffTypeRepository.existsByName("Sick Leave");

		assertThat(exists).isFalse();
	}
}
