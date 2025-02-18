package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class EmployeeGetDto {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int accessLevel;
    private EmployeeGetDto() {
    }

    public static EmployeeGetDto fromEntity(EmployeeEntity entity) {
        var result = new EmployeeGetDto();
        BeanUtils.copyProperties(entity, result);
        return result;
    }
}
