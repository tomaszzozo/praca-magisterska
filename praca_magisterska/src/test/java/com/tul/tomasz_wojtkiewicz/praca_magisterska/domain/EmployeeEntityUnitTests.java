package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Tag("entity")
@Tag("unit")
class EmployeeEntityUnitTests {
	@Test
		// cases: 8
	void equalsShouldCompareFieldsOtherThanLimitsAndTimeOffs() {
		var entity1 = EmployeeTestEntityFactory.build().asEntity();
		var entity2 = EmployeeTestEntityFactory.build().asEntity();
		assertEquals(entity1, entity2);

		entity1.setTimeOffs(List.of(new TimeOffEntity()));
		entity1.setYearlyTimeOffLimits(List.of(new TimeOffLimitEntity()));
		assertEquals(entity1, entity2);

		entity1.setId(1L);
		assertNotEquals(entity1, entity2);

		entity1.setId(entity2.getId());
		entity1.setEmail(entity2.getEmail()+"a");
		assertNotEquals(entity1, entity2);

		entity1.setEmail(entity2.getEmail());
		entity1.setFirstName(entity2.getFirstName()+"a");
		assertNotEquals(entity1, entity2);

		entity1.setFirstName(entity2.getFirstName());
		entity1.setLastName(entity2.getLastName()+"a");
		assertNotEquals(entity1, entity2);

		entity1.setLastName(entity2.getLastName());
		entity1.setPhoneNumber(new StringBuilder(entity2.getPhoneNumber()).reverse().toString());
		assertNotEquals(entity1, entity2);

		entity1.setPhoneNumber(entity2.getPhoneNumber());
		entity1.setAccessLevel(entity2.getAccessLevel() + 1 == 4 ? 0 : 3);
		assertNotEquals(entity1, entity2);
	}
}
