package game;

public class Vector3D {
    double x, y, z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // basic operations
    public Vector2D toVector2D() {
        return new Vector2D(x, y);
    }

    public Vector3D add(Vector3D other) {
        return new Vector3D(x + other.x, y + other.y, z + other.z);
    }

    public Vector3D subtract(Vector3D other) {
        return new Vector3D(x - other.x, y - other.y, z - other.z);
    }

    public Vector3D multiply(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }

    // magnitude and normalization
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3D normalize() {
        double len = length();
        if (len == 0)
            return new Vector3D(0, 0, 0);
        return new Vector3D(x / len, y / len, z / len);
    }

    // dot product/angle between vectors
    public double dotProduct(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public double angleBetween(Vector3D other) {
        double dotProduct = dotProduct(other);
        double magnitudeProduct = length() * other.length();
        return Math.acos(dotProduct / magnitudeProduct);
    }

    // cross product
    public Vector3D crossProduct(Vector3D other) {
        return new Vector3D(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x);
    }

    // projection and reflection
    public Vector3D projectionOnto(Vector3D onto) {
        double dotProduct = dotProduct(onto);
        double ontoLengthSquared = onto.lengthSquared();
        return new Vector3D(x * dotProduct / ontoLengthSquared,
                y * dotProduct / ontoLengthSquared,
                z * dotProduct / ontoLengthSquared);
    }

    public Vector3D reflectAcross(Vector3D normal) {
        Vector3D projection = projectionOnto(normal);
        return new Vector3D(x - 2 * projection.x,
                y - 2 * projection.y,
                z - 2 * projection.z);
    }

    public Vector3D rotateAround(Vector3D axis, double angleInRadians) {
        double sinHalfAngle = Math.sin(angleInRadians / 2);
        double cosHalfAngle = Math.cos(angleInRadians / 2);

        double rx = axis.x * sinHalfAngle;
        double ry = axis.y * sinHalfAngle;
        double rz = axis.z * sinHalfAngle;

        double dotProduct = dotProduct(axis);
        double lengthSquared = lengthSquared();

        double xNew = (cosHalfAngle * cosHalfAngle - sinHalfAngle * sinHalfAngle) * x +
                (sinHalfAngle * sinHalfAngle + cosHalfAngle * cosHalfAngle) * dotProduct / lengthSquared +
                2 * (rx * y - ry * z) * sinHalfAngle * cosHalfAngle;

        double yNew = (sinHalfAngle * sinHalfAngle + cosHalfAngle * cosHalfAngle) * dotProduct / lengthSquared +
                (cosHalfAngle * cosHalfAngle - sinHalfAngle * sinHalfAngle) * y +
                2 * (ry * x - rx * z) * sinHalfAngle * cosHalfAngle;

        double zNew = (sinHalfAngle * sinHalfAngle + cosHalfAngle * cosHalfAngle) * dotProduct / lengthSquared +
                (cosHalfAngle * cosHalfAngle - sinHalfAngle * sinHalfAngle) * z +
                2 * (rz * x - rx * y) * sinHalfAngle * cosHalfAngle;

        return new Vector3D(xNew, yNew, zNew);
    }

    // linear interpolation
    public Vector3D lerp(Vector3D other, double t) {
        return new Vector3D(
                x + (other.x - x) * t,
                y + (other.y - y) * t,
                z + (other.z - z) * t);
    }

    // clamping and rounding
    public Vector3D clamp(double min, double max) {
        double length = length();
        if (length > max) {
            return normalize().multiply(max);
        } else if (length < min) {
            return normalize().multiply(min);
        }
        return this;
    }

    public Vector3D floor() {
        return new Vector3D(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    public Vector3D ceil() {
        return new Vector3D(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    public Vector3D round() {
        return new Vector3D(Math.round(x), Math.round(y), Math.round(z));
    }

    public double distanceTo(Vector3D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}