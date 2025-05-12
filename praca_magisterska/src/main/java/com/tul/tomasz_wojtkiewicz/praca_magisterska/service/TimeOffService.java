package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffPutDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@AllArgsConstructor
@Validated
public class TimeOffService {
    private final TimeOffRepository timeOffRepository;
    private final TimeOffLimitService timeOffLimitService;
    private final TimeOffTypeService timeOffTypeService;
    private final EmployeeService employeeService;

    public List<TimeOffEntity> getAllByYearAndEmployeeId(@Range(min = 2020, max = 2100) int year, @Min(1) long employeeId) {
        return timeOffRepository.findAllByYearAndEmployeeId(employeeId, year);
    }

    @Transactional
    public void post(@Valid TimeOffPostDto dto) {
        var otherTimeOffs = timeOffRepository.findAllByYearAndEmployeeId(dto.getEmployeeId(), dto.getFirstDay().getYear());
        if (otherTimeOffs.stream().anyMatch(e ->
                !e.getLastDayInclusive().isBefore(dto.getFirstDay()) &&
                        !e.getFirstDay().isAfter(dto.getLastDayInclusive()))) {
            throw new ApiException(HttpStatus.CONFLICT, "Nowy urlop koliduje z istniejącym urlopem.");
        }

        var yearlyLimit = timeOffLimitService.getById(dto.getYearlyLimitId());
        var usedHours = otherTimeOffs.stream().filter(e -> e.getTimeOffType().getId() == dto.getTypeId()).mapToInt(TimeOffEntity::getHoursCount).sum();
        if (yearlyLimit.getMaxHours() < usedHours + dto.getHoursCount()) {
            int hoursOverLimit = usedHours + dto.getHoursCount() - yearlyLimit.getMaxHours();
            if (hoursOverLimit == 1) {
                throw new ApiException(HttpStatus.CONFLICT, "Nowy urlop przekorczy limit o 1 godzinę");
            } else if (Integer.toString(hoursOverLimit).matches("[234]$")) {
                throw new ApiException(HttpStatus.CONFLICT, "Nowy urlop przekorczy limit o %d godziny".formatted(hoursOverLimit));
            }
            throw new ApiException(HttpStatus.CONFLICT, "Nowy urlop przekorczy limit o %d godzin".formatted(hoursOverLimit));
        }

        var obj = new TimeOffEntity();
        BeanUtils.copyProperties(dto, obj, "typeId", "employeeId", "yearlyLimitId");
        obj.setTimeOffType(timeOffTypeService.getById(dto.getTypeId()));
        obj.setEmployee(employeeService.getById(dto.getEmployeeId()));
        obj.setTimeOffYearlyLimit(yearlyLimit);
        timeOffRepository.save(obj);
    }

    @Transactional
    public void put(@Min(1) long id, @Valid TimeOffPutDto dto) {
        var obj = findOrElseThrow(id);
        if (dto.getFirstDay().getYear() != obj.getFirstDay().getYear()) {
            throw new ApiException(HttpStatus.CONFLICT, "Nie można modyfikować roku urlopu.");
        }
        var otherTimeOffs = timeOffRepository.findAllByYearAndEmployeeId(dto.getEmployeeId(), dto.getFirstDay().getYear());
        otherTimeOffs = otherTimeOffs.stream().filter(e -> e.getId() != id).toList();
        if (otherTimeOffs.stream().anyMatch(e ->
                !e.getLastDayInclusive().isBefore(dto.getFirstDay()) &&
                        !e.getFirstDay().isAfter(dto.getLastDayInclusive()))) {
            throw new ApiException(HttpStatus.CONFLICT, "Nowy urlop koliduje z istniejącym urlopem.");
        }

        var yearlyLimit = timeOffLimitService.getById(dto.getYearlyLimitId());
        var usedHours = otherTimeOffs.stream().filter(e -> e.getTimeOffType().getId() == dto.getTypeId()).mapToInt(TimeOffEntity::getHoursCount).sum();
        if (yearlyLimit.getMaxHours() < usedHours + dto.getHoursCount()) {
            int hoursOverLimit = usedHours + dto.getHoursCount() - yearlyLimit.getMaxHours();
            if (hoursOverLimit == 1) {
                throw new ApiException(HttpStatus.CONFLICT, "Nowy urlop przekorczy limit o 1 godzinę");
            } else if (Integer.toString(hoursOverLimit).matches("[234]$")) {
                throw new ApiException(HttpStatus.CONFLICT, "Nowy urlop przekorczy limit o %d godziny".formatted(hoursOverLimit));
            }
            throw new ApiException(HttpStatus.CONFLICT, "Nowy urlop przekorczy limit o %d godzin".formatted(hoursOverLimit));
        }

        BeanUtils.copyProperties(dto, obj, "typeId", "employeeId", "yearlyLimitId");
        obj.setTimeOffType(timeOffTypeService.getById(dto.getTypeId()));
        obj.setEmployee(employeeService.getById(dto.getEmployeeId()));
        obj.setTimeOffYearlyLimit(yearlyLimit);
        timeOffRepository.save(obj);
    }

    @Transactional
    public void delete(@Min(1) long id) {
        timeOffRepository.delete(findOrElseThrow(id));
    }

    public TimeOffEntity findOrElseThrow(@Min(1) long id) {
        return timeOffRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Nie znaleziono urlopu"));
    }
}
