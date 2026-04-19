package com.example.uhf_bt.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status_code")
    private int status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("token")
    private String token;
    
    public int getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getToken() {
        return token;
    }

}