package sample;

import java.awt.*;

public class Bar {

    /*
    todo: gestion de la barre:
     - proprietes (position, taille...)
     - hitbox avec la balle et les drops
     - gestion des commandes (droite/gauche et space)
     - ...
     */

    private Point topRight;
    private Point bottomRight;
    private Point bottomLeft;
    private Point topLeft;

    public Bar(Point topRight, Point bottomRight, Point bottomLeft, Point topLeft) {
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
        this.topLeft = topLeft;
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

    public void doTranslation(int distance) {
            topRight.x += distance;
            bottomRight.x += distance;
            bottomLeft.x += distance;
            topLeft.x += distance;
    }
}
