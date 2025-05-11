package main;

import java.io.*;
import java.net.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(12345);

        Socket clientSocket = serverSocket.accept();
        PrintWriter output = new PrintWriter(clientSocket.getOutputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        String inputMessage = "";
        // listener
        while(!(inputMessage.equals("X"))) {
            inputMessage = input.readLine();
            System.out.println(inputMessage);
            processMessage(inputMessage, output);
        }

        input.close();
        output.close();
        clientSocket.close();
        serverSocket.close();

    }

    public static void processMessage(String message, PrintWriter output) {
        String[] messageBuffer = message.split(" ");
        String action = messageBuffer[0];

        switch (action) {
            case "ready":
                sendMessage("init", output);
                break;
            case "guess":
                sendMessage(handleGuess(messageBuffer[1]), output);
                break;
            default:
                System.out.println("Invalid Message sent");
                break;
        }
    }

    public static void sendMessage(String message, PrintWriter output) {
        output.println(message);
        output.flush();
    }

    public static String handleGuess(String number) {
        int firstNumber = Integer.parseInt(number);
        int secondNumber = new Random().nextInt(1, 10+1);

        System.out.println(secondNumber);
        
        String message = "";

        if(firstNumber == secondNumber) {
            message = "draw";
        }else if(firstNumber > secondNumber) {
            message = "win";
        }else{
            message = "loss";
        }
        return message;
    }
}

/*class Server {
    ServerSocket serverSocket;
    ArrayList<ClientHandler> clients = new ArrayList<>();
    
    public Server() throws IOException{
        this.serverSocket = new ServerSocket(12345);
        start();
    }

    public void start() {
        int playersConnected = 0;
        while(playersConnected!=1) {
            try{
                System.out.println(" Waiting for client to connect...");
    
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                new Thread(client).start();
                playersConnected++;
            
    
            } catch (IOException e) {
                System.out.println(" Error in creating ServerSocket");
            }
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

}

class GameSession{

}

/*class ClientHandler implements Runnable{
    private Server server;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    //private String username;
    //private int playerId;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try{
            String message;
                while ((message = input.readLine()) != null) {
                    System.out.println("Received: " + message);
                    // Broadcast the message to all clients
                    server.broadcast(message);
                }
        } catch (IOException e) {
            System.out.println("Client Disconnected");
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }
}*/