package com.app.ui.battleui;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EntityFX extends Group{
    private Image emptyBody = new Image("/empty.png");
    private ImageView body;
    private HealthBarFX healthBarFX;
    
    public EntityFX(String[] entityData, int myBattleId) {
        //System.out.println(entityData[3]);
        this.body = new ImageView(entityData[3] + ".png");
        if(entityData[2].equals("creature") || entityData[2].equals("hero")) {
            if(Integer.parseInt(entityData[5]) == 0) {
                    this.body.setScaleX(1);
            }else{
                    this.body.setScaleX(-1);
            }
        }
        
        getChildren().add(body);

        this.healthBarFX = new HealthBarFX();
        if(entityData[2].equals("creature") || entityData[2].equals("hero")) {
            this.healthBarFX.update(Integer.parseInt(entityData[6]), Integer.parseInt(entityData[7]), BoardFX.getAlliance(Integer.parseInt(entityData[4]), myBattleId));
        }
        this.healthBarFX.setLayoutX(this.body.getBoundsInLocal().getWidth()/2 - (HealthBarFX.HEALTHBAR_OUTLINE_WIDTH/2));
        this.healthBarFX.setLayoutY(this.body.getBoundsInLocal().getHeight() - 10);

        this.setLayoutX(2);
        this.setLayoutY(-40);

        getChildren().add(healthBarFX);
    }

    // Functions
    public void update() {

    }

    public HealthBarFX getHealthBarFX() {
        return this.healthBarFX;
    }
}
