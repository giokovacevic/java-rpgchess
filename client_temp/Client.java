import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;

// cd documents/projects/java-chess/client_temp
// javac -d out -cp "lib\*;." Client.java
// java --module-path C:\javafx-sdk-21.0.5\lib --add-modules javafx.controls,javafx.fxml -cp out Client

public class Client extends Application{

    public static void main(String[] args) throws IOException{
        launch(args);
        
    }

    @Override
    public void start(Stage stage) throws IOException {
        
        Pane root = new Pane();

        GameFX gameFX = new GameFX();
        root.getChildren().add(gameFX);
        
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }
}
class GameFX extends Pane{
    private ClientHandler clientHandler;
    private ButtonFX[] buttons;
    private Label labelName, labelDesc;
    private Group group;

    public GameFX() throws IOException{
        super();
        setPrefSize(500,600);
        this.clientHandler = new ClientHandler();

        this.labelName = new Label("-");
        this.labelName.setPrefSize(300,30);
        this.labelName.setLayoutX(20);
        this.labelName.setLayoutY(10);
        this.getChildren().add(this.labelName);

        this.labelDesc = new Label("Waiting for other players...");
        this.labelDesc.setPrefSize(300,30);
        this.labelDesc.setLayoutX(20);
        this.labelDesc.setLayoutY(50);
        this.getChildren().add(this.labelDesc);

        this.group = new Group();
        this.group.setLayoutX(20);
        this.group.setLayoutY(90);

        this.buttons = new ButtonFX[10];
        for(int i=0; i<10; i++) {
            this.buttons[i] = new ButtonFX((i*(30+5)), 0, i+1);
            this.group.getChildren().add(this.buttons[i]);
        }

        this.group.setDisable(true);

        this.getChildren().add(this.group);

        for(ButtonFX button : this.buttons) {
            button.setOnMouseClicked(e -> {
                clientHandler.sendMessage("guess " + this.labelName.getText() + " " + String.valueOf(((ButtonFX)(e.getSource())).getData()));
                this.group.setDisable(true);
                startListening();
            });
        }

        this.setDisable(true);
        startListening();
    }

    public void startListening() {
        new Thread(() -> {
            try {
                String message = clientHandler.getResponse();
                processMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }).start();
    }

    public void processMessage(String message) {
        String[] messageComponents = message.split(" ");
        String action = messageComponents[0];

        switch (action) {
            case "init":
                handleInit(messageComponents[1]);
                break;
            case "onturn":
                System.out.println(action);
                handleOnTurn(messageComponents[1]);
                break;
            case "result":
                handleResult(messageComponents[1]);
                break;
            default:
                break;
        }
    }

    public void handleInit(String id) {
        Platform.runLater(() -> {
            this.labelName.setText(id);
            this.setDisable(false);
        });
        startListening();
    }

    public void handleOnTurn(String id) {
        if(id.equals(this.labelName.getText())) {
            Platform.runLater(() -> {
                this.labelDesc.setText("Your Turn! Guess a Number!");
                this.group.setDisable(false);    
            });
        }else{
            Platform.runLater(() -> {
                this.labelDesc.setText("Enemy Turn....."); 
            });
            startListening();
        }
    }

    public void handleResult(String winner) {
        if(winner.equals("-1")) {
            Platform.runLater(() -> {
                this.labelDesc.setText("It's a Draw!"); 
            });
        }else if(winner.equals(this.labelName.getText())){
            Platform.runLater(() -> {
                this.labelDesc.setText("YOU WIN"); 
            });
        }else{
            Platform.runLater(() -> {
                this.labelDesc.setText("You Lost....."); 
            });
        }
    }
}
class ClientHandler{
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

    String getResponse() throws IOException{//?
        String response = input.readLine();
        return response;
    }

    public void close() throws IOException{
        input.close();
        output.close();
        socket.close();
    }
}
class ButtonFX extends Button{
    private int data;
    
    public ButtonFX(int x, int y, int data) {
        super(String.valueOf(data));
        this.setPrefSize(30, 30);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.data = data;
    }

    public int getData() {
        return this.data;
    }
}