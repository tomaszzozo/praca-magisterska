package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off.TimeOffTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
class TimeOffTypeRepositoryIntegrationDeletionTests {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffLimitRepository timeOffLimitRepository;
    @Autowired
    private TimeOffRepository timeOffRepository;

    @AfterEach
    void afterEach() {
        timeOffRepository.deleteAll();
        timeOffLimitRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void entityCanNotBeDeletedIfThereAreDependentTimeOffEntities() {
        var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
        var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
        var limit = timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
        timeOffRepository.saveAndFlush(TimeOffTestEntityFactory.builder().employee(employee).timeOffYearlyLimit(limit).timeOffType(type).build().asEntity());
        assertThrows(DataIntegrityViolationException.class, () -> timeOffTypeRepository.deleteById(type.getId()));
    }

    @Test
    void entityCanBeDeletedAndDeletesDependentTimeOffLimitsEntitiesIfThereAreNoDependentTimeOffEntitiesButThereAreDependentTimeOffLimitEntities() {
        var employee = employeeRepository.saveAndFlush(EmployeeTestEntityFactory.build().asEntity());
        var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
        timeOffLimitRepository.saveAndFlush(TimeOffLimitTestEntityFactory.builder().timeOffType(type).employee(employee).build().asEntity());
        assertDoesNotThrow(() -> timeOffTypeRepository.deleteById(type.getId()));
        assertTrue(timeOffLimitRepository.findAll().isEmpty());
    }

    @Test
    void entityCanBeDeletedIfThereAreNoDependentEntities() {
        var type = timeOffTypeRepository.saveAndFlush(TimeOffTypeTestEntityFactory.build().asEntity());
        assertDoesNotThrow(() -> timeOffTypeRepository.deleteById(type.getId()));
    }
}
