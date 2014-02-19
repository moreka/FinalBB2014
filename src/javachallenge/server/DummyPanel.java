package javachallenge.server;

import javachallenge.exceptions.CellIsNullException;
import javachallenge.units.Unit;
import javachallenge.util.Direction;
import javachallenge.util.EdgeType;
import javachallenge.util.Map;
import javachallenge.util.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DummyPanel extends JPanel {
    private final Map map;

    private Image bufferImage;
    private Image background;
    private Image sand, ocean, zombie, ce;
    private final int WIDTH = 950;
    private final int HEIGHT = 650;
    private final int SIZE = 48;

    private final int NUM_STEPS = 10;
    private final int ANIM_LEN = 100;

    private HashMap<Unit, Point> lastPosition = new HashMap<Unit, Point>();

    public DummyPanel(Map map) {
        this.map = map;
        this.setSize(WIDTH, HEIGHT);
        this.bufferImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.background = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        try {
            this.sand = ImageIO.read(new File("dummy/desert.png")).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
            this.ocean = ImageIO.read(new File("dummy/ocean.png")).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
            this.zombie = ImageIO.read(new File("dummy/zombie.png")).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
            this.ce = ImageIO.read(new File("dummy/ce.png")).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);

            loadBackground();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeLastPosition() {
        for (int i = 0; i < map.getSizeX(); i++)
            for (int j = 0; j < map.getSizeY(); j++)
                try {
                    if (map.getCellAt(i, j).getUnit() != null)
                        if (!lastPosition.containsKey(map.getCellAt(i, j).getUnit()))
                            lastPosition.put(map.getCellAt(i, j).getUnit(), map.getSpawnPoint(map.getCellAt(i, j).getUnit().getTeamId()));
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
    }

    public void updateLastPosition() {
        for (int i = 0; i < map.getSizeX(); i++)
            for (int j = 0; j < map.getSizeY(); j++)
                try {
                    if (map.getCellAt(i, j).getUnit() != null)
                        lastPosition.put(map.getCellAt(i, j).getUnit(),
                                map.getCellAt(i, j).getPoint());
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
    }

    public void startTurnAnimation() {
        initializeLastPosition();

        new Thread() {
            @Override
            public void run() {
                int step = 1;
                while (step <= NUM_STEPS) {
                    drawBackground();
                    drawEdges();
                    drawUnits(step);
                    repaint();
                    step++;
                    try {
                        Thread.sleep(ANIM_LEN / NUM_STEPS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                updateLastPosition();
            }
        }.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bufferImage, 0, 0, null);
    }

    private void loadBackground() {
        Graphics2D g = (Graphics2D) background.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for (int i = 0; i < map.getSizeX(); i++) {
            for (int j = 0; j < map.getSizeY(); j++) {
                int x, y = j * SIZE * 3 / 4;
                if (j % 2 == 0)
                    x = i * SIZE;
                else
                    x = i * SIZE + (SIZE / 2);
                try {
                    switch (map.getCellAt(i, j).getType()) {
                        case TERRAIN:
                            g.drawImage(sand, x, y, null);
                            break;
                        case RIVER:
                            g.drawImage(ocean, x, y, null);
                            break;
                        case OUTOFMAP:
                            g.drawImage(ocean, x, y, null);
                    }
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawBackground() {
        Graphics2D g = (Graphics2D) bufferImage.getGraphics();
        g.drawImage(background, 0, 0, null);
    }

    private void drawEdges() {
        Graphics2D g = (Graphics2D) bufferImage.getGraphics();

        int[] xEdge = { SIZE / 2, SIZE, SIZE, SIZE / 2, 0, 0 };
        int[] yEdge = { 0, SIZE / 4, SIZE * 3 / 4, SIZE, SIZE * 3 / 4, SIZE / 4};

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(6));

        for (int i = 0; i < map.getSizeX(); i++) {
            for (int j = 0; j < map.getSizeY(); j++) {
                int x, y = j * SIZE * 3 / 4;
                if (j % 2 == 0)
                    x = i * SIZE;
                else
                    x = i * SIZE + SIZE / 2;
                for (Direction dir : Direction.values()) {
                    try {
                        if (map.getCellAt(i, j).getEdge(dir) != null &&
                                map.getCellAt(i, j).getEdge(dir).getType() == EdgeType.WALL) {
                            g.drawLine(x + xEdge[dir.ordinal()], y + yEdge[dir.ordinal()],
                                    x + xEdge[(dir.ordinal() + 1) % 6], y + yEdge[(dir.ordinal() + 1) % 6]);
                        }
                    } catch (CellIsNullException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Point getPointInside(Point start, Point end, int step) {
        return new Point(
                ((NUM_STEPS - step) * start.getX() + step * end.getX()) / (NUM_STEPS),
                ((NUM_STEPS - step) * start.getY() + step * end.getY()) / (NUM_STEPS)
        );
    }

    private Point getGraphicalPoint(Point point) {
        int x, y = point.getY() * SIZE * 3 / 4;
        if (point.getY() % 2 == 0)
            x = point.getX() * SIZE;
        else
            x = point.getX() * SIZE + SIZE / 2;
        return new Point(x, y);
    }

    private void drawUnits(int step) {
        Graphics2D g = (Graphics2D) bufferImage.getGraphics();

        for (int i = 0; i < map.getSizeX(); i++) {
            for (int j = 0; j < map.getSizeY(); j++) {
                try {
                    if (map.getCellAt(i, j).getUnit() != null) {
                        Point place;
                        Unit unit = map.getCellAt(i, j).getUnit();

                        if (!unit.getCell().getPoint().equals(lastPosition.get(unit))) {
                            place = getPointInside(getGraphicalPoint(lastPosition.get(unit)), getGraphicalPoint(unit.getCell().getPoint()) , step);
                        }
                        else {
                            place = getGraphicalPoint(unit.getCell().getPoint());
                        }
                        g.drawImage((unit.getTeamId() == 0 ? ce : zombie), place.getX(), place.getY(), null);
                    }
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
