package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action {
    private long videoId;
    private int root;
    private int parent;
    private long commentId;
    private int actionType;
    private String commentText;

}
