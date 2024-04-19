package com.anusha.paradise.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;

@Configuration
public class AwsConfig {

    @Value("${app.config.aws.access_key_id}")
    private String awsAccessKeyId;

    @Value("${app.config.aws.secret_key_id}")
    private String awsSecretKeyId;

    @Value("${app.config.aws.session_token}")
    private String awsSessionToken;

    @Bean
    public AwsSessionCredentials awsBasicCredentials() {
        return AwsSessionCredentials.create(
                awsAccessKeyId,
                awsSecretKeyId,
                awsSessionToken);
    }

}
