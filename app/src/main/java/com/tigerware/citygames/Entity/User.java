package com.tigerware.citygames.Entity;

import java.io.Serializable;

/**
 * Created by User on 10.05.2017.
 */

public class User implements Serializable {
    private int id;
    private String username;
    private String email;
    private int hash;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }
}
