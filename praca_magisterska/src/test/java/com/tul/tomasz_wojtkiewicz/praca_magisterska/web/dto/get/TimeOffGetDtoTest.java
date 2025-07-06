package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;// Testy jednostkowe dla TimeOffGetDto

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
// MISTAKE: import of non-existing class import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffYearlyLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffGetDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
// ai tag: unit
class TimeOffGetDtoTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get'

	@Test
		// cases: 1
	void fromEntity_shouldCopyPropertiesCorrectly() {
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setId(10L);

		var yearlyLimit = new TimeOffLimitEntity(); // MISTAKE: usage of non-existing class TimeOffYearlyLimitEntity
		yearlyLimit.setId(20L);

		EmployeeEntity employee = new EmployeeEntity();
		employee.setId(30L);

		TimeOffEntity entity = new TimeOffEntity();
		entity.setId(1L);
		entity.setFirstDay(LocalDate.of(2023, 5, 1));
		entity.setLastDayInclusive(LocalDate.of(2023, 5, 5));
		entity.setHoursCount(40);
		entity.setTimeOffType(type);
		entity.setTimeOffYearlyLimit(yearlyLimit);
		entity.setEmployee(employee);
		entity.setComment("Test comment");

		TimeOffGetDto dto = TimeOffGetDto.fromEntity(entity);

		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getFirstDay(), dto.getFirstDay());
		assertEquals(entity.getLastDayInclusive(), dto.getLastDayInclusive());
		assertEquals(entity.getHoursCount(), dto.getHoursCount());
		assertEquals(type.getId(), dto.getTypeId());
		assertEquals(yearlyLimit.getId(), dto.getYearlyLimitId());
		assertEquals(employee.getId(), dto.getEmployeeId());
		assertEquals(entity.getComment(), dto.getComment());
	}

	@Test
		// cases: 1
	void fromEntity_shouldReturnNonNullInstance() {
		TimeOffEntity entity = new TimeOffEntity();
		var type = new TimeOffTypeEntity();
		type.setId(1L);
		entity.setTimeOffType(type);
		var limit = new TimeOffLimitEntity();
		limit.setId(1L);
		entity.setTimeOffYearlyLimit(limit); // MISTAKE: usage of non-existing class TimeOffYearlyLimitEntity
		var employee = new EmployeeEntity();
		employee.setId(1L);
		entity.setEmployee(employee);
		entity.setHoursCount(1);
		entity.setId(1L);

		TimeOffGetDto dto = TimeOffGetDto.fromEntity(entity); // MISTAKE: fields not set - null pointer exception
		assertNotNull(dto);
	}
}
