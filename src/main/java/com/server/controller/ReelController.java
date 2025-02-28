package com.server.controller;

import com.server.entity.Reel;
import com.server.service.impl.ReelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reels")
public class ReelController {

    private final ReelServiceImpl reelService;

    @Autowired
    public ReelController(ReelServiceImpl reelService) {
        this.reelService = reelService;
    }

    // API lấy danh sách tất cả Reel
    @GetMapping("/view")
    public List<Reel> getAllReels() {
        return reelService.getAllReels();
    }

    // API thêm Reel mới (tự động lấy postedBy)
    @PostMapping("/add")
    public Reel addReel(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String video = request.get("video");
        String image = request.get("image");

        return reelService.addReel(title, video, image);
    }

    @GetMapping("/find-video-by-id/{id}")
    public ResponseEntity<?> viewReelById(@PathVariable String id) {
        Optional<Reel> reel = reelService.getVideoById(id);

        if (reel.isPresent()) {
            return ResponseEntity.ok(reel.get());
        } else {
            return ResponseEntity.status(404).body("Reel not found");
        }
    }
}
