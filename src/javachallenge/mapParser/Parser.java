package javachallenge.mapParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javachallenge.message.Action;
import javachallenge.message.Delta;
import javachallenge.util.MapHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    public MapHelper jsonToJava (String filePath) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        Gson gson = new GsonBuilder().create();
        MapHelper mapHelper = gson.fromJson(reader, MapHelper.class);
        reader.close();
        return mapHelper;
    }

    public HashMap<Integer, ArrayList<Action>> logParser (String filePath) throws IOException {
        Gson gson = new GsonBuilder().create();
        HashMap<Integer, ArrayList<Action>> output = new HashMap<Integer, ArrayList<Action>>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        ArrayList<String> lines = new ArrayList<String>();
        String rLine = reader.readLine();
        while (rLine != null) {
            lines.add(rLine);
            rLine = reader.readLine();
        }
        for (String line : lines) {
            String[] temp = line.split("[@]");
            int key = Integer.parseInt(temp[1]);
            if (!output.containsKey(key)) {
                output.put(key, new ArrayList<Action>());
            }
            output.get(key).add(gson.fromJson(temp[2], Action.class));
        }
        return output;
    }

    public void javaToJson (MapHelper mapHelper, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        Gson gson = new Gson();
        writer.write(gson.toJson(mapHelper));
        writer.close();
    }
}
