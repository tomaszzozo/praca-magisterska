package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@AllArgsConstructor
@Validated
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<EmployeeEntity> getAll() {
        return employeeRepository.findAll();
    }

    public EmployeeEntity getById(@Min(1) long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pracownik o podanym id nie istnieje"));
    }

    @Transactional
    public void post(@Valid EmployeePostDto dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email jest już zajęty");
        } else if (employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ApiException(HttpStatus.CONFLICT, "Numer telefonu jest już zajęty");
        }
        var result = new EmployeeEntity();
        BeanUtils.copyProperties(dto, result);
        employeeRepository.save(result);
    }

    @Transactional
    public void put(@Min(1) long id, @Valid EmployeePutDto dto) {
        var original = getById(id);
        if (!original.getEmail().equals(dto.getEmail()) && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email jest już zajęty");
        } else if (!original.getPhoneNumber().equals(dto.getPhoneNumber()) && employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ApiException(HttpStatus.CONFLICT, "Numer telefonu jest już zajęty");
        }
        BeanUtils.copyProperties(dto, original);
        employeeRepository.save(original);
    }
}
