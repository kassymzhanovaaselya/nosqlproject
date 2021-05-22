package com.recipes.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Data
public class Video {
    @Id
    private String id;
    private String title;
    private InputStream stream;
    private String url;
    private MultipartFile file;
}
