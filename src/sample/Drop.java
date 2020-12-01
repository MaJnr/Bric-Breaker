package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.awt.*;

import static java.lang.Math.random;

public class Drop {

    // peut-etre pas necessaire comme classe mais comme enum...

    /*
    todo: gestion des bonus/malus:
     - proprietes (position, effets, taille...)
     - generation et affichage
     - physique (vers le bas, collisions)
     - ...
     */

    private boolean isBonus;
    private final double BONUS_CHANCE = 0.5;
    private String effect;
    private String[] bonusEffectArray = {"increaseBarSize", "decreaseBallSpeed", "increaseBarSpeed"};
    private String[] malusEffectArray = {"decreaseBarSize", "increaseBallSpeed", "decreaseBarSpeed"};

    private Circle circle;

    private Point middle;
    public Drop(Point middle) {
        setRandomEffect(BONUS_CHANCE);
        this.middle = middle;
        drawDrop();
    }

    public void drawDrop() {
        circle = new Circle();
        circle.setCenterX(middle.x);
        circle.setCenterY(middle.y);
        circle.setRadius(5);
        if (isBonus) {
            circle.setFill(Color.GREEN);
        } else {
            circle.setFill(Color.RED);
        }
    }

    private void setRandomEffect(double bonusChance) {
        if (random() >= bonusChance) {
            //drop is a bonus
            isBonus = true;
            int randomBonus = (int) (random() * bonusEffectArray.length);
            effect = bonusEffectArray[randomBonus];
        } else {
            //drop is a malus
            isBonus = false;
            int randomBonus = (int) (random() * malusEffectArray.length);
            effect = malusEffectArray[randomBonus];
        }
    }



    public boolean isBonus() {
        return isBonus;
    }

    public void setBonus(boolean bonus) {
        isBonus = bonus;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }
}
