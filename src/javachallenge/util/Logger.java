package javachallenge.util;

import com.google.gson.Gson;
import javachallenge.message.Action;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Logger {
    private static Logger instance = null;
    private PrintWriter writer;
    private Gson gson = new Gson();

    private Logger() {
        try {
            writer = new PrintWriter(new FileWriter("JavaChallenge.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }

    public void log(Action action, int turn) {
        writer.append(System.currentTimeMillis() + "@" + turn + "@");
        writer.append(gson.toJson(action) + '\n');
        writer.flush();
    }

    public void logs(ArrayList<Action> actions, int turn) {
        for (Action action : actions)
            log(action, turn);
    }

    public void close() {
        writer.close();
    }
}
