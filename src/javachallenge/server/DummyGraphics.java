package javachallenge.server;

import javachallenge.util.Map;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by mohammad on 2/11/14.
 */
public class DummyGraphics extends JFrame {

    private DummyPanel dummyPanel;

    public DummyGraphics(Game game) throws HeadlessException, IOException {
        super("Java Challenge Tester");
        this.setSize(new Dimension(850, 650));
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dummyPanel = new DummyPanel(game.getMap());
        this.add(dummyPanel);
    }

    public void startAnimation() {
        dummyPanel.startTurnAnimation();
    }
}
