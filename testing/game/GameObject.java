// GameObject.java
package game;

import java.awt.Color;

public class GameObject {
    private Vector2D position;
    private double height;
    private double radius;
    private Color color;

    public GameObject(Vector2D position, double height, Color color) {
        this.position = position;
        this.height = height;
        this.radius = 0.5;
        this.color = color;
    }

    public Vector2D getPosition() {
        return position;
    }

    public double getHeight() {
        return height;
    }

    public double getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }
}