package com.app.ui;

import java.io.IOException;

import com.app.network.ClientHandler;
import com.app.ui.battleui.BattleFX;

import javafx.application.Platform;
import javafx.print.PageLayout;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class HomeFX extends Pane{
    private ClientHandler clientHandler;
    private Stage stage;

    private Button singleplayerButton, multiplayerButton;
    
    public HomeFX(Stage stage, ClientHandler clientHandler) {
        super();
        setPrefSize(400, 400);
        this.stage = stage;
        this.clientHandler = clientHandler;

        this.singleplayerButton = new Button("Single Player");
        this.singleplayerButton.setPrefSize(140, 30);
        this.singleplayerButton.setLayoutX(20);
        this.singleplayerButton.setLayoutY(20);
        this.singleplayerButton.setDisable(true);   // temporary
        getChildren().add(this.singleplayerButton);

        this.multiplayerButton = new Button("Multi Player");
        this.multiplayerButton.setPrefSize(140, 30);
        this.multiplayerButton.setLayoutX(20);
        this.multiplayerButton.setLayoutY(70);
        getChildren().add(this.multiplayerButton);

       

        this.multiplayerButton.setOnMouseClicked( event -> {
            Platform.runLater(() -> {
                if(!this.multiplayerButton.getText().equals("Cancel")) {
                    this.multiplayerButton.setText("Cancel");
                    clientHandler.sendMessage("multiplayer");
                    waitForServerResponse();
                   
                }/*else{
                    this.multiplayerButton.setText("Multi Player");
                    clientHandler.sendMessage("multiplayer cancel");
                }*/
            });
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
            case "singleplayer":
                break;
            case "multiplayer":
                handleMultiPlayerResponse(response);
                break;
            case "ready":
                Platform.runLater(() -> {
                    try {
                        stage.close();
                        stage.setScene(new Scene(new BattleFX(stage, clientHandler)));
                        stage.centerOnScreen();
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            default:
                break;
        }
    }

    private void handleMultiPlayerResponse(String response) {
        String confirmation = response.split(" ")[1];
        if(confirmation.equals("accept")) {
            waitForServerResponse();
        }else if(confirmation.equals("deny")){
            Platform.runLater(() -> {
                this.multiplayerButton.setText("Multi Player");
            });
        }
    }
}
