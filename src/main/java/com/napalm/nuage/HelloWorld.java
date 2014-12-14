package com.napalm.nuage;

/**
 * Created by Valentin on 14/12/14.
 */
public class HelloWorld {
    private String message;

    public HelloWorld(String message)
    {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
