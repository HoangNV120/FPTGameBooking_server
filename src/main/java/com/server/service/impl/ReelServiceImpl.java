package com.server.service.impl;

import com.server.dto.request.reels.reelRequest;
import com.server.entity.Reel;
import com.server.entity.User;
import com.server.repository.ReelRepository;
import com.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class ReelServiceImpl {

    private final ReelRepository reelRepository;
    private final UserRepository userRepository; // Inject UserRepository

    @Autowired
    public ReelServiceImpl(ReelRepository reelRepository, UserRepository userRepository) {
        this.reelRepository = reelRepository;
        this.userRepository = userRepository;
    }

    // Lấy tất cả Reel
    public List<Reel> getAllReels() {
        return reelRepository.findAll();
    }

    // Lấy Reel theo ID (kiểu String)
    public Optional<Reel> getVideoById(String id) {
        return reelRepository.findById(id);
    }

    // Thêm một Reel mới, lấy họ tên từ tài khoản đăng nhập
    public Reel addReel(reelRequest request) {
        String fullName = getCurrentName(); // Lấy họ tên người dùng

        Reel reel = new Reel(request.getTitle(), request.getVideo(), request.getImage(), fullName);
        return reelRepository.save(reel);
    }

    public Reel editReel(String id, reelRequest request) {
        Reel reel = reelRepository.findById(id).orElse(null);
        if (request.getTitle().trim() != null){
            reel.setTitle(request.getTitle());
        }
        if (request.getVideo().trim() != null){
            reel.setVideo(request.getVideo());
        }
        if (request.getImage().trim() != null){
            reel.setImage(request.getImage());
        }
        return reelRepository.save(reel);
    }

    public void deleteReel(String id) {
         reelRepository.deleteById(id);
    }

    // Lấy họ tên từ tài khoản đăng nhập hiện tại
    private String getCurrentName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername(); // Lấy email từ UserDetails

            // Truy vấn database để lấy họ và tên từ email
            return userRepository.findByEmail(email)
                    .map(User::getFullName) // Lấy fullName
                    .orElse("Unknown User");
        } else {
            return "Unknown User"; // Trường hợp không phải UserDetails
        }
    }


}
