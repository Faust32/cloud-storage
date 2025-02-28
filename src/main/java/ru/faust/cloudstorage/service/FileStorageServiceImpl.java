package ru.faust.cloudstorage.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.faust.cloudstorage.config.minio.MinioProperties;
import ru.faust.cloudstorage.exception.CreateBucketException;
import ru.faust.cloudstorage.exception.InvalidParameterException;
import ru.faust.cloudstorage.exception.NoSuchFileException;
import ru.faust.cloudstorage.exception.UploadException;
import ru.faust.cloudstorage.model.FileDetails;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) {
        try {
            createBucket();
        } catch (Exception e) {
            throw new UploadException("File upload failed: " + e.getMessage());
        }
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new UploadException("File must have name,");
        }
        String fileName = generateFileName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            throw new UploadException("File upload failed: " + e.getMessage());
        }
        saveFile(inputStream, fileName);
        return fileName;
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
    private void saveFile(InputStream inputStream, String fileName) {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .stream(inputStream, inputStream.available(), -1)
                        .bucket(minioProperties.getBucketName())
                        .object(fileName)
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public void delete(String fileName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(fileName)
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public InputStreamResource get(String fileName) {
        InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(fileName)
                        .build()
        );
        return new InputStreamResource(inputStream);
    }

    @Override
    @SneakyThrows
    public void rename(String fileName, String newFileName) {
        if (fileName.equals(newFileName)) {
            return;
        }
        boolean exists = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(fileName)
                        .build()
        ) != null;
        if (!exists) {
            throw new NoSuchFileException("No such file: " + fileName);
        }
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(newFileName)
                        .source(
                                CopySource.builder()
                                        .bucket(minioProperties.getBucketName())
                                        .object(fileName)
                                        .build()
                        )
                        .build()
        );
        delete(fileName);
    }


}
