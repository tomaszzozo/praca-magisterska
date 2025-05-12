package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements
        ConstraintValidator<Name, String> {
    @Override
    public boolean isValid(String field,
                           ConstraintValidatorContext cxt) {
        return field == null || field.matches("^[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ][a-zA-Z '.,ęółśążźćńĘÓŁŚĄŻŹĆŃ-]+[a-zA-ZęółśążźćńĘÓŁŚĄŻŹĆŃ.]$");
    }
}
