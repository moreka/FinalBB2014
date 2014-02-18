package javachallenge.mapParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javachallenge.util.MapHelper;

import java.io.*;

public class Parser {
    public MapHelper jsonToJava (String filePath) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        Gson gson = new GsonBuilder().create();
        MapHelper mapHelper = gson.fromJson(reader, MapHelper.class);
        reader.close();
        return mapHelper;
    }

    public void javaToJson (MapHelper mapHelper, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        Gson gson = new Gson();
        writer.write(gson.toJson(mapHelper));
        writer.close();
    }
}
