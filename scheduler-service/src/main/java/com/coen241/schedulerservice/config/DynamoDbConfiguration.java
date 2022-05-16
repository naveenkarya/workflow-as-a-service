package com.coen241.schedulerservice.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDbConfiguration {

    @Value("${aws.dynamodb.endpoint}")
    private String dynamoDbEndpoint;
    @Value("${aws.dynamodb.accessKey}")
    private String awsAccessKey;
    @Value("${aws.dynamodb.secretKey}")
    private String awsSecretKey;

    @Bean
    public DynamoDBMapper getDynamoDbMapper() {
        return new DynamoDBMapper(getDynamoDbClient());
    }

    @Bean
    public AmazonDynamoDB getDynamoDbClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration(dynamoDbEndpoint, "us-east-1"))
                .withCredentials(getAwsCredentials())
                .build();
    }

    private AWSCredentialsProvider getAwsCredentials() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
    }
}
