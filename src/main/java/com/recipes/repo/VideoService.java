package com.recipes.repo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.recipes.entity.Video;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class VideoService {
    private GridFsTemplate gridFsTemplate;
    private GridFsOperations operations;

    public String addVideo(String title, MultipartFile file) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "video");
        metaData.put("title", title);
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), file.getName(), file.getContentType(), metaData);
        return id.toString();
    }

    public Video getVideo(String id) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        Video video = new Video();
        video.setTitle(file.getMetadata().get("title").toString());
        video.setStream(operations.getResource(file).getInputStream());
        return video;
    }

    public void deleteVideo(String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }

    public List<Video> getAll() throws IOException {
        List<GridFSFile> files = new ArrayList<>();
        gridFsTemplate.find(new Query()).into(files);
        System.out.println("files " +  files);
        List<Video> result = new ArrayList<>();
        for (GridFSFile f: files){
            Video video = new Video();
            video.setId(f.getObjectId().toString());
            video.setTitle(f.getMetadata().get("title").toString());
            video.setStream(operations.getResource(f).getInputStream());
            video.setUrl("/videos/stream/" + f.getObjectId());
            result.add(video);
        }
        return result;
    }
}
