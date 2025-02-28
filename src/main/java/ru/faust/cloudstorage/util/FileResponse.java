package ru.faust.cloudstorage.util;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponse extends RepresentationModel<FileResponse> {

    private String fileName;

    private String contentType;

    private Long fileSize;

    private Date createdAt;


}

