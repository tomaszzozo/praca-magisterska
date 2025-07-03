package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;// Testy jednostkowe dla EmployeeGetDto

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.EmployeeGetDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
// ai tag: unit
class EmployeeGetDtoTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get'

	@Test
	void fromEntity_shouldCopyPropertiesCorrectly() {
		EmployeeEntity entity = new EmployeeEntity();
		entity.setId(1L);
		entity.setEmail("test@example.com");
		entity.setFirstName("Jan");
		entity.setLastName("Kowalski");
		entity.setPhoneNumber("123456789");
		entity.setAccessLevel(3);

		EmployeeGetDto dto = EmployeeGetDto.fromEntity(entity);

		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getEmail(), dto.getEmail());
		assertEquals(entity.getFirstName(), dto.getFirstName());
		assertEquals(entity.getLastName(), dto.getLastName());
		assertEquals(entity.getPhoneNumber(), dto.getPhoneNumber());
		assertEquals(entity.getAccessLevel(), dto.getAccessLevel());
	}

	@Test
	void fromEntity_shouldReturnNonNullInstance() {
		EmployeeEntity entity = new EmployeeEntity();
		EmployeeGetDto dto = EmployeeGetDto.fromEntity(entity);
		assertNotNull(dto);
	}
}
