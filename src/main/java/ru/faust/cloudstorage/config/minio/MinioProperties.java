package ru.faust.cloudstorage.config.minio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinioProperties {

    private String bucketName;

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

}
