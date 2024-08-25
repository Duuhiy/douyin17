package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private long id;
    private long videoId;
    private long userId;
    private long root;
    private long parent;
    private String content;
    private Date ctime;
    private Date mtime;
}
