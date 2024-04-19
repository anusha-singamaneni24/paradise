package com.anusha.paradise.service;

import com.anusha.paradise.model.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${s3BucketName}")
    private String BUCKET ;
    @Autowired
    AwsSessionCredentials awsSessionCredentials;
    @Autowired
    PropertyService propertyService;

    private String propertyId;

    public ResponseModel storeImage(MultipartFile file, String propertyId) {
        ResponseModel response = new ResponseModel();
        this.propertyId = propertyId;

        try {
            String resp = uploadFile(generateFileName(propertyId, Objects.requireNonNull(file.getContentType()).split("/")[1]), file.getInputStream());
            response.setStatus("SUCCESS");
            response.setMessage(resp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private String uploadFile(String fileName, InputStream inputStream) {
        try (S3Client client = S3Client.builder().region(Region.EU_NORTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsSessionCredentials))
                .build()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(fileName)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            PutObjectResponse response = client.putObject(request, RequestBody.fromInputStream(inputStream, inputStream.available()));
            propertyService.updateImageUrl(propertyId, fileName);
            return response.eTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateFileName(String propertyId, String contentType) {
        return propertyId + "/" + UUID.randomUUID() + "." + contentType;
    }

}
