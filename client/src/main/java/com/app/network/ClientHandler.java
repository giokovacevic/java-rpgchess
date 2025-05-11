package com.app.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    Socket socket;
    PrintWriter output;
    BufferedReader input;
   
    public ClientHandler() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream());
    }

    public void sendMessage(String message) {
        output.println(message);
        output.flush();
    }

    public String getResponse() throws IOException{
        String response = input.readLine();
        return response;
    }

    public void close() throws IOException{
        input.close();
        output.close();
        socket.close();
    }
}
