package ru.faust.cloudstorage.service.impl;

import io.minio.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.faust.cloudstorage.config.minio.MinioProperties;
import ru.faust.cloudstorage.service.FolderStorageService;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderStorageServiceImpl implements FolderStorageService {

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    @Override
    @SneakyThrows
    public void createFolder(String folderName, String currentDirectory) {
        String folderPrefix = folderName.endsWith("/") ? currentDirectory + folderName : currentDirectory + folderName + "/";

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(folderPrefix)
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                        .contentType("/application/x-directory")
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public void deleteFolder(String folderName) {
        String folderPrefix = folderName.endsWith("/") ? folderName : folderName + "/";

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .prefix(folderPrefix)
                        .recursive(true)
                        .build()
        );

        List<DeleteObject> objectsToDelete = new ArrayList<>();
        for (Result<Item> result : results) {
            objectsToDelete.add(new DeleteObject(result.get().objectName()));
        }
        if (!objectsToDelete.isEmpty()) {
            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .objects(objectsToDelete)
                            .build()
            );
        }

    }

    @Override
    @SneakyThrows
    public void renameFolder(String oldFolderName, String newFolderName) {
        String oldPrefix = oldFolderName.endsWith("/") ? oldFolderName : oldFolderName + "/";
        String newPrefix = newFolderName.endsWith("/") ? newFolderName : newFolderName + "/";

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .prefix(oldPrefix)
                        .recursive(true)
                        .build()
        );

        List<String> objectsToDelete = new ArrayList<>();

        for (Result<Item> result : results) {
            String oldFileName = result.get().objectName();
            String newFileName = oldFileName.replace(oldPrefix, newPrefix);

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(newFileName)
                            .source(
                                    CopySource.builder()
                                            .bucket(minioProperties.getBucketName())
                                            .object(oldFileName)
                                            .build()
                            )
                            .build()
            );
            objectsToDelete.add(oldFileName);
        }

        if (!objectsToDelete.isEmpty()) {
            List<DeleteObject> deleteObjects = objectsToDelete.stream()
                    .map(DeleteObject::new)
                    .toList();

            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .objects(deleteObjects)
                            .build()
            );
        }
    }
}
