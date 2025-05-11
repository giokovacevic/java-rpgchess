package com.app.ui.battleui;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class DashboardFX extends Pane{
    private Button move, attack, skip;
    public DashboardFX(int width, int height, int x, int y) {
        super();
        setPrefSize(width, height);
        setLayoutX(x);
        setLayoutY(y);

        this.setStyle("-fx-background-color: #6437ab;");  // needs custom dashboard background
        
        this.move = new Button("Move");
        this.move.setPrefSize(200, 30);
        this.move.setLayoutX(200);
        this.move.setLayoutY(5);
        
        this.attack = new Button("Attack");
        this.attack.setPrefSize(200, 30);
        this.attack.setLayoutX(200);
        this.attack.setLayoutY(40);

        this.skip = new Button("Skip");
        this.skip.setPrefSize(200, 30);
        this.skip.setLayoutX(200);
        this.skip.setLayoutY(75);

        getChildren().addAll(this.move, this.attack, this.skip);
        
        disable();
    }

    public void enable() {
        this.move.setDisable(false);
        this.attack.setDisable(false);
        this.skip.setDisable(false);
    }

    public void disable() {
        this.move.setDisable(true);
        this.attack.setDisable(true);
        this.skip.setDisable(true);
    }

    public Button getMove() {
        return this.move;
    }
    public Button getAttack() {
        return this.attack;
    }
    public Button getSkip() {
        return this.skip;
    }
}
