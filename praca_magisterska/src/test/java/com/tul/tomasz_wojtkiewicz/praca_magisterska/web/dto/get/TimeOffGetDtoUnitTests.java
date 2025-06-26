package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("dto")
@Tag("unit")
class TimeOffGetDtoUnitTests {
	@Test
		// cases: 1
	void given_entity_when_fromEntity_then_returnsDtoWithMatchingFields() {
		var employee = EmployeeTestEntityFactory.builder().id(1L).build().asEntity();
		var type = TimeOffTypeTestEntityFactory.builder().id(2L).build().asEntity();
		var limit = TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).id(3L).build().asEntity();
		var entity = TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit).id(4L).comment("testing").build().asEntity();
		var dto = TimeOffGetDto.fromEntity(entity);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getTimeOffType().getId(), dto.getTypeId());
		assertEquals(entity.getEmployee().getId(), dto.getEmployeeId());
		assertEquals(entity.getTimeOffYearlyLimit().getId(), dto.getYearlyLimitId());
		assertEquals(entity.getHoursCount(), dto.getHoursCount());
		assertEquals(entity.getComment(), dto.getComment());
		assertEquals(entity.getFirstDay(), dto.getFirstDay());
		assertEquals(entity.getLastDayInclusive(), dto.getLastDayInclusive());
	}

}
