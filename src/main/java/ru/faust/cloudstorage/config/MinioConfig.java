package ru.faust.cloudstorage.config;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.endpoint)
                .credentials(minioProperties.accessKeyId,
                        minioProperties.accessKeySecret)
                .build();
    }

    @Bean
    @ConfigurationProperties("minio")
    public MinioProperties minioProperties() {
        return new MinioProperties();
    }


    @Setter
    @Getter
    public static class MinioProperties {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
    }
}
