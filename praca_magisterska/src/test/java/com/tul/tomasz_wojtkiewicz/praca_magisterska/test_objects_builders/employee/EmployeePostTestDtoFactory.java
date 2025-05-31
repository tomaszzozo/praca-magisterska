package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Builder
@Getter
public class EmployeePostTestDtoFactory {
	private static final EmployeeEntity defaultReference = EmployeeTestEntityFactory.build().asEntity();

	@Builder.Default
	private String email = defaultReference.getEmail();
	@Builder.Default
	private String firstName = defaultReference.getFirstName();
	@Builder.Default
	private String lastName = defaultReference.getLastName();
	@Builder.Default
	private String phoneNumber = defaultReference.getPhoneNumber();
	@Builder.Default
	private int accessLevel = defaultReference.getAccessLevel();

	public static EmployeePostTestDtoFactory build() {
		return builder().build();
	}

	public EmployeePostDto asDto() {
		var dto = new EmployeePostDto();
		BeanUtils.copyProperties(this, dto);
		return dto;
	}
}
