package com.server.dto.request.reels;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class reelRequest {
  private String title;
  private String video; // URL video
  private String image; // URL ảnh
  @Column(name = "funnymoment")
  private String funnyMoment;
}
