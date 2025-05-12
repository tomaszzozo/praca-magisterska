package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.TestEmployeeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_limit.TestTimeOffLimitEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_type.TestTimeOffTypeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@DataJpaTest
class TimeOffLimitRepositoryIntegrationTests {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffLimitRepository timeOffLimitRepository;

    private static Stream<Arguments> years() {
        return IntStream.range(2025, 2030).mapToObj(Arguments::of);
    }

    private static Stream<Arguments> nullableLimits() {
        return Stream.of(Arguments.of("type", (Function<TimeOffLimitEntity, TimeOffLimitEntity>) (o -> {
                    o.setTimeOffType(null);
                    return o;
                })), Arguments.of("employee", (Function<TimeOffLimitEntity, TimeOffLimitEntity>) (o -> {
                    o.setEmployee(null);
                    return o;
                }))
        );
    }

    private static Stream<Arguments> negativeMaxHours() {
        return Stream.of(Integer.MIN_VALUE, -100, -1).map(Arguments::of);
    }

    private TestTimeOffLimitEntityBuilder testLimit() {
        var employee = new TestEmployeeEntityBuilder().build();
        var type = new TestTimeOffTypeEntityBuilder().build();
        employeeRepository.save(employee);
        timeOffTypeRepository.save(type);
        return new TestTimeOffLimitEntityBuilder(employee, type);
    }

    @Test
    void validData() {
        var employees = ValidDataProvider.getEmployees().subList(0, 4);
        var types = ValidDataProvider.getTimeOffTypes();
        employeeRepository.saveAll(employees);
        timeOffTypeRepository.saveAll(types);

        Assertions.assertDoesNotThrow(() -> timeOffLimitRepository.saveAll(ValidDataProvider.getTimeOffLimits(employees, types)));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
    void yearOutOfRange(int invalidYear) {
        var limit = testLimit().withLeaveYear(invalidYear).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffLimitRepository.save(limit));
    }

    @ParameterizedTest
    @MethodSource("negativeMaxHours")
    void negativeMaxHours(int invalidMaxHours) {
        var limit = testLimit().withMaxHours(invalidMaxHours).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffLimitRepository.save(limit));
    }

    @Test
    void nullMaxHours() {
        var limit = testLimit().withMaxHours(null).build();
        Assertions.assertDoesNotThrow(() -> timeOffLimitRepository.save(limit));
    }

    @Test
    void zeroMaxHours() {
        var limit = testLimit().withMaxHours(0).build();
        Assertions.assertDoesNotThrow(() -> timeOffLimitRepository.save(limit));
    }

    @ParameterizedTest
    @MethodSource("nullableLimits")
    void notNullFieldsAsNull(String ignoredNullFieldName, Function<TimeOffLimitEntity, TimeOffLimitEntity> nullSetter) {
        var limit = nullSetter.apply(testLimit().build());
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffLimitRepository.save(limit));
    }

    @ParameterizedTest
    @MethodSource("years")
    void maxHoursMoreThanHoursInYear(int year) {
        var limit = testLimit().withLeaveYear(year).withMaxHours(LocalDate.of(year, 12, 31).getDayOfYear() * 24 + 1).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffLimitRepository.save(limit));
    }

    @ParameterizedTest
    @MethodSource("years")
    void maxHoursEqualThanHoursInYear(int year) {
        var limit = testLimit().withLeaveYear(year).withMaxHours(LocalDate.of(year, 12, 31).getDayOfYear() * 24).build();
        Assertions.assertDoesNotThrow(() -> timeOffLimitRepository.save(limit));
    }

    // TODO test uniqueness
}
