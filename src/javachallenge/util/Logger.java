package javachallenge.util;

import com.google.gson.Gson;
import javachallenge.message.Delta;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mohammad on 2/6/14.
 */
public class Logger {
    private static Logger instance = null;
    private PrintWriter writer;
    private Gson gson = new Gson();

    private Logger() {
        try {
            writer = new PrintWriter(new FileWriter("LOG"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }

    public void log(Delta delta, int turn) {
        writer.append("{" + String.valueOf(System.currentTimeMillis()) + "} turn " + turn + ": ");
        writer.append(gson.toJson(delta) + '\n');
        writer.flush();
    }

    public void logs(ArrayList<Delta> deltas, int turn) {
        for (Delta delta : deltas)
            log(delta, turn);
    }

    public void close() {
        writer.close();
    }
}
