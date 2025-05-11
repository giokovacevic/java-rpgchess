package com.app.network;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerHandler implements Runnable{
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private BattleSession battleSession;

    public PlayerHandler(Socket socket, BattleSession battleSession) throws IOException{
        this.socket = socket;
        this.battleSession = battleSession;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {
        String message = "";
        try {
            while((message = input.readLine()) != null) {
                System.out.println(" Message received: " + message);
                battleSession.processMessage(message);
            }
        } catch (IOException e) {
            //this.battleSession.getPlayerHandlers().remove(this);
            System.out.println("Player disconnected");
        } finally{
            try {
                close();    
            } catch (Exception e) {
                System.out.println("Error in closing player thread");
            }
            
        }
    }

    public void sendMessage(String message) {
        output.println(message);
        output.flush();
    }

    public void close() throws IOException{
        if (input != null) input.close();
        if (output != null) output.close();
        if (socket != null) socket.close();
    }
}
