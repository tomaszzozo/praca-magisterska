package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;// Testy jednostkowe dla TimeOffLimitGetDto

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffLimitGetDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
// ai tag: unit
class TimeOffLimitGetDtoTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get'

	@Test
	void fromEntity_shouldCopyPropertiesCorrectly() {
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setId(100L);

		EmployeeEntity employee = new EmployeeEntity();
		employee.setId(200L);

		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setId(1L);
		entity.setMaxHours(120);
		entity.setLeaveYear(2023);
		entity.setTimeOffType(type);
		entity.setEmployee(employee);

		TimeOffLimitGetDto dto = TimeOffLimitGetDto.fromEntity(entity);

		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getMaxHours(), dto.getMaxHours());
		assertEquals(entity.getLeaveYear(), dto.getLeaveYear());
		assertEquals(type.getId(), dto.getTypeId());
		assertEquals(employee.getId(), dto.getEmployeeId());
	}

	@Test
	void fromEntity_shouldReturnNonNullInstance() {
		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setTimeOffType(new TimeOffTypeEntity());
		entity.setEmployee(new EmployeeEntity());

		TimeOffLimitGetDto dto = TimeOffLimitGetDto.fromEntity(entity);
		assertNotNull(dto);
	}
}
