package javachallenge.graphics;

import javachallenge.server.Game;
import javachallenge.util.MineCell;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.Toolkit;

public class FJframe extends JFrame{
	private int rows;
	private int cols;
	public static final int RADIUS = 24;
	public static final int PADDING = 2;
    public static final int FJHEIGHT = 4;
	private Hexagon[][] map;
	private FJNode[][] nodes;
	private FJpanel panel;
    private Game game;
    private ArrayList<Hexagon[]> outOfMaps;

	
	
	public FJframe(Game game, int rows, int cols){
		super();

        this.rows = rows;
        this.cols = cols;

		// setting basic properties
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Java Challenge 1392");
		// full screen
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.game = game;
        System.out.println("FJframe width:" + (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        Point origin = new Point(((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() - (int)(Math.sqrt(3) * cols * (RADIUS + PADDING)))/2, 0);

        map = JCmap.makeMap(origin, rows, cols, RADIUS, PADDING);
        nodes = JCmap.makeNodes(rows, 2 * (cols + 1), map);
        /*for (int i = 0; i<outOfMaps.size(); i++){
            outOfMaps.set(i, JCmap.makeOutofMapHexagon(new Point(50,  20), rows, cols, RADIUS, PADDING, i));
        }*/

		initUI();
        setVisible(true);
    }

    JLabel label;

    public String getGameStatus() {
        String format = "<html>Cycle: " + game.getTurn() + "<br>" +
                "CE Team Score: " + game.getCEScore() + "<br>" +
                "CE Team Resource: " + game.getCETeam().getResource() + "<br>";
        for (MineCell cell : game.getMap().getMines()) {
            format += "Mine [" + cell.getX() + ", " + cell.getY() + "] remaining: " + cell.getAmount() + "<br>";
        }
        format += "</html>";
        return format;
    }

    public void updateStat() {
        this.label.setOpaque(true);
        this.label.setForeground(Color.YELLOW);
        this.label.setBackground(Color.BLACK);
        this.label.setText(getGameStatus());
    }

	private void initUI(){
        this.setBackground(Color.BLACK);

        FJpanel panel = new FJpanel(game, map, nodes, rows, cols);
        this.setContentPane(panel);

        panel.setLayout(null);
        panel.setBackground(Color.BLACK);

        label = new JLabel();
        panel.add(label);
        label.setBounds(10, 10, 200, 100);
        label.setForeground(Color.YELLOW);
        label.setVisible(true);

		this.panel = panel;
	}

	public FJpanel getPanel(){
		return panel;
	}
}