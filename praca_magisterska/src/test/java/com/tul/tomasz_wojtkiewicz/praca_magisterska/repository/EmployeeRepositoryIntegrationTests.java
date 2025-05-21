package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_limit.TimeOffLimitTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Tag("integration")
class EmployeeRepositoryIntegrationTests {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void fieldsOtherThanEmailAndPhoneNumberAreNotUnique() {
        assertDoesNotThrow(() -> employeeRepository.saveAllAndFlush(Stream.of(Arguments.of("Ruggiero", "Stealy", "rstealy0@plala.or.jp", "712568069", 1), Arguments.of("Ruggiero", "Stealy", "cbiggin1@tiny.cc", "709347655", 1), Arguments.of("Ruggiero", "Maypother", "sbowlesworth2@de.vu", "452172090", 2), Arguments.of("Waneta", "Stealy", "wkarus3@reverbnation.com", "164781938", 2), Arguments.of("Roxine", "Maypother", "rmaypother4@paginegialle.it", "131396665", 3)).map(a -> EmployeeTestEntityFactory.builder().firstName((String) a.get()[0]).lastName((String) a.get()[1]).email((String) a.get()[2]).phoneNumber((String) a.get()[3]).accessLevel((Integer) a.get()[4]).build().asEntity()).toList()));
    }

    @Test
    void emailIsUnique() {
        var firstEmployee = EmployeeTestEntityFactory.build().asEntity();
        employeeRepository.save(firstEmployee);
        var secondEmployee = EmployeeTestEntityFactory.builder().firstName(firstEmployee.getFirstName() + "second").lastName(firstEmployee.getLastName() + "second").phoneNumber(new StringBuilder(firstEmployee.getPhoneNumber()).reverse().toString()).email(firstEmployee.getEmail()).build().asEntity();
        assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.saveAndFlush(secondEmployee));
    }

    @Test
    void phoneNumberIsUnique() {
        var firstEmployee = EmployeeTestEntityFactory.build().asEntity();
        employeeRepository.save(firstEmployee);
        var secondEmployee = EmployeeTestEntityFactory.builder().firstName(firstEmployee.getFirstName() + "second").lastName(firstEmployee.getLastName() + "second").phoneNumber(firstEmployee.getPhoneNumber()).email(firstEmployee.getEmail() + ".second").build().asEntity();
        assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.saveAndFlush(secondEmployee));
    }

    @Test
    void samePhoneNumberAndEmailAlsoFails() {
        var firstEmployee = EmployeeTestEntityFactory.build().asEntity();
        employeeRepository.save(firstEmployee);
        var secondEmployee = EmployeeTestEntityFactory.builder().firstName(firstEmployee.getFirstName() + "second").lastName(firstEmployee.getLastName() + "second").phoneNumber(firstEmployee.getPhoneNumber()).email(firstEmployee.getEmail()).build().asEntity();
        assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.saveAndFlush(secondEmployee));
    }

    @Test
    void timeOffLimitsRelationWorks() {
        var employee = EmployeeTestEntityFactory.build().asEntity();
        var limit = TimeOffLimitTestEntityFactory.builder().maxHours(5).employee(employee).build().asEntity();
        employee.setYearlyTimeOffLimits(List.of(limit));

        employeeRepository.saveAndFlush(employee);

        var savedEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
        assertEquals(1, savedEmployee.getYearlyTimeOffLimits().size());
        assertEquals(5, savedEmployee.getYearlyTimeOffLimits().getFirst().getMaxHours());
    }

    @Test
    void timeOffsRelationWorks() {
        var employee = EmployeeTestEntityFactory.build().asEntity();
        var timeOffDate = LocalDate.of(2024, 11, 4);
        var timeOff = TimeOffTestEntityFactory.builder().firstDay(timeOffDate).lastDayInclusive(timeOffDate).hoursCount(2).employee(employee).build().asEntity();
        employee.setTimeOffs(List.of(timeOff));

        employeeRepository.saveAndFlush(employee);

        var savedEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
        assertEquals(1, savedEmployee.getTimeOffs().size());
        assertEquals(timeOffDate, savedEmployee.getTimeOffs().getFirst().getFirstDay());
        assertEquals(timeOffDate, savedEmployee.getTimeOffs().getFirst().getLastDayInclusive());
        assertEquals(2, savedEmployee.getTimeOffs().getFirst().getHoursCount());
    }
}
