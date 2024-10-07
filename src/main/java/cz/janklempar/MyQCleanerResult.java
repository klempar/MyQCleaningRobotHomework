package cz.janklempar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyQCleanerResult {

    private ArrayList<Point> finalVisited;
    private ArrayList<Point> finalCleaned;
    private RobotPosition finalPosition;
    private int finalBattery;

    public MyQCleanerResult(ArrayList<Point> finalVisited, ArrayList<Point> finalCleaned, RobotPosition finalPosition, int finalBattery) {
        this.finalVisited = finalVisited;
        this.finalCleaned = finalCleaned;
        this.finalPosition = finalPosition;
        this.finalBattery = finalBattery;
    }

    public void printResultToJsonFile(String outputFileName) {
        // more readable format
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


        File jsonFile = new File(outputFileName);

        try {

            objectMapper.writeValue(jsonFile, this.formatForFinalJson(this));
            System.out.println("JSON written to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Object formatForFinalJson(MyQCleanerResult finalData) throws IOException {
        HashMap<String, Object> finalFormatedObject = new HashMap<>();

        // format visited
        HashMap[] visitedJson = finalVisited.stream().map(visited -> {
            HashMap mappedPoint = new HashMap();
            mappedPoint.put("X", (int) visited.getX());
            mappedPoint.put("Y", (int) visited.getY());
            return mappedPoint;
        }).toArray(HashMap[]::new);
        finalFormatedObject.put("visited", visitedJson);

        // format cleaned
        HashMap[] cleanedJson = finalCleaned.stream().map(cleaned -> {
            HashMap mappedPoint = new HashMap();
            mappedPoint.put("X", (int) cleaned.getX());
            mappedPoint.put("Y", (int) cleaned.getY());
            return mappedPoint;
        }).toArray(HashMap[]::new);
        finalFormatedObject.put("cleaned", cleanedJson);

        // format final position of robot
        HashMap finalPositionJson = new HashMap();
        finalPositionJson.put("X", (int) finalPosition.getX());
        finalPositionJson.put("Y", (int) finalPosition.getY());
        finalPositionJson.put("facing", finalPosition.getFacing().name());
        finalFormatedObject.put("final", finalPositionJson);

        // format remaining battery
        finalFormatedObject.put("battery", finalBattery);

        return finalFormatedObject;
    }




    public ArrayList<Point> getFinalVisited() {
        return finalVisited;
    }

    public void setFinalVisited(ArrayList<Point> finalVisited) {
        this.finalVisited = finalVisited;
    }

    public ArrayList<Point> getFinalCleaned() {
        return finalCleaned;
    }

    public void setFinalCleaned(ArrayList<Point> finalCleaned) {
        this.finalCleaned = finalCleaned;
    }

    public RobotPosition getFinalPosition() {
        return finalPosition;
    }

    public void setFinalPosition(RobotPosition finalPosition) {
        this.finalPosition = finalPosition;
    }

    public int getFinalBattery() {
        return finalBattery;
    }

    public void setFinalBattery(int finalBattery) {
        this.finalBattery = finalBattery;
    }

}