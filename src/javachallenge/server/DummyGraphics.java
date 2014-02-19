package javachallenge.server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class DummyGraphics extends JFrame {

    private DummyPanel dummyPanel;

    public DummyGraphics(Game game) throws HeadlessException, IOException {
        super("Java Challenge Tester");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dummyPanel = new DummyPanel(game.getMap());
        dummyPanel.setPreferredSize(new Dimension(dummyPanel.WIDTH, dummyPanel.HEIGHT));

        JScrollPane scrollPane = new JScrollPane(dummyPanel);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(20, 20, 800, 600);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(1000, 700));
        contentPane.add(scrollPane);

        this.setContentPane(contentPane);
        pack();
    }

    public void startAnimation() {
        dummyPanel.startTurnAnimation();
    }
}
