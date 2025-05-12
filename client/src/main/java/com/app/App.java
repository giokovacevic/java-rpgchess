package com.app;

import com.app.ui.LoginFX;
import com.app.ui.battleui.BattleFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// cd documents/projects/java-chess/client
// cp -r src/main/resources/* out
// java --module-path C:\javafx-sdk-21.0.5\lib --add-modules javafx.controls,javafx.fxml -cp out com.app.App



public class App extends Application{
    public static void main(String[] args) {
        /**/
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        //Pane root = new Pane();
        //BattleFX battleFX = new BattleFX(); // need to make logic on how to init playerBattleId

        //root.getChildren().add(battleFX);

        Scene scene = new Scene(new LoginFX(stage));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image("gameicon_helmet.png"));
        stage.setTitle("Test");
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }
}
