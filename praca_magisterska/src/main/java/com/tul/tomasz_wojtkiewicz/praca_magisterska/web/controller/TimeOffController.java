package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffPutDto;
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
@RequestMapping("/time-offs")
@Validated
@AllArgsConstructor
public class TimeOffController {
    private final TimeOffService timeOffService;

    @GetMapping(params = {"year", "employeeId"})
    public ResponseEntity<List<TimeOffGetDto>> getAllByYearAndEmployeeId(@RequestParam @Range(min = 2020, max = 2100) int year, @RequestParam @Min(1) long employeeId) {
        return ResponseEntity.ok(timeOffService.getAllByYearAndEmployeeId(year, employeeId).stream().map(TimeOffGetDto::fromEntity).toList());
    }

    @PostMapping
    public ResponseEntity<Void> post(@Valid @RequestBody TimeOffPostDto body) {
        timeOffService.post(body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> post(@PathVariable @Min(1) long id, @Valid @RequestBody TimeOffPutDto body) {
        timeOffService.put(id, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) long id) {
        timeOffService.delete(id);
        return ResponseEntity.ok().build();
    }
}
