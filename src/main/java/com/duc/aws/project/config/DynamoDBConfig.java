package com.duc.aws.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

/**
 * @author m00306
 *
 */
@Configuration
public class DynamoDBConfig {
	@Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;


    /**
     * @param tableName
     * @return
     */
    public Table getTable(String tableName) {
    	Table table = this.amazonDynamoDB().getTable(tableName);
    	return table;
    }
    
    /**
     * @return
     */
    @SuppressWarnings("deprecation")
    public DynamoDB amazonDynamoDB() {
    	AmazonDynamoDBClient client = new AmazonDynamoDBClient(this.amazonAWSCredentials());
    	client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
    	DynamoDB amazonDynamoDB = new DynamoDB(client);
        return amazonDynamoDB;
    }
    
    @SuppressWarnings("deprecation")
    public AmazonDynamoDBClient amazonDynamoDBClient() {
    	AmazonDynamoDBClient client = new AmazonDynamoDBClient(this.amazonAWSCredentials());
    	client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
    	return client;
    }

    /**
     * @return
     */
    private ProfileCredentialsProvider amazonAWSCredentials() {
        return new ProfileCredentialsProvider();
    }
}
