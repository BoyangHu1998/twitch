package com.boyangh.twitch.service;

public class TwitchException extends RuntimeException {
    public TwitchException(String errMessage) {
        super(errMessage);
    }
}
