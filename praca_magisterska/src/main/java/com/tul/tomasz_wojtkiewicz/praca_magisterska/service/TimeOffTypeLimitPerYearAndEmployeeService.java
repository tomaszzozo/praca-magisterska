package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeLimitPerYearAndEmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeLimitPerYearAndEmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypeLimitPerYearAndEmployeePutDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Validated
public class TimeOffTypeLimitPerYearAndEmployeeService {
    private final TimeOffTypeLimitPerYearAndEmployeeRepository timeOffTypeLimitPerYearAndEmployeeRepository;
    private final TimeOffTypeService timeOffTypeService;
    private final EmployeeService employeeService;

    public List<TimeOffTypeLimitPerYearAndEmployeeEntity> getAllByYear(@Range(min = 2020, max = 2100) int year, @Min(1) long employeeId) {
        var types = timeOffTypeService.getAll();
        var existingLimits = timeOffTypeLimitPerYearAndEmployeeRepository.findAllByYearAndEmployeeId(year, employeeId);
        var existingLimitsTypes = existingLimits.stream().map(e -> e.getTimeOffType().getId()).toList();
        var result = new ArrayList<>(types.stream().filter(t -> !existingLimitsTypes.contains(t.getId())).map(t -> defaultFromYearAndEmployeeId(year, employeeId, t)).toList());
        result.addAll(existingLimits);
        return result;
    }

    public TimeOffTypeLimitPerYearAndEmployeeEntity defaultFromYearAndEmployeeId(int year, long employeeId, TimeOffTypeEntity type) {
        var result = new TimeOffTypeLimitPerYearAndEmployeeEntity();
        result.setYear(year);
        result.setMaxHours(0);
        result.setEmployee(employeeService.getById(employeeId));
        result.setTimeOffType(type);
        return result;
    }

    @Transactional
    public void putAll(@Valid List<TimeOffTypeLimitPerYearAndEmployeePutDto> dtos) {
        dtos.forEach(dto -> {
            TimeOffTypeLimitPerYearAndEmployeeEntity object;
            if (dto.getId() == 0) {
                if (timeOffTypeLimitPerYearAndEmployeeRepository.existsByYearAndEmployeeIdAndTimeOffTypeId(dto.getYear(), dto.getEmployeeId(), dto.getTypeId())) {
                    throw new ApiException(HttpStatus.CONFLICT, "Limit dla danego roku pracownika i typu urlopu już istnieje");
                }
                object = defaultFromYearAndEmployeeId(dto.getYear(), dto.getEmployeeId(), timeOffTypeService.getById(dto.getTypeId()));
            } else {
                object = timeOffTypeLimitPerYearAndEmployeeRepository.findById(dto.getId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Nie znaleziono limitu o podanym id."));
            }
            object.setMaxHours(dto.getMaxHours());
            timeOffTypeLimitPerYearAndEmployeeRepository.save(object);
        });
    }
}
