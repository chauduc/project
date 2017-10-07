package com.duc.aws.project.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.duc.aws.project.config.DynamoDBConfig;
import com.duc.aws.project.dto.RankingDTO;

@Component
public class RankingDAO {

	@Autowired
	DynamoDBConfig dynamoDBConfig;

	private static final String TABLE_NAME = "Ranking";

	/**
	 * Get ranking
	 * 
	 * @param fromRank
	 * @param toRank
	 * @return
	 * @throws Exception 
	 */
	public List<RankingDTO> getRankingByRange(int fromRank, int toRank) throws Exception {
		List<RankingDTO> listRanking = new ArrayList<RankingDTO>();
		Table rankingTable = dynamoDBConfig.getTable(TABLE_NAME);

		ScanSpec scanSpec = new ScanSpec().withFilterExpression("#rank between :from_rank and :to_rank")
				.withNameMap(new NameMap().with("#rank", "CurrentRank"))
				.withValueMap(new ValueMap().withNumber(":from_rank", fromRank).withNumber(":to_rank", toRank));

		try {
			ItemCollection<ScanOutcome> items = rankingTable.scan(scanSpec);

			Iterator<Item> iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				RankingDTO account = new RankingDTO();
				account.setUserName(item.getString("UserName"));
				account.setCurrentRank(item.getInt("CurrentRank"));
				listRanking.add(account);
			}

		} catch (Exception e) {
			System.err.println("Unable to scan the table:");
			System.err.println(e.getMessage());
			throw new Exception("Errors");
		}

		return listRanking;
	}

	/**
	 * Get ranking by username
	 * 
	 * @param userName
	 * @return
	 * @throws Exception 
	 */
	public int getRankingByUserName(String userName) throws Exception {
		int rs = 0;
		Table accountTable = dynamoDBConfig.getTable(TABLE_NAME);

		QuerySpec spec = new QuerySpec().withKeyConditionExpression("UserName = :userName")
				.withValueMap(new ValueMap().withString(":userName", userName));

		try {
			ItemCollection<QueryOutcome> items = accountTable.query(spec);

			Iterator<Item> iterator = items.iterator();
			Item item = null;

			if (iterator.hasNext()) {
				item = iterator.next();
				rs = item.getInt("CurrentRank");
			}

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			throw new Exception("Errors");
		}
		return rs;
	}

}
