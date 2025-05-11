package com.app.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ErrorFX extends Pane{
    private Label label;

    public ErrorFX(String errorMessage) {
        super();

        setPrefSize(200, 200);

        this.label = new Label(errorMessage);
        this.label.setStyle("-fx-font-size: 20px;");

        getChildren().add(this.label);
    }
}
