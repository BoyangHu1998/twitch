package com.boyangh.twitch.entity.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequestBody {

    private final String userId; // Represents the user's ID
    private final String password; // Represents the user's password

    // Constructor with @JsonCreator and @JsonProperty annotations to map JSON properties
    @JsonCreator
    public LoginRequestBody(@JsonProperty("user_id") String userId,
                            @JsonProperty("password") String password) {
        this.userId = userId;
        this.password = password;
    }

    // Getter method for userId
    public String getUserId() {
        return userId;
    }

    // Getter method for password
    public String getPassword() {
        return password;
    }
}

