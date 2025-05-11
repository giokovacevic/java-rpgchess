package com.app.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.app.core.entities.Creature;
import com.app.core.entities.Hero;
import com.app.core.entities.HeroClass;
import com.app.core.entities.User;
import com.app.core.entities.Weapon;
import com.app.core.entities.WeaponType;

public class Server implements IServer{
    private final static int PORT = 12345;
    private ServerSocket serverSocket;
    private ArrayList<UserHandler> onlineUsers = new ArrayList<>();
    private BattleServer battleServer = null;

    private volatile boolean running = true;
    
    public Server(){
    }

    public void start() throws IOException{
        this.serverSocket = new ServerSocket(PORT);
        System.out.println("Server starting ...");
       
        while(running) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream());

                String username = input.readLine();
                
                if(!isUserAlreadyConnected(username)) {
                    User newUser = new User(username);
                    newUser.setUserState(UserState.ONLINE);
                    newUser.getParty().add(new Creature("greywolf","Grey Wolf", 14, 5, new int[]{3, 5},0, 1, 100));
                    UserHandler userHandler = new UserHandler(socket, this, newUser);
                    connect(userHandler);
                }else{
                    output.println("fail");
                    output.flush();
                }
            }catch (IOException e) {
                if(socket != null) {
                    try {
                        socket.close();
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }   
                }
            }
        }    
    }

    public void stop() throws IOException{
        running = false;
        if(serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        System.out.println("Server stoped.");
    }

    @Override
    public void processRequest(UserHandler userHandler, String request) {
        String[] requestComponents = request.split(" ");
        String action = requestComponents[0];

        switch (action) {
            case "multiplayer":
                handleMultiPlayerBattleRequest(userHandler);
                break;
            default:
                break;
        }
    }

    private void handleMultiPlayerBattleRequest(UserHandler userHandler) {
        System.out.println(userHandler.getUser().getUsername() + " request multiplayer game");
        if(battleServer == null) {
            System.err.println("Starting new BattleServer");
            this.battleServer = new BattleServer(this);
        }
        
        if(this.battleServer.join(userHandler)) {
            userHandler.sendMessage("multiplayer accept");
            if(battleServer.isFull()){
                System.out.println("Server is FULL");
                battleServer.start();
            } 
        }else{
            userHandler.sendMessage("multiplayer deny");
        }
    }

    public void closeBattleServer() {
        this.battleServer = null;
    }

    public boolean isUserAlreadyConnected(String username) {
        for(UserHandler userHandler: this.onlineUsers) {
            if(userHandler.getUser().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void connect(UserHandler userHandler) {
        synchronized (this.onlineUsers) {
            this.onlineUsers.add(userHandler);
        }
        System.out.println(userHandler.getUser().getUsername() + " has connected.");
        userHandler.sendMessage("success");
        new Thread(userHandler).start();
    }

    @Override
    public void disconnectUser(UserHandler userHandler) {
        synchronized (this.onlineUsers) {
            onlineUsers.remove(userHandler);
        }
        System.out.println(userHandler.getUser().getUsername() + " has disconnected; new online count: " + this.onlineUsers.size());
    }
}
