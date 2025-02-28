package ru.faust.cloudstorage.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import ru.faust.cloudstorage.model.FileDetails;
import ru.faust.cloudstorage.util.FileResponse;

public interface FileStorageService {

    String upload(MultipartFile file);

    void delete(String fileName);

    InputStreamResource get(String fileName);

    void rename(String fileName, String newFileName);

}
