package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

// cd documents/projects/java-chess/client
// javac --module-path C:\javafx-sdk-21.0.5\lib --add-modules javafx.controls,javafx.fxml -d out src/main/java/com/Client.java
// java --module-path C:\javafx-sdk-21.0.5\lib --add-modules javafx.controls,javafx.fxml -cp out com.Client

public class Client extends Application{
    private Socket socket;  
    private BufferedReader input;
    private PrintWriter output;
    private Pane root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root = new Pane();
        root.setPrefSize(1200, 700);


        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Test");
        //new Thread(this::connect).start();
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        //sendMessage("exit");
        super.stop();
        //if (socket != null) socket.close();
        //if (input != null) input.close();
        //if (output != null) output.close();
    }

    /*private void connect() {
        try {
            socket = new Socket("localhost", 12345);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            sendMessage("ogi");

            // Continuously listen for messages from the server
            String messageFromServer;
            while ((messageFromServer = input.readLine()) != null) {
                String finalMessage = messageFromServer;
            }
        } catch (IOException e) {
            System.out.println(" Connection closed. ");;
        }
    }

    private void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        }
    }*/

}

//class 


/*try (Socket socket = new Socket("localhost", 12345);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server.");

            System.out.println(" Enter your username: ");
            String username = scanner.nextLine();
            output.println(username);

            // Start a new thread to listen for messages from the server
            new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = input.readLine()) != null) {
                        System.out.println(" " + serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            }).start();

            // Main loop to read input from the user and send to the server
            String message;
            while ((message = consoleInput.readLine()) != null) {
                output.println(message);
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Disconnected from the server.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
