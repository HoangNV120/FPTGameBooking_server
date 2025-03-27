package com.server.controller;

import com.server.dto.request.reels.reelRequest;
import com.server.dto.response.common.ResponseGlobal;
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
    public Reel addReel(@RequestBody reelRequest request) {
        return reelService.addReel(request);
    }

    @PutMapping("/edit/{id}")
    public ResponseGlobal<Reel> editReel(@PathVariable String id,@RequestBody reelRequest request) {
        return new ResponseGlobal<>(reelService.editReel(id,request));
    }

    @PostMapping("/delete/{id}")
    public ResponseGlobal<Void> deleteReelById(@PathVariable String id) {
         reelService.deleteReel(id);
         return new ResponseGlobal<>();
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
