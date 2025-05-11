package com.app.ui.battleui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HealthBarFX extends Group{
    public static final int HEALTHBAR_OUTLINE_WIDTH = 62;
    public static final int HEALTHBAR_OUTLINE_HEIGHT = 7;
    public static final int HEALTHBAR_WIDTH = HEALTHBAR_OUTLINE_WIDTH - 2;
    public static final int HEALTHBAR_HEIGHT = 3;
    private Rectangle healthBarOutline, healthBar;  

    public HealthBarFX() {
        this.healthBarOutline = new Rectangle(0, 0, HEALTHBAR_OUTLINE_WIDTH, HEALTHBAR_OUTLINE_HEIGHT);
        healthBarOutline.setFill(Color.BLACK);
        getChildren().add(healthBarOutline);

        this.healthBar = new Rectangle((HEALTHBAR_OUTLINE_WIDTH - HEALTHBAR_WIDTH) / 2, (HEALTHBAR_OUTLINE_HEIGHT - HEALTHBAR_HEIGHT) / 2, HEALTHBAR_OUTLINE_WIDTH, HEALTHBAR_HEIGHT);
        healthBarOutline.setFill(Color.BLACK);
        getChildren().add(healthBar); 
    }

    public void update(int maxHealth, int currentHealth, String alliance) {
        updateHealth(maxHealth, currentHealth);
        updateColor(alliance);
    }

    public void updateHealth(int maxHealth, int currentHealth) {
        double percentage = (currentHealth*100) / maxHealth;
        this.healthBar.setWidth((percentage/100) * HEALTHBAR_WIDTH);
    }
    public void updateColor(String alliance) {
        switch (alliance) {
            case "ally":
                this.healthBar.setFill(Color.rgb(30, 247, 146));
                break;
            case "enemy":
                this.healthBar.setFill(Color.rgb(255, 91, 59));
                break;
            default:
                this.healthBar.setFill(Color.YELLOW);
                break;
        }
    }
}
