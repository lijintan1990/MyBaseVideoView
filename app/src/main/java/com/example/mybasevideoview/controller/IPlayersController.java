package com.example.mybasevideoview.controller;

public interface IPlayersController {
    void play();
    void stop();
    void pause();
    void seek(long time);
}
