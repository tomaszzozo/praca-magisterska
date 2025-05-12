package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.TestEmployeeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off.TestTimeOffEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_limit.TestTimeOffLimitEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_type.TestTimeOffTypeEntityBuilder;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.stream.Stream;

@SpringBootTest
class TimeOffTypeRepositoryIntegrationTests {
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffLimitRepository timeOffLimitRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffRepository timeOffRepository;

    @AfterEach
    void afterEach() {
        timeOffRepository.deleteAll();
        timeOffLimitRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    private static Stream<Arguments> invalidCompensationPercentages() {
        return Stream.of(-Float.MAX_VALUE, -1f, -0.1f, -0.01f, 100.01f, 100.1f, Float.MAX_VALUE).map(Arguments::of);
    }

    @Test
    void validData() {
        Assertions.assertDoesNotThrow(() -> timeOffTypeRepository.saveAll(ValidDataProvider.getTimeOffTypes()));
    }

    @Test
    void blankName() {
        var type = new TestTimeOffTypeEntityBuilder().withName("").build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffTypeRepository.save(type));
    }

    @Test
    void takenName() {
        timeOffTypeRepository.save(new TestTimeOffTypeEntityBuilder().build());
        var type = new TestTimeOffTypeEntityBuilder().build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffTypeRepository.save(type));
    }

    @ParameterizedTest
    @MethodSource("invalidCompensationPercentages")
    void compensationPercentageOutOfRange(float invalidCompensation) {
        var type = new TestTimeOffTypeEntityBuilder().withCompensationPercentage(invalidCompensation).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffTypeRepository.save(type));
    }

    @Test
    void deletesLimitsOnDeletion() {
        var employee = new TestEmployeeEntityBuilder().build();
        employeeRepository.save(employee);
        var type = new TestTimeOffTypeEntityBuilder().build();
        timeOffTypeRepository.save(type);
        var limit = new TestTimeOffLimitEntityBuilder(employee, type).build();
        timeOffLimitRepository.save(limit);

        Assertions.assertDoesNotThrow(() -> timeOffTypeRepository.deleteAll());
        Assertions.assertEquals(0, timeOffLimitRepository.count());
    }

    @Test
    void throwsErrorWhenRelatedTimeOffsExistsOnDeletion() {
        var employee = new TestEmployeeEntityBuilder().build();
        employeeRepository.save(employee);
        var type = new TestTimeOffTypeEntityBuilder().build();
        timeOffTypeRepository.save(type);
        var limit = new TestTimeOffLimitEntityBuilder(employee, type).build();
        timeOffLimitRepository.save(limit);
        var timeOff = new TestTimeOffEntityBuilder().withEmployee(employee).withType(type).withLimit(limit).build();
        timeOffRepository.save(timeOff);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffTypeRepository.deleteAll());
        Assertions.assertEquals(1, timeOffLimitRepository.count());
        Assertions.assertEquals(1, timeOffTypeRepository.count());
    }
}
