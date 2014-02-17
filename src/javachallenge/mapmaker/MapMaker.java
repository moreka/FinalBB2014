package mapmaker;

import javachallenge.mapParser.Parser;
import javachallenge.util.CellType;
import javachallenge.util.MapHelper;
import javachallenge.util.Point;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class MapMaker {
    public static int size = 20;
    public static Color[] colors;
    public static JButton[][] cell;
    //public static CellType[][] cellType;
    public static int x;
    public static int y;
    public static MapHelper mapHelper;



    public static void main(String[] args) {
        colors = new Color[7];
        colors[0] = Color.green;
        colors[1] = Color.blue;
        colors[2] = Color.lightGray;
        colors[3] = Color.YELLOW;
        colors[4] = Color.BLACK;
        colors[5] = Color.magenta;
        colors[6] = Color.red;
        x = Integer.valueOf(JOptionPane.showInputDialog("X:"));
        y = Integer.valueOf(JOptionPane.showInputDialog("Y:"));
        //Page
        JFrame mapMaker = new JFrame("Map Maker");
        mapMaker.setLayout(null);
        mapMaker.setSize(1300, 700);
        mapMaker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Map Panel
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, x * size + 20 + 12, y * size + 20);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        //Help Panel
        JPanel helpPanel = new JPanel();
        helpPanel.setBounds(1000, 0, 500, 500);
        helpPanel.setLayout(null);
        helpPanel.setBackground(Color.white);
        mapMaker.add(helpPanel);
        //Help Buttons
        JButton[] helps = new JButton[7];
        JLabel[] helpText = new JLabel[7];
        String[] text = {"TERRAIN","RIVER","MOUNTAIN","MINE","OUT OF MAP","SPAWN","DESTINATION"};
        for(int i = 0; i < 7; i++){
            helps[i] = new JButton(String.valueOf(i));
            helps[i].setBackground(colors[i]);
            helps[i].setBounds(10, i * 60, 50, 50);
            helpText[i] = new JLabel(text[i]);
            helpText[i].setBounds(70, i * 60 + 10, 100, 35);
            helpPanel.add(helpText[i]);
            helpPanel.add(helps[i]);
        }
        //Map
        cell = new JButton[x][y];
        //cellType = new CellType[x][y];
        ActionListener a = new cellAction(colors);
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                cell[i][j] = new JButton();
                cell[i][j].addActionListener(a);
                if(j % 2 == 1)
                    cell[i][j].setBounds(i * size + (size / 2) + 2, j * size + 2, size, size);
                else
                    cell[i][j].setBounds(i * size + 2, j * size + 2, size, size);
                if(i == 0 || i == x - 1 || j == 0 || j == y - 1) {
                    cell[i][j].setName("4");
                    cell[i][j].setBackground(colors[4]);
                }
                else{
                    cell[i][j].setName("0");
                    cell[i][j].setBackground(colors[0]);
                }
                cell[i][j].setVisible(true);
                panel.add(cell[i][j]);

            }
        }
        //MenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);
        JMenuItem saveItem = new JMenuItem("save");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                try{
                    PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(JOptionPane.showInputDialog("Map's name:","**.map")),true));
                    printWriter.append(String.valueOf(x) + "\n");
                    printWriter.append(String.valueOf(y) + "\n");

                    Point[] sources = new Point[2];
                    int sCount = 0;
                    Point[] Destination = new Point[2];
                    int dCount = 0;
                    ArrayList<Point> mines = new ArrayList<Point>();
                    for (int i = 0; i < y; i++){
                        for (int j = 0; j < x; j++) {
                            printWriter.append(cell[j][i].getName() + " ");
                            if(cell[j][i].getName().equals("5")){
                                sources[sCount] = new Point();
                                sources[sCount].x = j;
                                sources[sCount].y = i;
                                sCount++;
                            }
                            if(cell[j][i].getName().equals("6")){
                                Destination[dCount] = new Point();
                                Destination[dCount].x = j;
                                Destination[dCount].y = i;
                                dCount++;
                            }
                            if(cell[j][i].getName().equals("3")){
                                Point temp = new Point();
                                temp.x = j;
                                temp.y = i;
                                mines.add(temp);
                            }
                        }
                        printWriter.append("\n");
                    }
                    Point[] minesArray = new Point[mines.size()];
                    for(int i = 0; i < minesArray.length; i++){
                        minesArray[i] = mines.get(i);
                    }


                    String awnser = JOptionPane.showInputDialog("Sources[0]:","yes for " + sources[0].x + "," + sources[0].y + " no for " + sources[1].x + "," + sources[1].y);
                    if(awnser.toLowerCase() == "no"){
                        Point t = sources[1];
                        sources[1] = sources[0];
                        sources[0] = t;
                    }
                    awnser = JOptionPane.showInputDialog("Des[0]:","yes for " + Destination[0].x + "," + Destination[0].y + " no for " + Destination[1].x + "," + Destination[1].y);
                    if(awnser.toLowerCase() == "no"){
                        Point t = Destination[1];
                        Destination[1] = Destination[0];
                        Destination[0] = t;
                    }

                    int mine = Integer.valueOf(JOptionPane.showInputDialog("Amount of the map:","400"));
                    printWriter.append(String.valueOf(mine) + "\n");
                    printWriter.append(String.valueOf(sources[0].getX()) + " " + String.valueOf(sources[0].getY()) + "\n");
                    printWriter.append(String.valueOf(sources[1].getX()) + " " + String.valueOf(sources[1].getY()) + "\n");
                    printWriter.append(Destination[0].getX() + " " + Destination[0].getY() + "\n");
                    printWriter.append(Destination[1].getX() + " " + Destination[1].getY());
                    printWriter.close();
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Exe." + e.getMessage());
                }
                /*mapHelper = new MapHelper(x,y,cellType,sources[0],Destination[0],sources[1], Destination[1],minesArray,mine);
                Parser p = new Parser();
                String name = JOptionPane.showInputDialog("Map's name:");
                try {
                    p.javaToJson(mapHelper, name);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }*/

            }
        });
        menu.add(saveItem);
        mapMaker.setJMenuBar(menuBar);
        mapMaker.add(panel);
        mapMaker.setVisible(true);

    }
}

class cellAction implements ActionListener{
    private Color[] col;
    public cellAction(Color[] in){
        super();
        col = in;
    }
    public void actionPerformed(ActionEvent e){
        int m = Integer.valueOf(((JButton)e.getSource()).getName());
        m = (m + 1) % 7;
        ((JButton)e.getSource()).setBackground(col[m]);
        ((JButton)e.getSource()).setName(String.valueOf(m));
    }
}
