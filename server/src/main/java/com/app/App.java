package com.app;

import com.app.network.*;

// cd documents/projects/java-chess/server
// javac -d out src/main/java/com/app/App.java
// java -cp out com.app.App

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
