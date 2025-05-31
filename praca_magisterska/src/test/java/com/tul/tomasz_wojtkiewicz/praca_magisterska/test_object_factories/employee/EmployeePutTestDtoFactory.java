package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Builder
@Getter
public class EmployeePutTestDtoFactory {
	private static final EmployeePostDto defaultReference = EmployeePostTestDtoFactory.build().asDto();

	@Builder.Default
	private String email = defaultReference.getEmail();
	@Builder.Default
	private String firstName = defaultReference.getFirstName();
	@Builder.Default
	private String lastName = defaultReference.getLastName();
	@Builder.Default
	private String phoneNumber = defaultReference.getPhoneNumber();
	@Builder.Default
	private Integer accessLevel = defaultReference.getAccessLevel();

	public static EmployeePutTestDtoFactory build() {
		return builder().build();
	}

	public EmployeePutDto asDto() {
		var dto = new EmployeePutDto();
		BeanUtils.copyProperties(this, dto);
		return dto;
	}
}
