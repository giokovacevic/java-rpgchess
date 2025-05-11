package com.app.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.app.core.entities.User;

public class UserHandler implements Runnable{
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private IServer server;
    private User user;

    private int battleId = -1;

    public UserHandler(Socket socket, IServer server, User user) throws IOException{
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream());
        this.server = server;
        this.user = user;
    }

    @Override
    public void run() {
        String message = ""; //!Thread.currentThread().isInterrupted() && 
        try {
            while((message = input.readLine()) != null) {
                System.out.println(" Message received: " + message);
                this.server.processRequest(this, message);
            }
        } catch (IOException e) {
            //System.out.println("Error in reading Line");
        } finally{
            try {
                close();    
            } catch (Exception e) {
                System.out.println("Error in closing UserHandler");
            }
            
        }
    }

    public void sendMessage(String message) {
        output.println(message);
        output.flush();
    }

    public void close() throws IOException{
        if(this.user!=null) {
            server.disconnectUser(this);
        }
        if (input != null) input.close();
        if (output != null) output.close();
        if (socket != null) socket.close();
    }

    // Setters and Getters
    public void setServer(IServer server) {
        this.server = server;
    }
    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public User getUser() { return this.user; }
    public int getBattleId() { return this.battleId; }
}
