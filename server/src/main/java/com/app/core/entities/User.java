package com.app.core.entities;

import java.util.ArrayList;

import com.app.network.UserState;

public class User {
    protected String username;
    protected UserState userState;
    protected ArrayList<Unit> party = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }

    public boolean isNPC() {
        return false;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public String getUsername() {
        return this.username;
    }
    public UserState getUserState() { return this.userState; }
    public ArrayList<Unit> getParty() {
        return this.party;
    }
}
