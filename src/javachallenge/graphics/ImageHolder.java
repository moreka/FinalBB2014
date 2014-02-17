package javachallenge.graphics;

import javax.swing.*;
import java.awt.*;

/**
 * Created by alireza on 2/12/14.
 */
public class ImageHolder {

    public static Image[] walls = loadImage("data/wall_", 3);
    public static Image[] semiWalls = loadImage("data/semi_wall_", 3);
    public static Image grass = new ImageIcon("data/grass.png").getImage();
    public static Image mountain = new ImageIcon("data/mountain.png").getImage();
    public static Image water = new ImageIcon("data/water.png").getImage();
    public static Image mine = new ImageIcon("data/mine.png").getImage();
    public static Image attacker = new ImageIcon("data/attacker.png").getImage();
    public static Image wallie = new ImageIcon("data/black.png").getImage();
    public static Image black = new ImageIcon("data/black.png").getImage();
    public static Image spawn = new ImageIcon("data/spawn.png").getImage();
    public static Image destination = new ImageIcon("data/destination.png").getImage();
    public static Image zombie = new ImageIcon("data/zombie.png").getImage();
    public static Image bomber = new ImageIcon("data/bomber.png").getImage();
    public static Image white = new ImageIcon("data/white.jpg").getImage();
    public static Image brickWall = new ImageIcon("data/brickwall.png").getImage();

    public static Image[] loadImage(String prefix, int size){

        Image[] all = new Image[size];
        for (int i = 0; i < size; i++){
            all[i] = new ImageIcon(prefix + String.valueOf(i + 1) + ".png").getImage();
        }
        return all;
    }
}
