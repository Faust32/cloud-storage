package ru.faust.cloudstorage.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadFile(MultipartFile file, String currentDirectory);

    void deleteFile(String filePath);

    InputStreamResource getFile(String filePath);

    void renameFile(String filePath, String newFilePath);

}
