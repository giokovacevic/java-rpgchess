package com.app.ui.battleui;

import javax.swing.GroupLayout.Alignment;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

import com.app.fsm.BattleState;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class FieldFX extends Group{
    public static final int FIELD_SIZE = 160;
    public final double FIELD_BACKGROUND_OPACITY_DEFAULT = 0.0;
    public final double FIELD_BACKGROUND_OPACITY_HOVERED = 0.10;
    public final double FIELD_BACKGROUND_OPACITY_HIGHLIGHTED = 0.15;
    public final double FIELD_OUTLINE_OPACITY_DEFAULT = 0.2;
    public final double FIELD_OUTLINE_OPACITY_FOCUSED = 1.0;
    public final int ACTION_BACKGROUND_WIDTH = 100;
    public final int ACTION_BACKGROUND_HEIGHT = 80;
    private static final Image ACTION_BACKGROUND_DEATH_DEFAULT = new Image("action_death_default.png");
    private static final Image ACTION_BACKGROUND_DEATH_CRIT = new Image("action_death_crit.png");
    private final int fieldId;
    private Image[] backgrounds, outlines;
    private ImageView background, outline;
    private EntityFX entityFX;

    private boolean highlighted;

    private Label label;

    public FieldFX(int fieldId) {
        super();
        this.fieldId = fieldId;

        this.backgrounds = new Image[2];
        this.backgrounds[0] = new Image("/tile_background_default.png");
        this.backgrounds[1] = new Image("/tile_background_targeted.png");
        this.background = new ImageView(backgrounds[0]);
        this.background.setOpacity(FIELD_BACKGROUND_OPACITY_DEFAULT);
        getChildren().add(this.background);

        this.outlines = new Image[3];
        this.outlines[0] = new Image("/tile_outline_default.png");
        this.outlines[1] = new Image("/tile_outline_onturn0.png");
        this.outlines[2] = new Image("/tile_outline_onturn1.png");
        this.outline = new ImageView(outlines[0]);
        this.outline.setOpacity(FIELD_OUTLINE_OPACITY_DEFAULT);
        getChildren().add(this.outline);

        enableTargeting();

        this.label = new Label(String.valueOf(this.fieldId));
        this.label.setLayoutX(20);
        this.label.setLayoutY(95);
        this.label.setStyle("-fx-font-size:20px;");
        getChildren().add(this.label);
    }

    // Functions
    public void update(String entityString, int myBattleId) {
        String[] entityData = entityString.split(" ");
        setEntityFX(entityData, myBattleId);
    }

    public void enableTargeting() {
        setOnMouseEntered(e -> {
            if(getHighlighted()) {
                this.background.setOpacity(FIELD_BACKGROUND_OPACITY_HOVERED + FIELD_BACKGROUND_OPACITY_HIGHLIGHTED);
            }else{
                this.background.setOpacity(FIELD_BACKGROUND_OPACITY_HOVERED);
            }
            setCursor(Cursor.HAND);
        });
        setOnMouseExited(e -> {
            if(getHighlighted()) {
                this.background.setOpacity(FIELD_BACKGROUND_OPACITY_DEFAULT + FIELD_BACKGROUND_OPACITY_HIGHLIGHTED);
            }else{
                this.background.setOpacity(FIELD_BACKGROUND_OPACITY_DEFAULT);
            }
            setCursor(Cursor.DEFAULT);
        });
    }
    public void disableTargeting() {
        setOnMouseEntered(null);
        setOnMouseExited(null);
    }

    public void highlight(BattleState state) {
        this.highlighted = true;
        this.background.setOpacity(FIELD_BACKGROUND_OPACITY_HIGHLIGHTED);
        if(state == BattleState.MOVING) {

        }else if(state == BattleState.ATTACKING) {
            this.background.setImage(this.backgrounds[1]);
        }
    }
    public void dehighlight() {
        this.highlighted = false;
        this.background.setOpacity(FIELD_BACKGROUND_OPACITY_DEFAULT);
        this.background.setImage(this.backgrounds[0]);
    }

    public void focus(String alliance) {
        if(alliance.equals("ally")) {
            this.outline.setImage(this.outlines[1]);
        }else{
            this.outline.setImage(this.outlines[2]);
        }
        this.outline.setOpacity(FIELD_OUTLINE_OPACITY_FOCUSED);
    }
    public void defocus() {
        this.outline.setImage(this.outlines[0]);
        this.outline.setOpacity(FIELD_OUTLINE_OPACITY_DEFAULT);
    }

    public void playDamageTakenAnimation(int[] damage) {
        Group group = new Group();
        group.setLayoutX((this.FIELD_SIZE/2) - (this.ACTION_BACKGROUND_WIDTH/2));
        group.setLayoutY(this.FIELD_SIZE/2 - this.ACTION_BACKGROUND_HEIGHT - 20);
        
        Text damageText = new Text(String.valueOf(String.valueOf(damage[0])));
        damageText.setStroke(Color.BLACK);
        damageText.setStroke(Color.rgb(31, 0, 0));
        
        if(damage[1] == 1) {
            damageText.setFill(Color.rgb(245, 5, 5));
            damageText.setStyle("-fx-font-size: 32px;-fx-font-weight: bold;");
        }else{
            damageText.setFill(Color.rgb(255, 157, 0));
            damageText.setStyle("-fx-font-size: 36px;-fx-font-weight: bold;");
        }

        Label damageLabel = new Label();
        damageLabel.setGraphic(damageText);
        damageLabel.setAlignment(Pos.CENTER);
        damageLabel.setPrefSize(this.ACTION_BACKGROUND_WIDTH, this.ACTION_BACKGROUND_HEIGHT);
        
        group.getChildren().add(damageLabel);
        
        this.getChildren().add(group);
        
        double duration = 1.5;
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(duration), group);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        TranslateTransition moveUpTransition = new TranslateTransition(Duration.seconds(duration), group);
        moveUpTransition.setByY(-80);

        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, moveUpTransition);
        parallelTransition.setOnFinished(e -> {
            getChildren().remove(group);
        });

        parallelTransition.play();
    }
    public void playDeathAnimation(int[] damage) {
        Group group = new Group();
        group.setLayoutX((this.FIELD_SIZE/2) - (this.ACTION_BACKGROUND_WIDTH/2));
        group.setLayoutY((this.FIELD_SIZE - this.ACTION_BACKGROUND_HEIGHT)/2 - 10);

        ImageView deathImage;
        if(damage[1] == (-1)) {
            deathImage = new ImageView(ACTION_BACKGROUND_DEATH_DEFAULT);
        }else{
            deathImage = new ImageView(ACTION_BACKGROUND_DEATH_CRIT);
        }
        
        group.getChildren().add(deathImage);

        Text damageText = new Text(String.valueOf(String.valueOf(damage[0])));
        damageText.setFill(Color.WHITE);
        damageText.setStroke(Color.BLACK);
        damageText.setStyle("-fx-font-size: 30px;-fx-font-weight: bold;");

        Label damageLabel = new Label();
        damageLabel.setAlignment(Pos.CENTER);
        damageLabel.setPrefSize(this.ACTION_BACKGROUND_WIDTH, this.ACTION_BACKGROUND_HEIGHT);
        damageLabel.setGraphic(damageText);
        
        group.getChildren().add(damageLabel);

        getChildren().add(group);

        double duration = 1.5;
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(duration), group);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        TranslateTransition moveUpTransition = new TranslateTransition(Duration.seconds(duration), group);
        moveUpTransition.setByY(-80);

        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, moveUpTransition);
        parallelTransition.setOnFinished(e -> {
            getChildren().remove(group);
        });
        parallelTransition.play();
        
    }


    // Setters and Getters
    public void setEntityFX(String[] entityData, int myBattleId) {
        if(this.entityFX != null) {
            getChildren().remove(this.entityFX);
        }

        if(!(entityData[1].equals("-")) && !(entityData[0].equals("blocked"))) {
            this.entityFX = new EntityFX(entityData, myBattleId);
            getChildren().add(this.entityFX);
        }
    }
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public int getFieldId() { return this.fieldId; }
    public EntityFX getEntityFX() { return this.entityFX; }
    public boolean getHighlighted(){return this.highlighted;}
}
