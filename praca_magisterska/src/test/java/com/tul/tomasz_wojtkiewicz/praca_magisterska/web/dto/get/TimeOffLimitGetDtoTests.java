package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("dto")
class TimeOffLimitGetDtoTests {
	@Test
	void given_entity_when_fromEntity_then_returnsDtoWithMatchingFields() {
		var employee = EmployeeTestEntityFactory.builder().id(1L).build().asEntity();
		var type = TimeOffTypeTestEntityFactory.builder().id(2L).build().asEntity();
		var entity = TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).id(3L).maxHours(10).build().asEntity();
		var dto = TimeOffLimitGetDto.fromEntity(entity);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getMaxHours(), dto.getMaxHours());
		assertEquals(entity.getTimeOffType().getId(), dto.getTypeId());
		assertEquals(entity.getLeaveYear(), dto.getLeaveYear());
		assertEquals(entity.getEmployee().getId(), dto.getEmployeeId());
	}
}
