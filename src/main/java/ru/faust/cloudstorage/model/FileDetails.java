package ru.faust.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDetails {

    private String path;

    private long size;

    private String type;

    private String fileName;

}
