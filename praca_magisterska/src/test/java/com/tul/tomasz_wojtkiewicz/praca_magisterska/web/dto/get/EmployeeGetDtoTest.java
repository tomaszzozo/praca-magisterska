package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
// ai tag: unit
class EmployeeGetDtoTest {

	@Test
	void shouldMapFromEntityCorrectly() {
		EmployeeEntity entity = new EmployeeEntity();
		entity.setId(1L);
		entity.setEmail("test@example.com");
		entity.setFirstName("John");
		entity.setLastName("Doe");
		entity.setPhoneNumber("123456789");
		entity.setAccessLevel(2);

		EmployeeGetDto dto = EmployeeGetDto.fromEntity(entity);

		assertEquals(1L, dto.getId());
		assertEquals("test@example.com", dto.getEmail());
		assertEquals("John", dto.getFirstName());
		assertEquals("Doe", dto.getLastName());
		assertEquals("123456789", dto.getPhoneNumber());
		assertEquals(2, dto.getAccessLevel());
	}
}
