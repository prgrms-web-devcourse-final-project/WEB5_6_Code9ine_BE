package com.grepp.spring.infra.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class S3PresignedUrlService {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 허용된 이미지 파일 형식
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public String generatePresignedUploadUrl(String fileExtension) {
        // 파일 확장자 검증
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. 허용된 형식: " + ALLOWED_EXTENSIONS);
        }

        String fileName = UUID.randomUUID() + "." + fileExtension.toLowerCase();
        String contentType = getContentType(fileExtension);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .contentLength(MAX_FILE_SIZE) // 최대 파일 크기 제한
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(builder ->
                builder.signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(objectRequest)
        );
        URL url = presignedRequest.url();
        presigner.close();
        return url.toString();
    }

    // 기본 메서드 (jpg로 고정)
    public String generatePresignedUploadUrl() {
        return generatePresignedUploadUrl("jpg");
    }

    private String getContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> throw new IllegalArgumentException("지원하지 않는 파일 형식: " + extension);
        };
    }

    // 허용된 파일 형식 조회
    public List<String> getAllowedExtensions() {
        return ALLOWED_EXTENSIONS;
    }

    // 최대 파일 크기 조회
    public long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
}