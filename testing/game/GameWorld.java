package game;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.util.*;

public class GameWorld extends JPanel {
    private static final int GRID_SIZE = 32; // size of map e.g. 32 -> 32x32
    private static final int CELL_SIZE = 20; // size of individual cells when displayed -> each cell is of size
                                             // cell_size x cell_size
    private static final double DAY_LENGTH = 24.0; // length of a full cycle

    // lighting system constants
    private static final double CELESTIAL_RADIUS = GRID_SIZE * CELL_SIZE / 2.0;
    private static final double MIN_SHADOW_INTENSITY = 0.2;
    private static final double MAX_SHADOW_LENGTH = GRID_SIZE / 2.0;

    // misc
    private Player player;
    private List<GameObject> objects;
    private double gameTime;
    private Timer gameTimer;
    private Set<Integer> pressedKeys;
    private long lastUpdateTime;

    // lighting system components
    private CelestialBody sun;
    private CelestialBody moon;
    private Map<GameObject, Shadow> shadowMap;
    private double ambientLight;

    @SuppressWarnings("unused")
    public GameWorld() {
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        setBackground(Color.WHITE);

        player = new Player(new Vector2D(GRID_SIZE / 2.0, GRID_SIZE / 2.0));
        objects = new ArrayList<>();
        shadowMap = new HashMap<>();
        pressedKeys = new HashSet<>();

        sun = new CelestialBody(0, true, CELESTIAL_RADIUS, CELESTIAL_RADIUS, CELESTIAL_RADIUS);
        moon = new CelestialBody(Math.PI, false, CELESTIAL_RADIUS, CELESTIAL_RADIUS, CELESTIAL_RADIUS);

        // add sample objects
        initializeObjects();

        gameTime = 0.0;
        lastUpdateTime = System.nanoTime();

        // set up game clock and apply desired fps (60 FPS)
        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();

        // input handling
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });
    }

    private void initializeObjects() {
        Random rand = new Random();
        for (int i = 0; i < 15; i++) {
            double x = rand.nextDouble() * (GRID_SIZE - 4) + 2;
            double y = rand.nextDouble() * (GRID_SIZE - 4) + 2;
            double height = 1.0 + rand.nextDouble() * 2.0;
            objects.add(new GameObject(new Vector2D(x, y), height,
                    new Color(rand.nextFloat() * 0.5f + 0.5f,
                            rand.nextFloat() * 0.5f + 0.5f,
                            rand.nextFloat() * 0.5f + 0.5f)));
        }
    }

    private void handleInputs() {
        // handle input
        Vector2D inputForce = new Vector2D(0, 0);

        if (pressedKeys.contains(KeyEvent.VK_W))
            inputForce = inputForce.add(new Vector2D(0, -1));
        if (pressedKeys.contains(KeyEvent.VK_S))
            inputForce = inputForce.add(new Vector2D(0, 1));
        if (pressedKeys.contains(KeyEvent.VK_A))
            inputForce = inputForce.add(new Vector2D(-1, 0));
        if (pressedKeys.contains(KeyEvent.VK_D))
            inputForce = inputForce.add(new Vector2D(1, 0));

        if (inputForce.length() > 0) {
            inputForce = inputForce.normalize().multiply(player.getAcceleration());
            player.applyForce(inputForce);
        }

    }

    private void updateTime(double deltaTime) {
        gameTime += deltaTime;
        if (gameTime >= DAY_LENGTH) {
            gameTime -= DAY_LENGTH; // normalizes to range [0, day_length]
        }

        // update celestial bodies
        sun.update(deltaTime);
        moon.update(deltaTime);
    }

    private void updateGame() {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastUpdateTime) / 1e9;
        lastUpdateTime = currentTime;

        // update game time
        updateTime(deltaTime);

        // handle input
        handleInputs();

        // update player
        player.update(deltaTime);

        // collision handler
        for (GameObject obj : objects) {
            player.handleCollision(obj);
        }

        repaint();
    }

    private void updateLighting() {
        // calculate ambient light based on celestial body positions
        CelestialBody activeCelestialBody = sun.isVisible() ? sun : moon;

        // calc ambient light
        double celestialHeight = activeCelestialBody.getPosition().getY() - (GRID_SIZE * CELL_SIZE / 2.0);
        double maxHeight = CELESTIAL_RADIUS;
        double ambientFactor = 0.8 * (celestialHeight / maxHeight);
        ambientLight = Math.min(Math.max(0.2 + ambientFactor, 0), 1.0);

        // update shadows
        updateShadows(activeCelestialBody);
    }

    private void updateShadows(CelestialBody celestialBody) {
        shadowMap.clear();

        for (GameObject obj : objects) {
            Shadow shadow = calculateShadow(obj, celestialBody);
            if (shadow != null) {
                shadowMap.put(obj, shadow);
            }
        }
    }

    private Shadow calculateShadow(GameObject obj, CelestialBody celestialBody) {
        if (!celestialBody.isVisible()) {
            return null;
        }

        Vector2D objPos = obj.getPosition().multiply(CELL_SIZE);
        Vector2D celestialPos = celestialBody.getPosition();
        Vector2D toLight = celestialPos.subtract(objPos);

        // calculate shadow length based on object height and celestial body position
        double celestialHeight = celestialPos.getY() - (GRID_SIZE * CELL_SIZE / 2.0);
        double shadowLength = (obj.getHeight() * CELL_SIZE * toLight.length()) / (celestialHeight * 2.0);
        shadowLength = Math.min(shadowLength, MAX_SHADOW_LENGTH);

        // calculate shadow direction
        Vector2D shadowDir = toLight.normalize();

        // calculate shadow endpoints
        Vector2D shadowStart = objPos;
        Vector2D shadowEnd = shadowStart.add(shadowDir.multiply(shadowLength));

        // calculate shadow intensity based on celestial object height
        double intensity = 1.0 - (celestialHeight / CELESTIAL_RADIUS);
        intensity = Math.max(MIN_SHADOW_INTENSITY, intensity);

        return new Shadow(shadowStart, shadowEnd, intensity);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateLighting();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw background with ambient lighting
        drawBackground(g2d);

        // draw shadows
        drawShadows(g2d);

        // draw objects
        drawObjects(g2d);

        // draw celestial bodies
        drawCelestialBodies(g2d);

        // draw player
        drawPlayer(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        // apply ambient lighting to background
        Color bgColor = new Color(
                (float) (0.9 * ambientLight),
                (float) (0.9 * ambientLight),
                (float) (1.0 * ambientLight));
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // create grid
        g2d.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= GRID_SIZE; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE);
            g2d.drawLine(0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawShadows(Graphics2D g2d) {
        for (Shadow shadow : shadowMap.values()) {
            g2d.setColor(new Color(0, 0, 0, (float) (0.5 * shadow.getIntensity())));

            Vector2D start = shadow.getStart();
            Vector2D end = shadow.getEnd();

            // draw shadow as a gradient
            GradientPaint gradient = new GradientPaint(
                    (float) start.getX(), (float) start.getY(),
                    new Color(0, 0, 0, (float) (0.5 * shadow.getIntensity())),
                    (float) end.getX(), (float) end.getY(),
                    new Color(0, 0, 0, 0));

            g2d.setPaint(gradient);

            // create shadow shape
            double width = 20; // width of shadow
            Vector2D perpendicular = shadow.getDirection().perpendicular().multiply(width / 2);

            Path2D shadowPath = new Path2D.Double();
            shadowPath.moveTo(start.getX() - perpendicular.getX(), start.getY() - perpendicular.getY());
            shadowPath.lineTo(start.getX() + perpendicular.getX(), start.getY() + perpendicular.getY());
            shadowPath.lineTo(end.getX() + perpendicular.getX(), end.getY() + perpendicular.getY());
            shadowPath.lineTo(end.getX() - perpendicular.getX(), end.getY() - perpendicular.getY());
            shadowPath.closePath();

            g2d.fill(shadowPath);
        }
    }

    private void drawObjects(Graphics2D g2d) {
        for (GameObject obj : objects) {
            Vector2D pos = obj.getPosition();
            int screenX = (int) (pos.getX() * CELL_SIZE);
            int screenY = (int) (pos.getY() * CELL_SIZE);

            Color objColor = obj.getColor();
            float[] hsb = Color.RGBtoHSB(
                    objColor.getRed(),
                    objColor.getGreen(),
                    objColor.getBlue(),
                    null);

            Color litColor = Color.getHSBColor(
                    hsb[0],
                    hsb[1],
                    (float) (hsb[2]));

            g2d.setColor(litColor);
            g2d.fill(new Rectangle2D.Double(screenX - 10, screenY - 10, 20, 20));
        }
    }

    private void drawCelestialBodies(Graphics2D g2d) {
        // draw sun
        if (sun.isVisible()) {
            g2d.setColor(new Color(1.0f, 0.9f, 0.2f, 0.8f));
            Vector2D sunPos = sun.getPosition();
            g2d.fill(new Ellipse2D.Double(
                    sunPos.getX() - sun.getSize() / 2,
                    sunPos.getY() - sun.getSize() / 2,
                    sun.getSize(),
                    sun.getSize()));
        }

        // draw moon
        if (moon.isVisible()) {
            g2d.setColor(new Color(0.9f, 0.9f, 1.0f, 0.6f));
            Vector2D moonPos = moon.getPosition();
            g2d.fill(new Ellipse2D.Double(
                    moonPos.getX() - moon.getSize() / 2,
                    moonPos.getY() - moon.getSize() / 2,
                    moon.getSize(),
                    moon.getSize()));
        }
    }

    private void drawPlayer(Graphics2D g2d) {
        Vector2D playerPos = player.getPosition();
        g2d.setColor(new Color(0, 0, 255, (int) (255)));
        g2d.fill(new Ellipse2D.Double(
                playerPos.getX() * CELL_SIZE - 10,
                playerPos.getY() * CELL_SIZE - 10,
                20,
                20));
    }
}