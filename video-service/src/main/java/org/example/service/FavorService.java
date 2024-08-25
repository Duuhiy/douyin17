package org.example.service;

import org.example.entity.FavoriteActionRequest;
import org.example.entity.Video;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FavorService {
    void action(FavoriteActionRequest action) throws ExecutionException, InterruptedException;
    void addFavor(FavoriteActionRequest action);
    List<Video> list(long userId);
}
