package com.duc.aws.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RankingDTO {
	private int fromRank;
	private int toRank;
	private String userName;
	private int currentRank;
}
