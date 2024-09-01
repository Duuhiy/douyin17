package org.example.entity;

import java.util.Date;

public class Favorite {
    private long userId;
    private long videoId;
    private Date createAt;
    private Date updateAt;
    private Date deleteAt;

    public Favorite(long userId, long videoId) {
        this.userId = userId;
        this.videoId = videoId;
    }
}
