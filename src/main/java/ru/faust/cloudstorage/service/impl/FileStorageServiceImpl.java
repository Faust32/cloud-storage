package ru.faust.cloudstorage.service.impl;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.faust.cloudstorage.config.minio.MinioProperties;
import ru.faust.cloudstorage.exception.CreateBucketException;
import ru.faust.cloudstorage.exception.NoSuchFileException;
import ru.faust.cloudstorage.exception.UploadException;
import ru.faust.cloudstorage.service.FileStorageService;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    @Override
    public String uploadFile(MultipartFile file, String currentDirectory) {
        try {
            createBucket();
        } catch (Exception e) {
            throw new UploadException("File uploadFile failed: " + e.getMessage());
        }
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new UploadException("File must have name,");
        }
        String filePath = currentDirectory + generateFileName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            throw new UploadException("File uploadFile failed: " + e.getMessage());
        }
        saveFile(inputStream, filePath);
        return filePath;
    }

    private void createBucket() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioProperties.getBucketName())
                                .build()
                );
            }
        } catch (Exception e) {
            throw new CreateBucketException("Failed to create bucket: " + e.getMessage());
        }
    }

    private String generateFileName(MultipartFile file) {
        return file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

    @SneakyThrows
    private void saveFile(InputStream inputStream, String filePath) {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .stream(inputStream, inputStream.available(), -1)
                        .bucket(minioProperties.getBucketName())
                        .object(filePath)
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public void deleteFile(String filePath) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(filePath)
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public InputStreamResource getFile(String filePath) {
        InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(filePath)
                        .build()
        );
        return new InputStreamResource(inputStream);
    }

    @Override
    @SneakyThrows
    public void renameFile(String filePath, String newfilePath) {
        if (filePath.equals(newfilePath)) {
            return;
        }
        boolean exists = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(filePath)
                        .build()
        ) != null;
        if (!exists) {
            throw new NoSuchFileException("No such file: " + filePath);
        }
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(newfilePath)
                        .source(
                                CopySource.builder()
                                        .bucket(minioProperties.getBucketName())
                                        .object(filePath)
                                        .build()
                        )
                        .build()
        );
        deleteFile(filePath);
    }


}
