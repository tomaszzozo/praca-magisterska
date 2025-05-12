package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class NameValidatorUnitTests {
    @Mock
    private ConstraintValidatorContext ctx;

    @Test
    void given_invalidName_when_isValidIsCalled_then_itReturnsFalse() {
        List.of(" jan", "Kowalski ", "123Nowak", "Marek#Nowak", "-Nowak", "Nowak--Jackson", "Kowal..Ski", "", "A", "a", "Kowalski123").forEach(name -> Assertions.assertFalse(new NameValidator().isValid(name, ctx), name));
    }

    @Test
    void given_validName_when_isValidIsCalled_then_itReturnsTrue() {
        List.of("Jan", "Kowalski", "Nowak-Jackson", "Dąbrowski", "Zofia", "O'Connel", "von der Osten", "Wiśniewska", "Anna Maria", "jr.", "dr.", "Łucja", "Żółć").forEach(name -> Assertions.assertTrue(new NameValidator().isValid(name, ctx), name));
    }

    @Test
    void given_null_when_isValidIsCalled_then_itReturnsTrue() {
        Assertions.assertTrue(new NameValidator().isValid(null, ctx));
    }
}
