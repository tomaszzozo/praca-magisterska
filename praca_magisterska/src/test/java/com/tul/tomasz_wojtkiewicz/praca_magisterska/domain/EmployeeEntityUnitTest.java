// Testy jednostkowe dla klasy EmployeeEntity (głównie testy getterów, setterów i metod EqualsAndHashCode)
package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
// ai tag: unit
class EmployeeEntityUnitTest {

	@Test
	// CASES: 1
	void testGettersAndSetters() {
		EmployeeEntity employee = new EmployeeEntity();

		employee.setId(1L);
		employee.setEmail("test@example.com");
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setPhoneNumber("123456789");
		employee.setAccessLevel(2);

		assertEquals(1L, employee.getId());
		assertEquals("test@example.com", employee.getEmail());
		assertEquals("John", employee.getFirstName());
		assertEquals("Doe", employee.getLastName());
		assertEquals("123456789", employee.getPhoneNumber());
		assertEquals(2, employee.getAccessLevel());
	}

	@Test
		// CASES: 1
	void testEqualsAndHashCode() {
		EmployeeEntity e1 = new EmployeeEntity();
		e1.setId(1L);
		e1.setEmail("a@b.com");
		e1.setFirstName("A");
		e1.setLastName("B");
		e1.setPhoneNumber("123456789");
		e1.setAccessLevel(1);

		EmployeeEntity e2 = new EmployeeEntity();
		e2.setId(1L);
		e2.setEmail("a@b.com");
		e2.setFirstName("A");
		e2.setLastName("B");
		e2.setPhoneNumber("123456789");
		e2.setAccessLevel(1);

		EmployeeEntity e3 = new EmployeeEntity();
		e3.setId(2L);

		assertEquals(e1, e2);
		assertEquals(e1.hashCode(), e2.hashCode());
		assertNotEquals(e1, e3);
	}
}
