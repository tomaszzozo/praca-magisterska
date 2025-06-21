package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("dto")
class EmployeeGetDtoTests {
	@Test
	void given_entity_when_fromEntity_then_returnsDtoWithMatchingFields() {
		var entity = EmployeeTestEntityFactory.builder().email("test@email.com").phoneNumber("123456789").firstName("Bruce").lastName("Wayne").accessLevel(3).id(7L).build().asEntity();
		var dto = EmployeeGetDto.fromEntity(entity);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getAccessLevel(), dto.getAccessLevel());
		assertEquals(entity.getEmail(), dto.getEmail());
		assertEquals(entity.getFirstName(), dto.getFirstName());
		assertEquals(entity.getLastName(), dto.getLastName());
		assertEquals(entity.getPhoneNumber(), dto.getPhoneNumber());
	}
}
