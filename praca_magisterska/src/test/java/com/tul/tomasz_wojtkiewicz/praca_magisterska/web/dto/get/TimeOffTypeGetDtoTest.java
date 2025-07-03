package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;// Testy jednostkowe dla TimeOffTypeGetDto

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffTypeGetDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
// ai tag: unit
class TimeOffTypeGetDtoTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get'

	@Test
		// CASES: 1
	void fromEntity_shouldCopyPropertiesCorrectly() {
		TimeOffTypeEntity entity = new TimeOffTypeEntity();
		entity.setId(10L);
		entity.setName("Vacation");
		entity.setCompensationPercentage(75.5f);

		TimeOffTypeGetDto dto = TimeOffTypeGetDto.fromEntity(entity);

		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getName(), dto.getName());
		assertEquals(entity.getCompensationPercentage(), dto.getCompensationPercentage());
	}

	@Test
		// CASES: 1
	void fromEntity_shouldReturnNonNullInstance() {
		TimeOffTypeEntity entity = new TimeOffTypeEntity();
		entity.setCompensationPercentage(100f);
		entity.setId(1L);
		TimeOffTypeGetDto dto = TimeOffTypeGetDto.fromEntity(entity); // MISTAKE: fields not set - null pointer exception
		assertNotNull(dto);
	}
}
