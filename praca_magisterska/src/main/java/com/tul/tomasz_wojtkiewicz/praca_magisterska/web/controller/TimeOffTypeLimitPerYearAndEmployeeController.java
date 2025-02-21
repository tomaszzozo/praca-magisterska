package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffTypeLimitPerYearAndEmployeeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffTypeLimitPerYearAndEmployeeGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypeLimitPerYearAndEmployeePutDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-offs/types/limits")
@Validated
@AllArgsConstructor
public class TimeOffTypeLimitPerYearAndEmployeeController {
    private final TimeOffTypeLimitPerYearAndEmployeeService timeOffTypeLimitPerYearAndEmployeeService;

    @GetMapping(params = {"year", "employeeId"})
    public ResponseEntity<List<TimeOffTypeLimitPerYearAndEmployeeGetDto>> getAllByYearAndEmployee(@RequestParam @Range(min = 2020, max = 2100) int year, @RequestParam @Min(1) long employeeId) {
        return ResponseEntity.ok(timeOffTypeLimitPerYearAndEmployeeService.getAllByYear(year, employeeId).stream().map(TimeOffTypeLimitPerYearAndEmployeeGetDto::fromEntity).toList());
    }

    @PutMapping
    public ResponseEntity<Void> putAll(@RequestBody @Valid List<TimeOffTypeLimitPerYearAndEmployeePutDto> body) {
        timeOffTypeLimitPerYearAndEmployeeService.putAll(body);
        return ResponseEntity.ok().build();
    }
}
