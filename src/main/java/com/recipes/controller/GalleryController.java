package com.recipes.controller;

import com.recipes.entity.Car;
import com.recipes.entity.Photo;
import com.recipes.entity.Video;
import com.recipes.repo.PhotoRepo;
import com.recipes.repo.VideoService;
import com.recipes.service.PhotoService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@AllArgsConstructor
public class GalleryController {
    PhotoService photoService;
    VideoService videoService;
    PhotoRepo photoRepo;


    @GetMapping("/gallery")
    public String getAll(Model model) throws IOException {
        Photo photo = new Photo();
        Video video = new Video();
        model.addAttribute("photo", photo);
        model.addAttribute("video", video);
        List<Photo> photos = photoRepo.findAll();
        for (Photo ph: photos) {
            ph.setPostimage(ph.getImage());
        }
        model.addAttribute("photos", photos);
        List<Video> videos = videoService.getAll();
        System.out.println(videos);
        model.addAttribute("videos", videos);
        return "gallery";
    }

    @PostMapping("/photos/add")
    public String addPhoto(Model model, @Param("photo") Photo photo) throws IOException {
        String id = photoService.addPhoto(photo.getTitle(), photo.getPreimage());
        return "redirect:/gallery";
    }

    @GetMapping("/photos/{id}")
    public String getPhoto(@PathVariable String id, Model model) {
        Photo photo = photoService.getPhoto(id);
        model.addAttribute("title", photo.getTitle());
//        model.addAttribute("image",
//                Base64.getEncoder().encodeToString(photo.getImage().getData()));
        return "photos";
    }

    @GetMapping("/photos/delete/{id}")
    public String deletePhoto(@PathVariable("id") String id, Model model) throws Exception {
        Photo photo = photoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid photo Id:" + id));
        photoRepo.delete(photo);
        return "redirect:/gallery";
    }

    @PostMapping("/videos/add")
    public String addVideo(Model model, @Param("video") Video video) throws IOException {
        String id = videoService.addVideo(video.getTitle(), video.getFile());
        return "redirect:/gallery";
    }

    @GetMapping("/videos/{id}")
    public String getVideo(@PathVariable String id, Model model) throws Exception {
        Video video = videoService.getVideo(id);
        model.addAttribute("title", video.getTitle());
        model.addAttribute("url", "/videos/stream/" + id);
        return "videos";
    }

    @GetMapping("/videos/delete/{id}")
    public String deleteVideo(@PathVariable("id") String id, Model model) throws Exception {
        videoService.deleteVideo(id);
        return "redirect:/gallery";
    }

    @GetMapping("/videos/stream/{id}")
    public void streamVideo(@PathVariable String id, HttpServletResponse response) throws Exception {
        Video video = videoService.getVideo(id);
        FileCopyUtils.copy(video.getStream(), response.getOutputStream());
    }


}
