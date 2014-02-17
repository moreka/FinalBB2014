package javachallenge.graphics;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Random;

public class Hexagon extends Polygon {

    private static final long serialVersionUID = 1L;

    public static final int SIDES = 6;

    private Point center = new Point(0, 0);
    private int radius;
    private static int ROTATION = 90;

    public Hexagon(Point center, int radius) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];

        this.center = center;
        this.radius = radius;

        updatePoints();
    }

    public Hexagon(int x, int y, int radius) {
        this(new Point(x, y), radius);
    }

    public void setCenter(Point center) {
        this.center = center;

        updatePoints();
    }

    public void setCenter(int x, int y) {
        setCenter(new Point(x, y));
    }

    private double findAngle(double fraction) {
        return fraction * Math.PI * 2 + Math.toRadians((ROTATION + 180) % 360);
    }

    private Point findPoint(double angle) {
        int x = (int) Math.round(center.x + Math.cos(angle) * radius);
        int y = (int) Math.round(center.y + Math.sin(angle) * radius);

        return new Point(x, y);
    }

    protected void updatePoints() {
        for (int p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point point = findPoint(angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
        }
    }
    public void draw(Graphics2D g, Image img, int lineThickness, int colorValue, boolean filled) {
        // Store before changing.
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        if (filled)
            g.fillPolygon(xpoints, ypoints, npoints);
        else
            g.drawPolygon(xpoints, ypoints, npoints);

        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
        
		g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_COLOR_RENDERING, 
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		
        g.setClip(this);
        Rectangle r = this.getBounds();
        g.drawImage(img, r.x, r.y, null);
    }

	public Point getPoint(int i) {
		return new Point(xpoints[i], ypoints[i]);
	}
	
	public String toString() {
    	// TODO Auto-generated method stub
    	String result = "";
    	for (int i = 0; i < SIDES; i++){
    		result += String.valueOf(i) + "-(" + xpoints[i] + ", " + ypoints[i] + ") \n"; 
    	}
    	return result;
    }

}

class FJgon extends Polygon{
	private int n1;
	private int n2;
	private int shib;
	
	public static final int ISWALL = 2;
	public static final int ISSEMI = 1;
	public static final int NOWALL = 0;
	int status = 0;
	
	public static final int SIDES = 4;

    private Point[] points = new Point[SIDES];
    private int FJheight;

    public FJgon(Point[] points, int FJheight, int shib) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];
        this.shib = shib;
//        this.n1 = node1;
//        this.n2 = node2;
        this.points = points;
        this.FJheight = FJheight;
        
        draw();
    }
    
    public int getShib(){
    	return shib;
    }
    
    protected void makePoints() {
        for (int i = 0; i < SIDES; i++){	
            Point point = points[i];
            xpoints[i] = point.x;
            ypoints[i] = point.y;
            points[i] = point;
        }
    }
    
    public Shape draw() {
    	makePoints();
    	
        GeneralPath p = new GeneralPath();
        for (int ii = 0; ii < SIDES; ii++) {
            if (ii == 0) {
                p.moveTo(xpoints[ii], ypoints[ii]);
            } else {
                p.lineTo(xpoints[ii], ypoints[ii]);
            }
        }
        p.closePath();
        return p;
    }
}


class FJNode extends Polygon{
	public static final int SIDES = 3;

    public FJNode(Point one, Point two, Point three) {
        npoints = SIDES;
        xpoints = new int[]{one.x, two.x, three.x};
        ypoints = new int[]{one.y, two.y, three.y};
        
    }
    
    public Shape draw() {
    	//int radius = (int) Math.round(Math.sqrt(Math.pow(xpoints[0] - xpoints[1], 2) + Math.pow(ypoints[0] -  ypoints[1], 2)));
        int radius = FJframe.FJHEIGHT;
        Shape p = new Ellipse2D.Float(getCircleCenter().x - radius, getCircleCenter().y - radius, 2 * radius, 2 * radius);
        return p;
    }
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	String result = "";
    	for (int i = 0; i < SIDES; i++){
    		result += String.valueOf(i) + "-(" + xpoints[i] + ", " + ypoints[i] + ") \n"; 
    	}
    	return result;
    }

	public Point getCircleCenter() {
		// TODO Auto-generated method stub
		Point center = new Point((xpoints[0] + xpoints[1] + xpoints[2])/3, (ypoints[0] + ypoints[1] + ypoints[2])/3);
		//System.out.println("triangle: \nx: " + xpoints[0] + " y: " + ypoints[0] + "\nx: " + xpoints[1] + " y: " + ypoints[1]+ "\nx: " + xpoints[2] + " y: " + ypoints[2]);
		//System.out.println("circle x:" +  center.x + "y: " + center.y);
		return center;
	}
    
    
}

