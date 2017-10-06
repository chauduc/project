package com.duc.aws.project.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class AuthDTO {
	private String username;
	private String password;
	private int score;
}
