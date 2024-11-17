package game;

public class Player {
    private Vector2D position;
    private Vector2D velocity;
    private Vector2D acceleration;

    private double height;
    private double radius;

    private static final double MAX_SPEED = 5.0;
    private static final double ACCELERATION = 20.0;
    private static final double FRICTION = 0.10;

    public Player(Vector2D position) {
        this.position = position;
        this.velocity = new Vector2D(0, 0);
        this.acceleration = new Vector2D(0, 0);
        this.height = 1.8;
        this.radius = 0.4;
    }

    public double getAcceleration() {
        return ACCELERATION;
    }

    public void applyForce(Vector2D force) {
        acceleration = acceleration.add(force);
    }

    public void update(double deltaTime) {
        // apply friction to velocity directly -> half-life decay
        velocity = velocity.multiply(Math.pow(FRICTION, deltaTime));

        // update velocity with acceleration
        velocity = velocity.add(acceleration.multiply(deltaTime));

        // limit speed within range of max speed
        if (velocity.length() > MAX_SPEED) {
            velocity = velocity.normalize().multiply(MAX_SPEED);
        }

        // update position
        position = position.add(velocity.multiply(deltaTime));

        // reset acceleration
        acceleration = new Vector2D(0, 0);

        // stop completely if moving very slowly
        if (velocity.length() < 0.01) {
            velocity = new Vector2D(0, 0);
        }
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public double getHeight() {
        return height;
    }

    public double getRadius() {
        return radius;
    }

    public void handleCollision(GameObject obj) {
        Vector2D diff = position.subtract(obj.getPosition());
        double distance = diff.length();
        double minDist = radius + obj.getRadius();

        if (distance < minDist) {
            Vector2D pushVector = diff.normalize().multiply(minDist - distance);
            position = position.add(pushVector);
        }
    }
}