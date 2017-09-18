package com.duc.aws.project.dto;

import com.duc.aws.project.model.User;
import com.duc.aws.project.model.User.UserBuilder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthDTO {
	private String username;
	private String password;
}
