package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffLimitService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffLimitGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-offs/types/limits")
@Validated
@AllArgsConstructor
public class TimeOffLimitController {
    private final TimeOffLimitService timeOffLimitService;

    @GetMapping
    public ResponseEntity<List<TimeOffLimitGetDto>> getAllByYearAndEmployee(@RequestParam @Range(min = 2020, max = 2100) int year, @RequestParam @Min(1) long employeeId) {
        return ResponseEntity.ok(timeOffLimitService.getAllByYearAndEmployeeId(year, employeeId).stream().map(TimeOffLimitGetDto::fromEntity).toList());
    }

    @PutMapping
    public ResponseEntity<Void> putAll(@RequestBody @Valid List<TimeOffLimitPutDto> body) {
        timeOffLimitService.putAll(body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
