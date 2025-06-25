package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
// ai tag: unit
class TimeOffTypeGetDtoTest {

	@Test
		// cases: 1
	void shouldMapFromEntityCorrectly() {
		TimeOffTypeEntity entity = new TimeOffTypeEntity();
		entity.setId(1L);
		entity.setName("Vacation");
		entity.setCompensationPercentage(100.0f);

		TimeOffTypeGetDto dto = TimeOffTypeGetDto.fromEntity(entity);

		assertEquals(1L, dto.getId());
		assertEquals("Vacation", dto.getName());
		assertEquals(100.0f, dto.getCompensationPercentage());
	}
}
