package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.TestEmployeeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off.TestTimeOffEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_limit.TestTimeOffLimitEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_type.TestTimeOffTypeEntityBuilder;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DataJpaTest
class TimeOffRepositoryIntegrationTests {
    @Autowired
    private TimeOffRepository timeOffRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffLimitRepository timeOffLimitRepository;

    private static Stream<Arguments> invalidHours() {
        return Stream.of(Integer.MIN_VALUE, -1000, -1, 0).map(Arguments::of);
    }

    private TestTimeOffEntityBuilder testTimeOff() {
        var employee = new TestEmployeeEntityBuilder().build();
        employeeRepository.save(employee);
        var type = new TestTimeOffTypeEntityBuilder().build();
        timeOffTypeRepository.save(type);
        var limit = new TestTimeOffLimitEntityBuilder(employee, type).build();
        timeOffLimitRepository.save(limit);
        return new TestTimeOffEntityBuilder().withEmployee(employee).withType(type).withLimit(limit);
    }

    @Test
    void validData() {
        var employees = ValidDataProvider.getEmployees().subList(0, 4);
        employeeRepository.saveAll(employees);
        var types = ValidDataProvider.getTimeOffTypes();
        timeOffTypeRepository.saveAll(types);
        var limits = ValidDataProvider.getTimeOffLimits(employees, types);
        timeOffLimitRepository.saveAll(limits);
        var timeOffs = ValidDataProvider.getTimeOffs(limits);
        Assertions.assertDoesNotThrow(() -> timeOffRepository.saveAll(timeOffs));
    }

    @Test
    void findAllByYearAndEmployeeIdQuery() {
        var employees = ValidDataProvider.getEmployees().subList(0, 4);
        employeeRepository.saveAll(employees);
        var types = ValidDataProvider.getTimeOffTypes();
        timeOffTypeRepository.saveAll(types);
        var limits = ValidDataProvider.getTimeOffLimits(employees, types);
        timeOffLimitRepository.saveAll(limits);
        var timeOffs = ValidDataProvider.getTimeOffs(limits);
        timeOffRepository.saveAll(timeOffs);

        var years = timeOffs.stream().map(t -> t.getFirstDay().getYear()).collect(Collectors.toSet());
        for (var e : employees) {
            for (var y : years) {
                var result = timeOffRepository.findAllByYearAndEmployeeId(e.getId(), y).stream().map(TimeOffEntity::getId).collect(Collectors.toSet());
                var expected = timeOffs.stream().filter(t -> t.getEmployee().getId() == e.getId() && t.getFirstDay().getYear() == y).map(TimeOffEntity::getId).collect(Collectors.toSet());
                Assertions.assertEquals(expected, result);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("invalidHours")
    void negativeOrZeroHours(int invalidHoursCount) {
        var timeOff = testTimeOff().withHoursCount(invalidHoursCount).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void oneHour() {
        var timeOff = testTimeOff().withHoursCount(1).build();
        Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
    }

    @Test
    void fistDayAfterLastDay() {
        var timeOff = testTimeOff().withFirstDay(TestTimeOffEntityBuilder.Defaults.lastDayInclusive.plusDays(1)).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
    void firstDayYearOutOfRange(int invalidYear) {
        var timeOff = testTimeOff().withFirstDay(TestTimeOffEntityBuilder.Defaults.firstDay.withYear(invalidYear)).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
    void lastDayYearOutOfRange(int invalidYear) {
        var timeOff = testTimeOff().withLastDay(TestTimeOffEntityBuilder.Defaults.lastDayInclusive.withYear(invalidYear)).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void firstAndLastDayMonthDoesNotMatch() {
        var timeOff = testTimeOff().withFirstDay(LocalDate.of(2025, 10, 10)).withLastDay(LocalDate.of(2025, 11, 10)).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void firstAndLastDayYearDoesNotMatch() {
        var timeOff = testTimeOff().withFirstDay(LocalDate.of(2025, 10, 10)).withLastDay(LocalDate.of(2026, 10, 10)).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void nullLimit() {
        var timeOff = testTimeOff().withLimit(null).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void nullLastDay() {
        var timeOff = testTimeOff().withLastDay(null).build();
        Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void nullFirstDay() {
        var timeOff = testTimeOff().withFirstDay(null).build();
        Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void nullEmployee() {
        var timeOff = testTimeOff().withEmployee(null).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void nullType() {
        var timeOff = testTimeOff().withType(null).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void nullComment() {
        var timeOff = testTimeOff().withComment(null).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> timeOffRepository.save(timeOff));
    }

    @Test
    void firstDayAndEmployeeNotUnique() {
        var day = LocalDate.of(2025, 10, 10);
        var timeOff = testTimeOff().withFirstDay(day).withLastDay(day).build();
        timeOffRepository.save(timeOff);
        var timeOff2 = new TestTimeOffEntityBuilder().withLimit(timeOff.getTimeOffYearlyLimit()).withEmployee(timeOff.getEmployee()).withType(timeOff.getTimeOffType()).withFirstDay(timeOff.getFirstDay()).withLastDay(timeOff.getLastDayInclusive().plusDays(1)).build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.save(timeOff2));
    }

    @Test
    void lastDayAndEmployeeNotUnique() {
        var day = LocalDate.of(2025, 10, 10);
        var timeOff = testTimeOff().withFirstDay(day).withLastDay(day).build();
        timeOffRepository.save(timeOff);
        var timeOff2 = new TestTimeOffEntityBuilder().withLimit(timeOff.getTimeOffYearlyLimit()).withEmployee(timeOff.getEmployee()).withType(timeOff.getTimeOffType()).withFirstDay(timeOff.getFirstDay().minusDays(1)).withLastDay(timeOff.getLastDayInclusive()).build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.save(timeOff2));
    }
}
