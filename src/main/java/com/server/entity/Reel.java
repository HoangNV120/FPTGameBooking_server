package com.server.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reel")
@Getter
@Setter
@NoArgsConstructor
public class Reel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Tự động sinh UUID cho ID
    private String id;

    private String title;
    private String video; // URL video
    private String image; // URL ảnh

    private String postedBy; // Người đăng

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date postedDate;

    // Constructor để khởi tạo với title, video, image, postedBy
    public Reel(String title, String video, String image, String postedBy) {
        this.id = UUID.randomUUID().toString(); // Tự động tạo UUID
        this.title = title;
        this.video = video;
        this.image = image;
        this.postedBy = postedBy;
        this.postedDate = new Date(); // Gán ngày đăng hiện tại
    }


    public String getId() {
        return this.id;
    }
}
