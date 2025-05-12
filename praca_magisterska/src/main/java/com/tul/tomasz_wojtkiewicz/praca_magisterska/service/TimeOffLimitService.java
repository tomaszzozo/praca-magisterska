package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
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
public class TimeOffLimitService {
    private final TimeOffLimitRepository timeOffLimitRepository;
    private final TimeOffTypeService timeOffTypeService;
    private final EmployeeService employeeService;

    public static final int DEFAULT_MAX_HOURS = 0;

    public List<TimeOffLimitEntity> getAllByYearAndEmployeeId(@Range(min = 2020, max = 2100) int year, @Min(1) long employeeId) {
        var types = timeOffTypeService.getAll();
        var existingLimits = timeOffLimitRepository.findAllByLeaveYearAndEmployeeId(year, employeeId);
        var existingLimitsTypes = existingLimits.stream().map(e -> e.getTimeOffType().getId()).toList();
        var result = new ArrayList<>(types.stream().filter(t -> !existingLimitsTypes.contains(t.getId())).map(t -> defaultFromYearAndEmployeeId(year, employeeId, t)).toList());
        result.addAll(existingLimits);
        return result;
    }

    public TimeOffLimitEntity getById(@Min(1) long id) {
        return timeOffLimitRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Limit urlopu o podanym id nie istnieje"));
    }

    private TimeOffLimitEntity defaultFromYearAndEmployeeId(int year, long employeeId, TimeOffTypeEntity type) {
        var result = new TimeOffLimitEntity();
        result.setLeaveYear(year);
        result.setMaxHours(DEFAULT_MAX_HOURS);
        result.setEmployee(employeeService.getById(employeeId));
        result.setTimeOffType(type);
        return result;
    }

    @Transactional
    public void putAll(@Valid List<TimeOffLimitPutDto> dtos) {
        dtos.forEach(dto -> {
            TimeOffLimitEntity object;
            if (dto.getId() == 0) {
                if (timeOffLimitRepository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(dto.getYear(), dto.getEmployeeId(), dto.getTypeId())) {
                    throw new ApiException(HttpStatus.CONFLICT, "Limit dla danego roku pracownika i typu urlopu już istnieje");
                }
                object = defaultFromYearAndEmployeeId(dto.getYear(), dto.getEmployeeId(), timeOffTypeService.getById(dto.getTypeId()));
            } else {
                object = timeOffLimitRepository.findById(dto.getId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Nie znaleziono limitu o podanym id."));
            }
            object.setMaxHours(dto.getMaxHours());
            if (object.getTimeOffs() != null && object.getTimeOffs().stream().mapToInt(TimeOffEntity::getHoursCount).sum() > object.getMaxHours()) {
                throw new ApiException(HttpStatus.CONFLICT, "Nowa liczba godzin limitu nie wystarczy na zapisane urlopy");
            }
            timeOffLimitRepository.save(object);
        });
    }
}
