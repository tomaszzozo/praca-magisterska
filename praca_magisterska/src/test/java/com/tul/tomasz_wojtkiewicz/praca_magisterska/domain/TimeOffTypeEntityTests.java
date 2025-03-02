package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@SpringBootTest
class TimeOffTypeEntityTests {
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;

    @AfterEach
    void afterEach() {
        timeOffTypeRepository.deleteAll();
    }

    @Test
    void nameValidation() {
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        type.setName("");
        Assertions.assertThrows(ValidationException.class, () -> timeOffTypeRepository.save(type));
        type.setName(null);
        Assertions.assertThrows(ValidationException.class, () -> timeOffTypeRepository.save(type));
        type.setName("a");
        Assertions.assertDoesNotThrow(() -> timeOffTypeRepository.save(type));
    }

    @Test
    void nameUniqueness() {
        timeOffTypeRepository.save(DefaultTestObjects.getTimeOffTypeEntity());
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffTypeRepository.save(DefaultTestObjects.getTimeOffTypeEntity()));
    }

    @Test
    void compensationPercentageValidation() {
        List.of(-Float.MAX_VALUE, -123456f, -Float.MIN_VALUE, 100.1f, 123f, 1234f, 12345f, 123456f, Float.MAX_VALUE).forEach(e -> {
            var type = DefaultTestObjects.getTimeOffTypeEntity();
            type.setCompensationPercentage(e);
            Assertions.assertThrows(ValidationException.class, () -> timeOffTypeRepository.save(type));
        });
        List.of(0f, 2.23f, 25.86f ,50f, 75.75f, 100f).forEach(cp -> {
            var type = DefaultTestObjects.getTimeOffTypeEntity();
            type.setCompensationPercentage(cp);
            Assertions.assertDoesNotThrow(() -> timeOffTypeRepository.save(type));
            timeOffTypeRepository.deleteAll();
        });
    }

    @Test
    void validData() {
        Assertions.assertDoesNotThrow(() -> timeOffTypeRepository.saveAll(ValidDataProvider.getTimeOffTypes()));
    }
}
