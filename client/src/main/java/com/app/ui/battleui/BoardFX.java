package com.app.ui.battleui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.app.fsm.BattleState;

import javafx.scene.Group;
import javafx.scene.layout.Pane;

public class BoardFX extends Pane{
    private final int FIELDS_SPACING_VERTICAL = 5;
    private final int FIELDS_SPACING_HORIZONTAL = 5;
    private final int FIELDS_STARTING_X = 70; // BOARD IS NOT CENTERED IT'S FIXED TO START AT 70 (CHANGE LATER)
    private final int FIELDS_STARTING_Y = 60;
    private HashMap<Integer, FieldFX> fieldFXs;

    public BoardFX(int width, int height){
        super();
        setPrefSize(width, height);
        setStyle("-fx-background-color: #7e50c7;"); // to be changed ( background, needs map background )... 
        this.fieldFXs = new HashMap<>();
    }

    public void init(int rows, int columns, int myBattleId) {
        for(int row=0; row<rows; row++) {
            for(int column=0; column<columns; column++) {
                FieldFX field;
              
                field = new FieldFX((row*columns)+column);

                if(row % 2 == 0) {
                    field.setLayoutX(FIELDS_STARTING_X + column*(FieldFX.FIELD_SIZE + FIELDS_SPACING_HORIZONTAL));
                    field.setLayoutY(FIELDS_STARTING_Y + row*(FieldFX.FIELD_SIZE*0.75 + FIELDS_SPACING_VERTICAL));
                }else{
                    field.setLayoutX(FIELDS_STARTING_X + FieldFX.FIELD_SIZE/2 + column*(FieldFX.FIELD_SIZE + FIELDS_SPACING_HORIZONTAL));
                    field.setLayoutY(FIELDS_STARTING_Y + row*(FieldFX.FIELD_SIZE*0.75 + FIELDS_SPACING_VERTICAL));
                }

                this.fieldFXs.put((row*columns) + column, field);

                getChildren().add(field);
            }
        }
    }

    // Functions
    public void update() {

    }

    public void enableMovementOnFields(ArrayList<Integer> fieldIds) {
        this.fieldFXs.forEach((fieldId, field) -> {
            if(fieldIds.contains(fieldId)) {
                field.highlight(BattleState.MOVING);
            }else{
                field.disableTargeting();
            }
        });
    }
    public void disableMovementOnFields() {
        this.fieldFXs.forEach((fieldId, field) -> {
            if(field.getHighlighted()) field.dehighlight();
            field.enableTargeting();
        });
    }

    public void enableAttackingOnFields(ArrayList<Integer> fieldIds) {
        this.fieldFXs.forEach((fieldId, field) -> {
            if(fieldIds.contains(fieldId)) {
                field.highlight(BattleState.ATTACKING);
            }else{
                field.enableTargeting();
            }
        });
    }
    public void disableAttackingOnFields() {
        this.fieldFXs.forEach((fieldId, field) -> {
            if(field.getHighlighted()) field.dehighlight();
            field.enableTargeting();
        });
    }

    /*public void attack(int targetTileId) {
        BattleMember member = this.tiles.get(targetTileId).getEntityFX().getBattleMember();
        if(member.getUnit().isAlive()) {
            this.tiles.get(targetTileId).getEntityFX().getHealthBar().updateHealth(member.getUnit().getMaxHealth(), member.getUnit().getCurrentHealth()); 
            this.tiles.get(targetTileId).getEntityFX().changeDirection(member.getDirection());
        }else{
            this.tiles.get(targetTileId).getEntityFX().setBattleMember(null);
        }
    }*/

    public static String getAlliance(int controllerId, int myBattleId) {
        if(controllerId == myBattleId) {
            return "ally";
        }
        if((controllerId != 0) && (controllerId != 1)) return "neutral";
        return "enemy";
    }

    // Setters and Getters
    public HashMap<Integer, FieldFX> getFieldFXs() {
        return this.fieldFXs;
    }
}
