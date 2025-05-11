package com.app.core.entities;

import java.util.LinkedList;

public class Player {
    private String username;
    private LinkedList<Unit> party;
    private boolean isNpc;

    public Player(String username, boolean isNpc) {
        this.username = username;
        this.isNpc = isNpc;
        this.party = new LinkedList<>();
    }

    public String getUsername() { return this.username; }
    public LinkedList<Unit> getParty() { return this.party; }
}
