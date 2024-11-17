package game;

public class Vector2D {
    double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // basic operations
    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    // magnitude and normalization
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public Vector2D normalize() {
        double len = length();
        if (len == 0)
            return new Vector2D(0, 0);
        return new Vector2D(x / len, y / len);
    }

    // dot product/angle between vectors
    public double dotProduct(Vector2D other) {
        return x * other.x + y * other.y;
    }

    public double angleBetween(Vector2D other) {
        double dotProduct = dotProduct(other);
        double magnitudeProduct = length() * other.length();
        return Math.acos(dotProduct / magnitudeProduct);
    }

    public Vector2D projectionOnto(Vector2D onto) {
        double dotProduct = dotProduct(onto);
        double ontoLengthSquared = onto.lengthSquared();
        return new Vector2D(dotProduct / ontoLengthSquared * onto.x,
                dotProduct / ontoLengthSquared * onto.y);
    }

    // perpendicular vectors
    public Vector2D perpendicular() {
        return new Vector2D(-y, x);
    }

    public double distanceTo(Vector2D other) {
        return subtract(other).length();
    }

    // clamping and rounding
    public Vector2D clamp(double minX, double maxX, double minY, double maxY) {
        return new Vector2D(
                Math.max(Math.min(x, maxX), minX),
                Math.max(Math.min(y, maxY), minY));
    }

    public Vector2D floor() {
        return new Vector2D(Math.floor(x), Math.floor(y));
    }

    public Vector2D ceil() {
        return new Vector2D(Math.ceil(x), Math.ceil(y));
    }

    public Vector2D round() {
        return new Vector2D(Math.round(x), Math.round(y));
    }

    // reflection
    public Vector2D reflectAcross(Vector2D normal) {
        Vector2D projection = projectionOnto(normal.perpendicular());
        return new Vector2D(x - 2 * projection.x,
                y - 2 * projection.y);
    }

    // linear interpolation
    public Vector2D lerp(Vector2D other, double t) {
        return new Vector2D(
                x + (other.x - x) * t,
                y + (other.y - y) * t);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
