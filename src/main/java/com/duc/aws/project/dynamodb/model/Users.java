package com.duc.aws.project.dynamodb.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Builder;
import lombok.Data;

@DynamoDBTable(tableName = "Users")
@Entity
@Data
@Builder
public class Users {
	
	@DynamoDBHashKey(attributeName = "UserId")
	@DynamoDBAutoGeneratedKey
	@Id
	private String userId;
	
	@DynamoDBAttribute(attributeName = "Score")
	private int score;
	
	@DynamoDBAttribute(attributeName = "UserName")
	private String userName;
	
	@DynamoDBAttribute(attributeName = "Password")
	private String password;
}
