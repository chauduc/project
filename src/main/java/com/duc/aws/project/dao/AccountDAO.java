package com.duc.aws.project.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.duc.aws.project.config.DynamoDBConfig;
import com.duc.aws.project.dto.AccountDTO;
import com.duc.aws.project.dto.RankingDTO;

import javassist.expr.NewArray;

/**
 * @author m00306
 *
 */
@Component
public class AccountDAO {

	@Autowired
	DynamoDBConfig dynamoDBConfig;

	@Autowired
	RankingDAO rankingDAO;

	private static final String TABLE_NAME = "Accounts";

	private static final int DEFAULT_SCORE = 0;

	/**
	 * アカウント登録
	 * 
	 * @param account
	 * @return
	 */
	public boolean registerAccount(AccountDTO account) {
		try {
			Table accountTable = dynamoDBConfig.getTable(TABLE_NAME);

			Item item = new Item().withPrimaryKey("UserName", account.getUserName())
					.withString("Password", account.getPassword()).withNumber("Score", DEFAULT_SCORE);

			accountTable.putItem(item);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * アカウントのScore更新
	 * 
	 * @param account
	 * @return
	 */
	public boolean updateAccount(AccountDTO account) {
		Table accountTable = dynamoDBConfig.getTable(TABLE_NAME);

		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		expressionAttributeNames.put("#S", "Score");

		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		expressionAttributeValues.put(":val1", account.getScore());

		try {
			UpdateItemOutcome outcome = accountTable.updateItem("UserName", account.getUserName(), "set #S = :val1",
					expressionAttributeNames, expressionAttributeValues);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Find account by username
	 * 
	 * @param userName
	 * @return
	 */
	public AccountDTO findAccountByUserName(String userName) {
		Table accountTable = dynamoDBConfig.getTable(TABLE_NAME);

		QuerySpec spec = new QuerySpec().withKeyConditionExpression("UserName = :userName")
				.withValueMap(new ValueMap().withString(":userName", userName));
		try {
			ItemCollection<QueryOutcome> items = accountTable.query(spec);

			Iterator<Item> iterator = items.iterator();
			Item item = null;
			AccountDTO dto = new AccountDTO();
			while (iterator.hasNext()) {
				item = iterator.next();
				dto.setUserName(item.getString("UserName"));
				dto.setPassword(item.getString("Password"));
				dto.setRanking(rankingDAO.getRankingByUserName(item.getString("UserName")));
				dto.setScore(item.getInt("Score"));
				return dto;
			}

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		return null;
	}

	/**
	 * Find Account by ranking
	 * 
	 * @param listUserRanking
	 * @return
	 */
	public List<AccountDTO> findAccountByRank(List<RankingDTO> listUserRanking) {
		Table accountTable = dynamoDBConfig.getTable(TABLE_NAME);
		List<AccountDTO> rs = new ArrayList<AccountDTO>();

		// Create map-value for scan filter expression
		Map<String,Object> mapOptions = new HashMap<String,Object>();
		List<String> listValue = new ArrayList<String>();
		int i = 0;
		for (RankingDTO account : listUserRanking) {
			mapOptions.put(":u" + i, account.getUserName());
			listValue.add(":u" + i);
			i++;
		}
		
		ScanSpec scanSpec = new ScanSpec().withFilterExpression("#userName IN ("+ StringUtils.join(listValue, ",") +")")
				.withNameMap(new NameMap().with("#userName", "UserName"))
				.withValueMap(mapOptions);
		
		ItemCollection<ScanOutcome> items = accountTable.scan(scanSpec);

		Iterator<Item> iterator = items.iterator();
		Item item = null;

		while (iterator.hasNext()) {
			item = iterator.next();
			AccountDTO dto = new AccountDTO();
			dto.setUserName(item.getString("UserName"));
			dto.setPassword(item.getString("Password"));
			for (RankingDTO item2 : listUserRanking) {
				if (item2.getUserName().equals(item.getString("UserName"))) {
					dto.setRanking(item2.getCurrentRank());
				}
			}
			dto.setScore(item.getInt("Score"));
			rs.add(dto);
		}
		return rs;
	}
}
