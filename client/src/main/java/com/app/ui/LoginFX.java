package com.app.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.app.network.ClientHandler;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginFX extends Pane{
    private ClientHandler clientHandler;

    private Stage stage;

    private TextField textField;
    private Button button;
    private Label label;
   
    public LoginFX(Stage stage){
        super();
        this.stage = stage;

        setPrefSize(400, 400);
        this.textField = new TextField();
        this.textField.setPrefSize(300, 30);
        this.textField.setLayoutX(20);
        this.textField.setLayoutY(20);
        getChildren().add(this.textField);

        this.button = new Button("Login");
        this.button.setPrefSize(120, 30);
        this.button.setLayoutX(20);
        this.button.setLayoutY(70);
        getChildren().add(this.button);

        this.label = new Label("");
        this.label.setPrefSize(300, 30);
        this.label.setLayoutX(20);
        this.label.setLayoutY(120);
        getChildren().add(this.label);

        this.button.setOnMouseClicked(event -> {
            if(!textField.getText().equals("")) {
                Platform.runLater(() -> {
                    try {
                        this.clientHandler = new ClientHandler();
                        this.clientHandler.sendMessage(textField.getText());
                        this.button.setDisable(true);
                        this.textField.setDisable(true);
                        waitForServerResponse();
                    } catch (IOException exc) {
                        Platform.runLater(() -> {
                            label.setText("Server is down.");
                        });
                    }
                }); 
            }else{
                Platform.runLater(() -> {
                    this.label.setText("Username can't be empty.");
                }); 
            }
        });
    }

    public void waitForServerResponse() {
        new Thread(() -> {
            try {
                String response = "";
                response = clientHandler.getResponse();
                processResponse(response);
                System.out.println("Response Received:\n" + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }).start();
    }

    private void processResponse(String response) {
        String action = response.split(" ")[0];

        switch (action) {
            case "fail":
                Platform.runLater(() -> {
                    this.label.setText("Invalid user data or account is in use");
                    this.textField.setDisable(false);
                    this.button.setDisable(false);
                });
                break;
            case "success":
                Platform.runLater(() -> {
                    this.label.setText("");
                    stage.setScene(new Scene(new HomeFX(stage, clientHandler)));
                });
                break;
            default:
                break;
        }
    }
}
