package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
// ai tag: unit
class TimeOffLimitGetDtoTest {

	@Test
	void shouldMapFromEntityCorrectly() {
		TimeOffTypeEntity typeEntity = new TimeOffTypeEntity();
		typeEntity.setId(10L);

		EmployeeEntity employeeEntity = new EmployeeEntity();
		employeeEntity.setId(20L);

		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setId(1L);
		entity.setMaxHours(160);
		entity.setLeaveYear(2025);
		entity.setTimeOffType(typeEntity);
		entity.setEmployee(employeeEntity);

		TimeOffLimitGetDto dto = TimeOffLimitGetDto.fromEntity(entity);

		assertEquals(1L, dto.getId());
		assertEquals(160, dto.getMaxHours());
		assertEquals(2025, dto.getLeaveYear());
		assertEquals(10L, dto.getTypeId());
		assertEquals(20L, dto.getEmployeeId());
	}
}
