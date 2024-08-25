package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private long id;
    private String room;
    private long fromUser;
    private long toUser;
    private String content;
    private Date ctime;
}
