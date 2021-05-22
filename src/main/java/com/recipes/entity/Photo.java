package com.recipes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Document(collection = "photos")
public class Photo {
    @Id
    private String id;

    private String title;

    private Binary image;

    private MultipartFile preimage;

    private String postimage;

    public Photo(String title) {
        this.title = title;
    }

    public String getImage(){
        this.postimage = Base64.getEncoder().encodeToString(this.image.getData());
        return this.postimage;
    }
}
