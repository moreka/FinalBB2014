package javachallenge.server;

import javachallenge.util.Map;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by mohammad on 2/11/14.
 */
public class DummyGraphics extends JFrame {
    public DummyGraphics(Map map) throws HeadlessException, IOException {
        super("Java Challenge Tester");
        this.setSize(new Dimension(850, 650));
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DummyPanel dp = new DummyPanel(map);
        this.add(dp);
    }

    public static void main(String[] args) {
        try {
            new DummyGraphics(Map.loadMap("test.map")).setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
