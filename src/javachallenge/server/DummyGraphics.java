package javachallenge.server;

import javachallenge.util.*;
import javachallenge.util.Point;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class DummyGraphics extends JFrame {

    private DummyPanel dummyPanel;

    JLabel turnNumberLabel = new JLabel("Turn: --");
    JLabel team1NameLabel = new JLabel("Team: --");
    JLabel team1ScoreLabel = new JLabel("Score: --");
    JLabel team1ResourceLabel = new JLabel("Resources: --");
    JLabel team2NameLabel = new JLabel("Team --");
    JLabel team2ScoreLabel = new JLabel("Score: --");
    JLabel team2ResourceLabel = new JLabel("Resources: --");

    public DummyGraphics(Map map) throws HeadlessException, IOException {
        super("Java Challenge Tester");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dummyPanel = new DummyPanel(map);
        dummyPanel.setPreferredSize(new Dimension(dummyPanel.WIDTH, dummyPanel.HEIGHT));

        JScrollPane scrollPane = new JScrollPane(dummyPanel);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(20, 20, 800, 600);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(1000, 700));
        contentPane.add(scrollPane);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        statusPanel.add(turnNumberLabel);
        statusPanel.add(team1NameLabel);
        statusPanel.add(team1ScoreLabel);
        statusPanel.add(team1ResourceLabel);
        statusPanel.add(team2NameLabel);
        statusPanel.add(team2ScoreLabel);
        statusPanel.add(team2ResourceLabel);

        statusPanel.setBounds(840, 20, 160, 600);

        contentPane.add(statusPanel);

        this.setContentPane(contentPane);
        pack();
    }

    public void startAnimation() {
        dummyPanel.startTurnAnimation();
    }

    public void setTurnNumberLabel(int turn) {
        turnNumberLabel.setText("Turn: " + turn);
    }

    public void setTeam1NameLabel(String name) {
        team1NameLabel.setText("Team: " + name);
    }

    public void setTeam2NameLabel(String name) {
        team2NameLabel.setText("Team: " + name);
    }

    public void setTeam1ScoreLabel(int score) {
        team1ScoreLabel.setText("Score: " + score);
    }

    public void setTeam2ScoreLabel(int score) {
        team2ScoreLabel.setText("Score: " + score);
    }

    public void setTeam1ResourceLabel(int resource) {
        team1ResourceLabel.setText("Resources: " + resource);
    }

    public void setTeam2ResourceLabel(int resource) {
        team2ResourceLabel.setText("Resources: " + resource);
    }

    public void updateBackground(ArrayList<Point> updatedPoints) {

    }
}
