package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
// ai tag: unit
class TimeOffGetDtoTest {

	@Test
		// cases: 1
	void shouldMapFromEntityCorrectly() {
		TimeOffTypeEntity typeEntity = new TimeOffTypeEntity();
		typeEntity.setId(100L);

		TimeOffLimitEntity limitEntity = new TimeOffLimitEntity();
		limitEntity.setId(200L);

		EmployeeEntity employeeEntity = new EmployeeEntity();
		employeeEntity.setId(300L);

		TimeOffEntity entity = new TimeOffEntity();
		entity.setId(1L);
		entity.setFirstDay(LocalDate.of(2025, 6, 1));
		entity.setLastDayInclusive(LocalDate.of(2025, 6, 5));
		entity.setHoursCount(40);
		entity.setTimeOffType(typeEntity);
		entity.setTimeOffYearlyLimit(limitEntity);
		entity.setEmployee(employeeEntity);
		entity.setComment("Annual leave");

		TimeOffGetDto dto = TimeOffGetDto.fromEntity(entity);

		assertEquals(1L, dto.getId());
		assertEquals(LocalDate.of(2025, 6, 1), dto.getFirstDay());
		assertEquals(LocalDate.of(2025, 6, 5), dto.getLastDayInclusive());
		assertEquals(40, dto.getHoursCount());
		assertEquals(100L, dto.getTypeId());
		assertEquals(200L, dto.getYearlyLimitId());
		assertEquals(300L, dto.getEmployeeId());
		assertEquals("Annual leave", dto.getComment());
	}
}
