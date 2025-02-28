package ru.faust.cloudstorage.config.minio;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKeyId(),
                        minioProperties.getAccessKeySecret())
                .build();
    }

    @Bean
    @ConfigurationProperties("minio")
    public MinioProperties minioProperties() {
        return new MinioProperties();
    }

}
