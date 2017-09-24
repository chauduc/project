package com.duc.aws.project.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDTO {
	private String userName;
	private String password;
	private int score;
	private int ranking;
}
