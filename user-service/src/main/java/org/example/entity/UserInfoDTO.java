package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private long id;
    private String name;
    private int followCount;
    private int followerCount;
    private int workCount;
    private int favoriteCount;
    private int totalFavorited;
    private String avatar;
    private String backgroundImage;
    private String signature;
    private boolean isFollow;
}
