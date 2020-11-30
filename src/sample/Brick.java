package sample;

import java.awt.*;

public class Brick {

    /*
    todo: gestion des briques:
     - proprietes (position, collisions, drops et chances de drop, etat...)
     - generation procedurale
     - ...
     */

    private Point topRight;
    private Point bottomRight;
    private Point bottomLeft;
    private Point topLeft;

    private Drop drop;

    private final double DROP_CHANCE = 0;

    public void setDrop() {
        Drop randomDrop = null;
        if (Math.random() <= DROP_CHANCE) {
            randomDrop = new Drop();
        }
        drop = randomDrop;
    }

    public Brick(Point topRight, Point bottomRight, Point bottomLeft, Point topLeft) {
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
        this.topLeft = topLeft;
        setDrop();
    }

    public Drop getDrop() {
        return drop;
    }

    public void setDrop(Drop drop) {
        this.drop = drop;
    }

    public Point getTopRight() {
        return topRight;
    }

    public void setTopRight(Point topRight) {
        this.topRight = topRight;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(Point bottomRight) {
        this.bottomRight = bottomRight;
    }

    public Point getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(Point bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(Point topLeft) {
        this.topLeft = topLeft;
    }
}
