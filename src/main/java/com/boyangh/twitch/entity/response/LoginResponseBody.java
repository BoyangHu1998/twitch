package com.boyangh.twitch.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseBody {

    @JsonProperty("user_id")
    private final String userId;  // Holds the user's ID from the response

    @JsonProperty("name")
    private final String name;    // Holds the user's name from the response

    // Constructor initializing userId and name
    public LoginResponseBody(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    // Getter method for userId
    public String getUserId() {
        return userId;
    }

    // Getter method for name
    public String getName() {
        return name;
    }
}
