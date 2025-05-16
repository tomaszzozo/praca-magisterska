package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Builder
@Getter
public class EmployeeTestEntityFactory {
    @Builder.Default
    private String email = "bruceWayne@gmail.com";
    @Builder.Default
    private String firstName = "Bruce";
    @Builder.Default
    private String lastName = "Wayne";
    @Builder.Default
    private String phoneNumber = "123456789";
    @Builder.Default
    private Integer accessLevel = 3;

    public static EmployeeTestEntityFactory build() {
        return builder().build();
    }

    public EmployeeEntity asEntity() {
        var entity = new EmployeeEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
