package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffTypeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffTypeGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffTypePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timeOff/type")
@Validated
@AllArgsConstructor
public class TimeOffTypeController {
    private final TimeOffTypeService timeOffTypeService;

    @GetMapping
    public ResponseEntity<List<TimeOffTypeGetDto>> getAll() {
        return ResponseEntity.ok(timeOffTypeService.getAll().stream().map(TimeOffTypeGetDto::fromEntity).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeOffTypeGetDto> getById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(TimeOffTypeGetDto.fromEntity(timeOffTypeService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<Void> post(@Valid TimeOffTypePostDto body) {
        timeOffTypeService.post(body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> put(@PathVariable @Min(1) long id, @Valid TimeOffTypePutDto body) {
        timeOffTypeService.put(id, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) long id) {
        timeOffTypeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
