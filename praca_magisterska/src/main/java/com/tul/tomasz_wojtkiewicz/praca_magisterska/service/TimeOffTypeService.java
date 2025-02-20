package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffTypePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
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
public class TimeOffTypeService {
    private final TimeOffTypeRepository timeOffTypeRepository;

    public List<TimeOffTypeEntity> getAll() {
        return timeOffTypeRepository.findAll();
    }

    public TimeOffTypeEntity getById(@Min(1) long id) {
        return timeOffTypeRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Typ urlopu z podanym id nie istnieje."));
    }

    @Transactional
    public void post(@Valid TimeOffTypePostDto dto) {
        if (timeOffTypeRepository.existsByName(dto.getName())) {
            throw new ApiException(HttpStatus.CONFLICT, "Typ urlopu z podaną nazwą już istnieje.");
        }
        var result = new TimeOffTypeEntity();
        BeanUtils.copyProperties(dto, result);
        timeOffTypeRepository.save(result);
    }

    @Transactional
    public void put( @Min(1) long id, @Valid TimeOffTypePutDto dto) {
        var original = getById(id);
        if (!original.getName().equals(dto.getName()) && timeOffTypeRepository.existsByName(dto.getName())) {
            throw new ApiException(HttpStatus.CONFLICT, "Typ urlopu z podaną nazwą już istnieje.");
        }
        BeanUtils.copyProperties(dto, original);
        timeOffTypeRepository.save(original);
    }

    @Transactional
    public void delete(@Min(1) long id) {
        var original = getById(id);
        if (!original.getYearlyCompensations().isEmpty() || !original.getYearlyLimits().isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "Nie można usunać tego typu urlopu, ponieważ jest on używany przez istniejące zasoby.");
        }
        timeOffTypeRepository.deleteById(id);
    }
}
