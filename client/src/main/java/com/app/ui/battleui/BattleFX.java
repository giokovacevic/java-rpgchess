package com.app.ui.battleui;

import java.io.IOException;
import java.util.ArrayList;

import com.app.core.entities.behaviour.*;
import com.app.fsm.BattleState;
import com.app.network.ClientHandler;
import com.app.ui.HomeFX;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BattleFX extends Pane{
    private final int BATTLEFX_WIDTH = 1700;
    private final int BATTLEFX_HEIGHT = 1000;
    private final int BOARDFX_WIDTH, BOARDFX_HEIGHT;
    private final int DASHBOARDFX_WIDTH, DASHBOARDFX_HEIGHT;
    private Stage stage;
    private ClientHandler clientHandler;
    private volatile boolean running = true;
    private DashboardFX dashboardFX;
    private BoardFX boardFX;
    private Label notificationLabel;
    private BattleState currentState = null;
    private int myBattleId;
    private int activeFieldId = -1;

    public BattleFX(Stage stage, ClientHandler clientHandler) throws IOException{
        super();

        this.stage = stage;
        this.clientHandler = clientHandler;

        setPrefSize(BATTLEFX_WIDTH, BATTLEFX_HEIGHT);
        BOARDFX_WIDTH = BATTLEFX_WIDTH;
        BOARDFX_HEIGHT = 880;

        this.notificationLabel = new Label("Loading.....");
        this.notificationLabel.setPrefSize(400, 300);
        this.notificationLabel.setLayoutX((BATTLEFX_WIDTH - 400)/2);
        this.notificationLabel.setLayoutY((BATTLEFX_HEIGHT-300) /2);
        this.notificationLabel.setAlignment(Pos.CENTER);
        this.notificationLabel.setStyle("-fx-background: white; -fx-font-size: 30px;");
        this.getChildren().add(this.notificationLabel);

        this.boardFX = new BoardFX(BOARDFX_WIDTH, BOARDFX_HEIGHT); 
        DASHBOARDFX_WIDTH = BATTLEFX_WIDTH;
        DASHBOARDFX_HEIGHT = BATTLEFX_HEIGHT - BOARDFX_HEIGHT;
        this.boardFX.setVisible(false);
        
        dashboardFX = new DashboardFX(DASHBOARDFX_WIDTH, DASHBOARDFX_HEIGHT, 0, BOARDFX_HEIGHT);
        this.dashboardFX.setVisible(false);
        getChildren().addAll(boardFX, dashboardFX);

        startListening();
    }

    // Functions
    public void startListening() {
        new Thread(() -> {
            try {
                String message = "";
                while((message = clientHandler.getResponse()) != null && running) {
                    processMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }).start();
    }

    public synchronized void processMessage(String message) {
        System.out.println("Received:\n" + message);
        String[] messageLines = message.split(";");
        String[] actionComponents = messageLines[0].split(" ");
        String action = actionComponents[0];

        switch (action) {
            case "init":
                changeState(BattleState.INITIALIZING, messageLines);
                break;
            case "start":
                changeState(BattleState.STARTING, messageLines);
                break;
            case "turn":
                changeState(BattleState.PREPARING, messageLines);
                break;
            case "moving":
                changeState(BattleState.MOVING, messageLines);
                break;
            case "move":
                handleMove(message);
                break;
            case "attacking":
                changeState(BattleState.ATTACKING, messageLines);
                break;
            case "attack":  
                handleAttack(message);
                break;
            case "end":
                if(this.currentState != BattleState.CONCLUDING) changeState(BattleState.CONCLUDING, messageLines);
                break;
            default:
                break;
        }
    }

    private void updateBoardFX(String[] boardLines) {
        
    }

    private void setupDashboard() {
        this.dashboardFX.getMove().setOnMouseClicked(e -> {
            changeState(BattleState.WAITING, null);
            clientHandler.sendMessage("moving " + String.valueOf(this.myBattleId) + " " + String.valueOf(this.activeFieldId));
        });

        this.dashboardFX.getAttack().setOnMouseClicked(e -> {
            changeState(BattleState.WAITING, null);
            clientHandler.sendMessage("attacking " + String.valueOf(this.myBattleId) + " " + String.valueOf(this.activeFieldId));
        });

        this.dashboardFX.getSkip().setOnMouseClicked(e -> {
            changeState(BattleState.WAITING, null);
            clientHandler.sendMessage("skip " + String.valueOf(myBattleId) + " " + String.valueOf(this.activeFieldId));
        });

        this.boardFX.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.SECONDARY) {
                if(this.currentState == BattleState.MOVING || this.currentState == BattleState.ATTACKING) changeState(BattleState.PLANNING, null);
            }
        });
    }

    private ArrayList<Integer> extractValidTargets(String targetsString) {
        System.out.println(targetsString);
        ArrayList<Integer> validTargets = new ArrayList<>();
        if (!targetsString.equals("-")) {
            String[] targets = targetsString.split(" ");
            for(String t : targets){
                validTargets.add(Integer.parseInt(t));
            }
        }
        return validTargets;
    }

    private void handleMove(String message) {
        String[] messageComponents = message.split(";");
        String[] actionComponents = messageComponents[0].split(" ");
        
        int controllerId = Integer.parseInt(actionComponents[1]);
        int sourceId = Integer.parseInt(actionComponents[2]);
        int targetId = Integer.parseInt(actionComponents[3]);

        this.activeFieldId = targetId;
        
        Platform.runLater(() -> {
            this.boardFX.getFieldFXs().get(sourceId).defocus();
            for(int i=2; i<messageComponents.length; i++) {
                int id = Integer.parseInt(messageComponents[i].split(" ")[0]);
                this.boardFX.getFieldFXs().get(id).update(messageComponents[i], myBattleId);
            }
            this.boardFX.getFieldFXs().get(targetId).focus(BoardFX.getAlliance(controllerId, myBattleId));
        });


        if(controllerId == this.myBattleId) {
            changeState(BattleState.MOVING, messageComponents);
        }
    }

    private void handleAttack(String message) {
        String[] messageComponents = message.split(";");
        String[] actionComponents = messageComponents[0].split(" ");
        
        int controllerId = Integer.parseInt(actionComponents[1]);
        int sourceId = Integer.parseInt(actionComponents[2]);
        int targetId = Integer.parseInt(actionComponents[3]);

        String[] damageComponents = messageComponents[1].split(" ");
        int[] damageTaken = new int[2];
        damageTaken[0] = Integer.parseInt(damageComponents[0]);
        damageTaken[1] = Integer.parseInt(damageComponents[1]);

        Platform.runLater(() -> {
            for(int i=2; i<messageComponents.length; i++) {
                int id = Integer.parseInt(messageComponents[i].split(" ")[0]);
                this.boardFX.getFieldFXs().get(id).update(messageComponents[i], myBattleId);
            }
            if(damageTaken[1] > 0) {
                this.boardFX.getFieldFXs().get(targetId).playDamageTakenAnimation(damageTaken);
            }else{
                this.boardFX.getFieldFXs().get(targetId).playDeathAnimation(damageTaken);
            }
        });
    }

    private void handleEnd(String winner) {
        this.setDisable(true);
        System.err.println("end");
    }

    // FSM
    private synchronized void changeState(BattleState newState, String[] messageLines) {
        if(this.currentState != null) {
            switch (currentState) {
                case INITIALIZING:
                    System.out.println("EXITING INITIALIZING");
                    handleInitializingStateExit();
                    break;
                case STARTING:
                    System.out.println("EXITING STARTING");
                    handleStartingStateExit();
                    break;    
                case PREPARING:
                    System.out.println("EXITING PREPARING");
                    handlePreparingStateExit();
                    break;
                case PLANNING:
                    System.out.println("EXITING PLANNING");
                    handlePlanningStateExit();
                    break;
                case MOVING:
                    System.out.println("EXITING MOVING");
                    handleMovingStateExit();
                    break;
                case ATTACKING:
                    System.out.println("EXITING ATTACKING");
                    handleAttackingStateExit();
                    break;
                case WAITING:
                    System.out.println("EXITING WAITING");
                    handleWaitingStateExit();
                    break;
                case SIMULATING:
                    System.out.println("EXITING SIMULATING");
                    handleSimulatingStateExit();                    
                    break;
                case CONCLUDING:
                    System.out.println("EXITING CONCLUDING");
                    handleConcludingStateExit();
                    break;
                default:
                    System.err.println(" Error: default exited");
                    break;
            }
        }
        
        this.currentState = newState;
        switch (this.currentState) {
            case INITIALIZING:
                System.out.println("INITIALIZING");
                handleInitializingStateEnter(messageLines);
                break;
            case STARTING:
                System.out.println("STARTING");
                handleStartingStateEnter(messageLines);
                break;  
            case PREPARING:
                System.out.println("PREPARING");
                handlePreparingStateEnter(messageLines);
                break;
            case PLANNING:
                System.out.println("PLANNING");
                handlePlanningStateEnter(messageLines);
                break;
            case MOVING:
                System.out.println("MOVING");
                handleMovingStateEnter(messageLines);
                break;
            case ATTACKING:
                System.out.println("ATTACKING");
                handleAttackingStateEnter(messageLines);
                break;
            case WAITING:
                System.out.println("WAITING");
                handleWaitingStateEnter(messageLines);
                break;
            case SIMULATING:
                System.out.println("SIMULATING");
                handleSimulatingStateEnter(messageLines);
                break;
            case CONCLUDING:
                System.out.println("CONCLUDING");
                handleConcludingStateEnter(messageLines);
                break;
            default:
                System.err.println(" Error: default entered");
                break;
        }
    }

    // EXIT Handlers
    private void handleInitializingStateExit() {

    }
    private void handleStartingStateExit() {
        Platform.runLater(() -> {
            this.notificationLabel.setVisible(false);
            this.boardFX.setVisible(true);
            this.dashboardFX.setVisible(true);
        }); 
    }
    private void handlePreparingStateExit() {

    }
    private void handlePlanningStateExit() { // temporary
    
    }
    private void handleMovingStateExit() {
        Platform.runLater(() -> {
            this.boardFX.disableMovementOnFields();
            this.boardFX.getFieldFXs().forEach((fieldId, field) -> {
                field.setOnMouseClicked(null);
            });
        });
    }
    private void handleAttackingStateExit() {
        Platform.runLater(() -> {
            this.boardFX.disableAttackingOnFields();
            this.boardFX.getFieldFXs().forEach((fieldId, field) -> {
                field.setOnMouseClicked(null);
            });
        });
    }
    private void handleWaitingStateExit() {
    
    }
    private void handleSimulatingStateExit() {
        
    }
    private void handleConcludingStateExit() {

    }

    // ENTER Handlers
    private void handleInitializingStateEnter(String[] messageLines) {
        String[] actionComponents = messageLines[0].split(" ");
        
        this.myBattleId = Integer.parseInt(actionComponents[1]);
        System.out.println("Initialized with id: " + myBattleId);

        String[] boardComponents = messageLines[1].split(" ");
        Platform.runLater(() -> {
            this.notificationLabel.setText("Initializing Game.....");
            this.boardFX.init(Integer.parseInt(boardComponents[1]), Integer.parseInt(boardComponents[2]), this.myBattleId);
        });
    }   
    private void handleStartingStateEnter(String[] messageLines) {
        Platform.runLater(() -> {
            this.notificationLabel.setText("Game Starting.....");
            for(int i=1; i<messageLines.length; i++) {
                int id = Integer.parseInt(messageLines[i].split(" ")[0]);
                this.boardFX.getFieldFXs().get(id).update(messageLines[i], myBattleId);
            }
            setupDashboard();
        });
        
    }
    private void handlePreparingStateEnter(String[] messageLines) {
        int prevActiveFieldId;
        prevActiveFieldId = this.activeFieldId;
       
        String[] idk = messageLines[0].split(" ");
        this.activeFieldId = Integer.parseInt(idk[1]);
        int controllerId = Integer.parseInt(idk[2]);
        Platform.runLater(() -> {
            this.boardFX.getFieldFXs().get(this.activeFieldId).focus(BoardFX.getAlliance(controllerId, this.myBattleId));
            if(prevActiveFieldId != (-1)) {
                this.boardFX.getFieldFXs().get(prevActiveFieldId).defocus();
            }
        });
        
        if(controllerId == myBattleId) {
            changeState(BattleState.PLANNING, null);
        }else{
            changeState(BattleState.WAITING, null);
        }
    }
    private void handlePlanningStateEnter(String[] messageLines) {
        Platform.runLater(() -> {
            this.dashboardFX.enable();
        });
        
    }
    private void handleMovingStateEnter(String[] messageLines) {
        Platform.runLater(() -> {
            this.dashboardFX.enable();
            this.dashboardFX.getMove().setDisable(true);
        });

        ArrayList<Integer> validTargets = extractValidTargets(messageLines[1]);

        Platform.runLater(() -> {
            this.boardFX.enableMovementOnFields(validTargets);
            this.boardFX.getFieldFXs().forEach((fieldId, field) -> {
                if(field.getHighlighted()) {
                    field.setOnMouseClicked(e -> {
                        if(e.getButton() == MouseButton.PRIMARY) {
                            changeState(BattleState.WAITING, null);
                            this.clientHandler.sendMessage("move " + String.valueOf(this.myBattleId) + " " + String.valueOf(this.activeFieldId) + " " + String.valueOf(((FieldFX)e.getSource()).getFieldId()));
                        }
                    });
                }else{
                    field.setOnMouseClicked(null);
                }
            });
        });
        
    }
    private void handleAttackingStateEnter(String[] messageLines) {
        Platform.runLater(() -> {
            this.dashboardFX.enable();
            this.dashboardFX.getAttack().setDisable(true);
        });

        ArrayList<Integer> validTargets = extractValidTargets(messageLines[1]);

        Platform.runLater(() -> {
            this.boardFX.enableAttackingOnFields(validTargets);
            this.boardFX.getFieldFXs().forEach((fieldId, field) -> {
                if(field.getHighlighted()) {
                    field.setOnMouseClicked(e -> {
                        if(e.getButton() == MouseButton.PRIMARY) {
                            changeState(BattleState.WAITING, null);
                            this.clientHandler.sendMessage("attack " + String.valueOf(this.myBattleId) + " " + String.valueOf(this.activeFieldId) + " " + String.valueOf(((FieldFX)e.getSource()).getFieldId()));
                        }
                    });
                }else{
                    field.setOnMouseClicked(null);
                }
            });
        });
    }
    private void handleWaitingStateEnter(String[] messageLines) {
        this.dashboardFX.disable();
    }
    private void handleSimulatingStateEnter(String[] messageLines) {
        
    }
    private void handleConcludingStateEnter(String[] messageLines) { // temporary (not good)
        int winner = Integer.parseInt(messageLines[0].split(" ")[1]);
        
        Platform.runLater(() -> {
            this.boardFX.setDisable(true);
            this.dashboardFX.setDisable(true);
            Button returnButton = new Button("Return");
            returnButton.setLayoutX(20);
            returnButton.setLayoutY(40);
            Label infoLabel = new Label();
            infoLabel.setLayoutX(20);
            infoLabel.setLayoutY(10);

            if(this.myBattleId == winner) {
                infoLabel.setText("YOU WON!");
            }else{
                infoLabel.setText("You Lost...");
            }
            Group group = new Group();
            group.getChildren().addAll(returnButton, infoLabel);
            this.getChildren().add(group);
            returnButton.setOnMouseClicked(e -> {
                clientHandler.sendMessage("conclude");
                this.running = false;
                stage.close();
                stage.setScene(new Scene(new HomeFX(stage, clientHandler)));
                stage.centerOnScreen();
                stage.show();
            });
        });
    }

    // Actions
    private synchronized void move(int fieldId) {
        
    }
    private synchronized void target() {

    }
    private synchronized void attack(int fieldId) {
        
    }
    private void skip() { // temporary?
        
    }
    
    // Setters and Getters
    public void setDashboardFX(DashboardFX dashboardFX) {
        this.dashboardFX = dashboardFX;
    }
    public void setBoardFX(BoardFX boardFX) {
        this.boardFX = boardFX;
    }
    public void setCurrentState(BattleState currentState) {
        this.currentState = currentState;
    }


    public DashboardFX getDashboardFX() { return dashboardFX; }
    public BoardFX getBoardFX() { return boardFX; }
    public BattleState getCurrentState() { return currentState; }
    public int getMyBattleId() { return this.myBattleId; }
}
