package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffTypeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffTypeGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-offs/types")
@Validated
@AllArgsConstructor
public class TimeOffTypeController {
    private final TimeOffTypeService timeOffTypeService;

    @GetMapping
    public ResponseEntity<List<TimeOffTypeGetDto>> getAll() {
        return ResponseEntity.ok(timeOffTypeService.getAll().stream().map(TimeOffTypeGetDto::fromEntity).toList());
    }

    @PutMapping
    public ResponseEntity<Void> putAll(@Valid @RequestBody List<TimeOffTypePutDto> body) {
        timeOffTypeService.putAll(body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
