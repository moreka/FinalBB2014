package javachallenge.graphics;

import javachallenge.server.Game;
import javachallenge.util.Map;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class GraphicMain {
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Map map = null;
                try {
                    map = Map.loadMap("test.map");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Game game = new Game(map);
                FJframe f = new FJframe(game, map.getSizeY(), map.getSizeX());
                FJpanel p = f.getPanel();
                //f.pack();
            }
        });
	}
}



// TODO:
// bia ye tabe besaz ke mapo begire va tuye paint component e FJlabel seda bezan uno.
// mokhtasate Fjgon haro az 6zelE ha begir. dige davarano bikhial.
// chera dayere dorst nemikeshe?