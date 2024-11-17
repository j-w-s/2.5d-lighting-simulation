package game;

public class CelestialBody {
    private double angle;
    private boolean isSun;
    private Vector2D position;
    private double intensity;
    private double size;

    private double CENTER_X;
    private double CENTER_Y;
    private double ORBITAL_RADIUS;
    private double ROTATION_SPEED = 1.0;

    public CelestialBody(double initialAngle, boolean isSun, double centerX, double centerY, double orbitalRadius) {
        this.angle = initialAngle;
        this.isSun = isSun;
        this.position = new Vector2D(0, 0);
        this.intensity = isSun ? 1.0 : 0.75;
        this.size = isSun ? 50.0 : 40.0;
        this.CENTER_X = centerX;
        this.CENTER_Y = centerY;
        this.ORBITAL_RADIUS = orbitalRadius;
        updatePosition();
    }

    public void update(double deltaTime) {
        angle = (angle + deltaTime * ROTATION_SPEED) % (2 * Math.PI);
        updatePosition();
    }

    private void updatePosition() {
        double x = CENTER_X + ORBITAL_RADIUS * Math.cos(angle);
        double y = CENTER_Y + ORBITAL_RADIUS * Math.sin(angle);
        position = new Vector2D(x, y);
    }

    public boolean isVisible() {
        return position.getY() <= CENTER_Y;
    }

    public Vector2D getPosition() {
        return position;
    }

    public double getIntensity() {
        return intensity;
    }

    public double getSize() {
        return size;
    }

    public boolean isSun() {
        return isSun;
    }
}