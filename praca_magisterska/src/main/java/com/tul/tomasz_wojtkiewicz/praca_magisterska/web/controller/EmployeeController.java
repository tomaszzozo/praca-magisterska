package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.EmployeeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.EmployeeGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeGetDto>> getAll() {
        return ResponseEntity.ok(employeeService.getAll().stream().map(EmployeeGetDto::fromEntity).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeGetDto> getById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(EmployeeGetDto.fromEntity(employeeService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<Void> post(@Valid @RequestBody EmployeePostDto body) {
        employeeService.post(body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> put(@PathVariable @Min(1) long id, @Valid @RequestBody EmployeePutDto body) {
        employeeService.put(id, body);
        return ResponseEntity.ok().build();
    }
}
