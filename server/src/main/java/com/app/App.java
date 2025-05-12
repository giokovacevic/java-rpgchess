package com.app;

import com.app.network.*;

// cd documents/projects/java-chess/server
// javac -d out src/main/java/com/app/App.java
// java -cp out com.app.App

/*
 - UPDATE NPC: TARGETING -> WAITING -> ATTACKING
- WHEN I HOVER OVER FOR MOVEMENT AND ATTACK CHANGE DIRECTION OF MY CHARACTER


-NPC: 
2 PROBLEMS WITH NPC: 
  1. IF "IN RANGE" TILES ARE OCCUPIED, IT DOESN'T MOVE TOWARDS ENEMY. [HARD PROBLEM]

  */

public class App {
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server();
            server.start();
        } catch (Exception e) {
           System.out.println(" Failed to Start the Server");
        }
    }
}
