package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;

@SpringBootTest
class TimeOffRepositoryTest {
    @Autowired
    private TimeOffRepository timeOffRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffTypeLimitPerYearAndEmployeeRepository timeOffTypeLimitPerYearAndEmployeeRepository;

    @AfterEach
    void afterAll() {
        timeOffRepository.deleteAll();
        timeOffTypeLimitPerYearAndEmployeeRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void findAllByYearAndEmployeeIdQuery() {
        var employees = ValidDataProvider.getEmployees().subList(0, 4);
        employeeRepository.saveAll(employees);
        var types = ValidDataProvider.getTimeOffTypes();
        timeOffTypeRepository.saveAll(types);
        var limits = ValidDataProvider.getTimeOffLimits(employees, types);
        timeOffTypeLimitPerYearAndEmployeeRepository.saveAll(limits);
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
}
