package javachallenge.graphics;


import javachallenge.message.Delta;
import javachallenge.server.Game;
import javachallenge.units.Unit;
import javachallenge.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class FJpanel extends JPanel implements Runnable{
    BufferedImage slate;
    TexturePaint slatetp;
    private Random random;
    private Hexagon[][] map;
    private FJNode[][] nodes;
    private int rows;
    private int cols;
    private int counter;

    private Thread animator;
    private int cycleTime;
    
    private Game game;
    private BufferedImage buffer;

    public FJpanel(Game game, Hexagon[][] map, FJNode[][] nodes, int rows, int cols){
        this.map = map;
        this.nodes = nodes;
        this.rows = rows;
        this.cols = cols;
        this.game = game;
        random = new Random();
        setDoubleBuffered(true);
        this.cycleTime = 500;
    }


    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 200, 200);
        paintComponents(g);
        Graphics2D g2d = (Graphics2D) g;

        //g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        /**
         * this is temporary!!!
         */

        /*if (buffer == null){
            buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        }*/
        //Graphics2D buffer_g2d = (Graphics2D) buffer.getGraphics();
        /**
         * End of temp
         */

        // ino havaset bashe bayad ba tavajjoh be type game bache ha render koni
        /*drawMap(buffer_g2d);
        drawUnits(buffer_g2d);
        drawDelta(buffer_g2d, getDelta(counter));*/
        drawMap(g2d);
        drawUnits(g2d);
        drawDelta(g2d, getDelta(counter));

        //hex.draw(g2d, 50, 50, 20, 0x008844, true);

        //g2d.drawImage(buffer, 0, 0, null);
        //counter++;
        //System.out.println(counter);

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }


    public void paintDelta(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        /**
         * this is temporary!!!
         */

        g2d = (Graphics2D) buffer.getGraphics();

        BufferedImage bImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        drawUnits(bImage.getGraphics());
        drawDelta(bImage.getGraphics(), getDelta(counter));

        g2d.drawImage(buffer, 0, 0, null);
        g2d.drawImage(bImage, 0, 0, null);

        counter++;

    }

    public ArrayList<Delta> getDelta(int round) {
        switch (round % 3) {
            case 0:
                //return game.getAttackDeltaList();
                return game.getWallDeltasList();
            case 1:
                return game.getMoveDeltasList();
            case 2:
                return game.getOtherDeltasList();
            default:
                return null;
        }
    }

/*	public void update(Delta delta){

	}*/

    private Image getImage(CellType type){
        switch (type){
            case TERRAIN:
                return ImageHolder.grass;
            case RIVER:
                return ImageHolder.water;
            case SPAWN:
                return ImageHolder.spawn;
            case MOUNTAIN:
                return ImageHolder.mountain;
            case OUTOFMAP:
                return ImageHolder.black;
            case DESTINATION:
                return ImageHolder.destination;
            case MINE:
                return ImageHolder.mine;
            default:
                return null;
        }
    }

    private void drawMap(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        for (int row = 0; row < rows; row++){
            for (int col = 0; col < cols; col++){
                //Image img = getImage(map[col][row].getType());
            	// man inja bayad ye for ru tamame game ine bezanam ke peyda konam tu har cell chi hast o chi bayad draw she.
                Cell cell = game.getMap().getCellAt(col, row);
                Hexagon hex = map[col][row];

                // cell textures
            	Image img = getImage(cell.getType());
                hex.draw(g2d, img, 0, 0, false);


            }
        }
    }

    private void drawUnits(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        // units
        for (int row = 0; row < rows; row++){
            for (int col = 0; col < cols; col++){
                Cell cell = game.getMap().getCellAt(col, row);
                Hexagon hex = map[col][row];
                // units
                if (cell.getType() != CellType.MINE && cell.getType() != CellType.OUTOFMAP) {
                    if (cell.getUnit() != null)
                        drawImage(g2d, hex, getImageByClassTeam(cell.getUnit().getClass(), cell.getUnit().getTeamId()));
                }
                else if (cell.getType() == CellType.MINE){
                    MineCell mine = (MineCell) cell;
                    if (mine.getUnit() != null)
                        drawImage(g2d, hex, getImageByClassTeam(mine.getUnit().getClass(), cell.getUnit().getTeamId()));
                }
            }
        }

        Edge[] utilEdges = game.getMap().getWalls();


        // walls
        for (int i = 0; i < utilEdges.length; i++){
            Edge edge = utilEdges[i];
            if (edge != null){
                FJgon wall = getFJgonByNodes(nodes[edge.getNodes()[0].getX()][edge.getNodes()[0].getY()], nodes[edge.getNodes()[1].getX()][edge.getNodes()[1].getY()]);
                if (edge.getType() == EdgeType.WALL)
                    drawImage(g2d, wall, ImageHolder.brickWall);//ImageHolder.walls[wall.getShib() + 1]);
                else if (edge.getType() == EdgeType.OPEN){
                    //drawOpenWall(g2d, wall, ImageHolder.black, 4);
                    drawImage(g2d, wall, ImageHolder.grass);
                }
            }
        }

        for (int col = 2; col < nodes.length - 2; col++ ){
            for (int row = 1; row < nodes[0].length; row++){
                //rawImage(g2d, (Polygon)nodes[col][row].draw(), new Color(219, 203, 110));
                drawImage(g2d, nodes[col][row].draw(), ImageHolder.black);
            }
        }
    }

    private Image getImageByClassTeam(Class clazz, int teamId){
//        if (clazz == UnitCE.class){
//            return attacker;
//        }
//        else
        if (clazz == Unit.class){
            // barghia
            if (teamId == 1)
                return ImageHolder.zombie;
            else{ // if (teamId == 0){
                return ImageHolder.attacker;
            }
        }
        /*else if (clazz == ){

        }*/
        else {
            return null;
        }
    }

    private void drawDelta(Graphics g, ArrayList<Delta> deltas){
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < deltas.size(); i++){
            Delta delta = deltas.get(i);
            FJgon temp;
            switch (delta.getType()) {
                case MINE_DISAPPEAR:
                    //mineDisappear(g2d, deltas[i].getCell, mineDissapear);
                    //drawImage(g2d, map[delta.getSource().x][delta.getSource().y], mineDissapear);
                    break;

                case CELL_MOVE:
                    drawImage(g2d, map[delta.getDestination().x][delta.getDestination().y], ImageHolder.attacker);
                    break;

                case AGENT_KILL:
                    // tu phase badi animation e kill
                    //drawImage(g2d, map[delta.getCell().x][delta.getCell().y], attacker);
                    break;
                case AGENT_ATTACK:
                    // tu phase badi animation e kill
                    //drawImage(g2d, map[delta.getCell().x][delta.getCell().y], attacker);
                    break;

                case AGENT_ARRIVE:
                    //
                    break;
//                case SPAWN_ATTACKER:
//                    drawImage(g2d, map[delta.getSource().x][delta.getSource().y], attacker);
//                    break;
                case MINE_CHANGE:
                    // ye addad draw kon
                    drawImage(g2d, map[delta.getSource().x][delta.getSource().y], ImageHolder.mine);
                    break;
                case WALL_DISAPPEAR:
                    //
                    break;
                case WALL_DRAW:
                	temp = getFJgonByNodes(nodes[delta.getSource().x][delta.getSource().y], nodes[delta.getDestination().x][delta.getDestination().y]);
                    drawImage(g2d, temp, ImageHolder.brickWall);//ImageHolder.walls[temp.getShib() + 1]);
                    break;
                case WALL_SEMI_DRAW:
                	temp = getFJgonByNodes(nodes[delta.getSource().x][delta.getSource().y], nodes[delta.getDestination().x][delta.getDestination().y]);
                    drawImage(g2d, temp, ImageHolder.semiWalls[temp.getShib() + 1]);
                    break;
                case WALLIE_MOVE:
                    drawImage(g2d, nodes[delta.getDestinationWallie().x][delta.getDestinationWallie().y], ImageHolder.wallie);



                default:

                    break;
            }
        }
    }

    private int shib (Point one, Point two){
        if (one.x - two.x == 0)
            return 0;
        float shib = (float)(one.y - two.y) / (float)(one.x - two.x);
        //System.out.println(shib);
        // shib inja nrgative halate dastgahe safhas
        if (shib < 0)
            return 1;
        else // (ship > 0)
            return -1;
    }
    
    private FJgon getFJgonByNodes(FJNode first, FJNode second){
        FJNode source = first;
        FJNode dest = second;
        //if (delta.getSource().x + delta.getSource().y < delta.getDestination().x + delta.getDestination().y){
        if (first.getCircleCenter().x  < second.getCircleCenter().x && + first.getCircleCenter().y > second.getCircleCenter().y ){
            FJNode temp = dest;
            dest = source;
            source = temp;
        }
        else if (first.getCircleCenter().x == second.getCircleCenter().x && + first.getCircleCenter().y > second.getCircleCenter().y){
            FJNode temp = dest;
            dest = source;
            source = temp;
        }
        else if(first.getCircleCenter().x > second.getCircleCenter().x && + first.getCircleCenter().y > second.getCircleCenter().y){
            FJNode temp = dest;
            dest = source;
            source = temp;
        }



        int shib = shib(source.getCircleCenter(), dest.getCircleCenter());
        Point one, two, three, four;
        if (shib < 0){
            one = new Point(source.xpoints[1], source.ypoints[1]);
            two = new Point(dest.xpoints[0], dest.ypoints[0]);
            three = new Point(dest.xpoints[2], dest.ypoints[2]);
            four =  new Point(source.xpoints[2], source.ypoints[2]);
        }

        else if (shib == 0){
            one = new Point(source.xpoints[1], source.ypoints[1]);
            two = new Point(dest.xpoints[1], dest.ypoints[1]);
            three = new Point(dest.xpoints[0], dest.ypoints[0]);
            four =  new Point(source.xpoints[2], source.ypoints[2]);
        }

        else{ //(shib > 0)
            one = new Point(source.xpoints[2], source.ypoints[2]);
            two = new Point(dest.xpoints[1], dest.ypoints[1]);
            three = new Point(dest.xpoints[0], dest.ypoints[0]);
            four =  new Point(source.xpoints[0], source.ypoints[0]);
        }
        return new FJgon(new Point[]{one, two, three, four}, FJframe.FJHEIGHT, shib);
    }


    private void drawImage(Graphics g, Shape shape, Image img){
        if (img != null){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        g2d.setClip(shape);
        Rectangle r = shape.getBounds();
        g2d.drawImage(img, r.x, r.y, null);
    }
    }

    private void drawImage(Graphics2D g, Polygon shape, Color color){
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(color);
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

            g.fillPolygon(shape.xpoints, shape.ypoints, shape.npoints);

        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
    }


    public void drawOpenWall(Graphics2D g, FJgon wall, Image img, int lineThickness) {
        // Store before changing.
        Graphics2D g2d = (Graphics2D) g;

        /*Stroke tmpS = g2d.getStroke();
        Color tmpC = g2d.getColor();

        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        g2d.drawPolygon(wall.xpoints, wall.ypoints, wall.npoints);

        // Set values to previous when done.
        g2d.setColor(tmpC);
        g2d.setStroke(tmpS);
        */

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        g2d.setClip(wall);
        Rectangle r = wall.getBounds();
        g2d.drawImage(img, r.x, r.y, null);
    }

    /*@Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }*/

    public void startAnimation(){
        animator = new Thread(this);
        animator.start();
    }

    @Override
    public void run() {

        long beforeTime, timeDiff, sleep;
        boolean end = true;
        beforeTime = System.currentTimeMillis();

        //while (true) {

//
            for (int i = 0; i < 3; i++){
                cycle(i);
            }
/*=======
//            cycle(getDelta(counter));
            counter++;
>>>>>>> 9ffc993f58b922334f6c2597f7f3ea427f4cde22*/
            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = cycleTime - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }

            beforeTime = System.currentTimeMillis();
        //}
    }

    private void cycle(int counter){

    }
}