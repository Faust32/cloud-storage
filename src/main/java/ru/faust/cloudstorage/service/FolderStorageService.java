package ru.faust.cloudstorage.service;

public interface FolderStorageService {

    void createFolder(String folderName, String currentDirectory);

    void deleteFolder(String folderPath);

    void renameFolder(String folderPath, String newFolderPath);
}
