package com.duc.aws.project.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.duc.aws.project.config.DynamoDBConfig;
import com.duc.aws.project.dto.RankingDTO;

@Component
public class RankingDAO {

	@Autowired
	DynamoDBConfig dynamoDBConfig;

	private static final String TABLE_NAME = "Ranking";

	/**
	 * @param fromRank
	 * @param toRank
	 * @return
	 */
	public List<RankingDTO> getRankingByRange(int fromRank, int toRank) {
		List<RankingDTO> listRanking = new ArrayList<RankingDTO>();
		return listRanking;
	}

	/**
	 * @param userName
	 * @return
	 */
	public int getCurrentRanking(String userName) {
		int rs = 0;
		return rs;
	}

	/**
	 * Used by Batch
	 * 
	 * @return
	 */
	public boolean updateRanking() {
		return true;
	}

}
