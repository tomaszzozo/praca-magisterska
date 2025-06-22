package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Tag("entity")
@Tag("unit")
class TimeOffTypeEntityUnitTests {
	@Test
	void equalsShouldCompareFieldsOtherThanYearlyLimitsAndTimeOffs() {
		var entity1 = TimeOffTypeTestEntityFactory.builder().build().asEntity();
		var entity2 = TimeOffTypeTestEntityFactory.builder().build().asEntity();
		assertEquals(entity1, entity2);

		entity1.setTimeOffs(List.of(new TimeOffEntity()));
		entity1.setYearlyLimits(List.of(new TimeOffLimitEntity()));
		assertEquals(entity1, entity2);

		entity1.setId(1L);
		assertNotEquals(entity1, entity2);

		entity1.setId(entity2.getId());
		entity1.setName(entity2.getName()+"a");
		assertNotEquals(entity1, entity2);

		entity1.setName(entity2.getName());
		entity1.setCompensationPercentage(entity2.getCompensationPercentage()+0.1f);
		assertNotEquals(entity1, entity2);
	}
}
