package com.etna.gunzbu_a.alldj.Classes;

/**
 * Created by Kevin_Tan on 31/08/16.
 */
public class ChatMessage {

    private String username;
    private String message;
    private long timeStamp;

    public ChatMessage(String username, String message, long timeStamp){
        this.username  = username;
        this.message   = message;
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}