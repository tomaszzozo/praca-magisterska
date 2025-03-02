package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
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
    public void putAll(@Valid List<TimeOffTypePutDto> dtos) {
        var ids = dtos.stream().map(TimeOffTypePutDto::getId).toList();
        var toDelete = timeOffTypeRepository.findAll().stream().filter(e -> !ids.contains(e.getId())).toList();
        if (toDelete.stream().anyMatch(e -> !e.getTimeOffs().isEmpty())) {
            throw new ApiException(HttpStatus.CONFLICT, "Typ urlopu jest używany - nie można usunąć");
        }
        timeOffTypeRepository.deleteAll(toDelete);
        dtos.forEach(dto -> {
            TimeOffTypeEntity entity;
            if (dto.getId() == 0) {
                if (timeOffTypeRepository.existsByName(dto.getName())) {
                    throw new ApiException(HttpStatus.CONFLICT, "Podana nazwa jest już zajęta");
                }
                entity = new TimeOffTypeEntity();

            } else {
                entity = getById(dto.getId());
                if (!dto.getName().equals(entity.getName()) && timeOffTypeRepository.existsByName(dto.getName())) {
                    throw new ApiException(HttpStatus.CONFLICT, "Podana nazwa jest już zajęta");
                }
            }
            BeanUtils.copyProperties(dto, entity, "id");
            timeOffTypeRepository.save(entity);
        });
    }
}
