package com.duc.aws.project.dynamodb.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Builder;
import lombok.Data;

@DynamoDBTable(tableName = "Accounts")
@Entity
@Data
@Builder
public class Accounts {
	@DynamoDBAttribute(attributeName = "Score")
	private int score;
	
	@DynamoDBHashKey(attributeName = "UserName")
	@Id
	private String userName;
	
	@DynamoDBAttribute(attributeName = "Password")
	private String password;
}
