package game;

class Shadow {
    private Vector2D start;
    private Vector2D end;
    private double intensity;
    private Vector2D direction;

    public Shadow(Vector2D start, Vector2D end, double intensity) {
        this.start = start;
        this.end = end;
        this.intensity = intensity;
        this.direction = end.subtract(start).normalize();
    }

    public Vector2D getStart() {
        return start;
    }

    public Vector2D getEnd() {
        return end;
    }

    public double getIntensity() {
        return intensity;
    }

    public Vector2D getDirection() {
        return direction;
    }
}