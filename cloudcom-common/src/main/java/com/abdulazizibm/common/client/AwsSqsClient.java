package com.abdulazizibm.common.client;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsSqsClient {

  @Bean
  public SqsClient sqsClient(@Value("${aws.region}") String region,
      @Value("${aws.endpoint}") String endpoint) {
    var builder = SqsClient.builder()
        .region(Region.of(region));

    if (!endpoint.isBlank()) {
      // LocalStack
      builder =
          builder
              .endpointOverride(URI.create(endpoint))
              .credentialsProvider(
                  StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")));
    } else {
      // real AWS Cloud
      builder = builder.credentialsProvider(DefaultCredentialsProvider.builder()
          .build());
    }
    return builder.build();

  }

}
