package cz.janklempar;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class MQCleanerInput {

    private String[][] roomMap;
    private HashMap<String, String> start;
    private String[] commands;
    private int battery;
    private Boolean validInput = true;

    public MQCleanerInput(String[][] roomMap, HashMap<String, String> start, String[] commands, int battery, boolean validInput) {
        this.roomMap = roomMap;
        this.start = start;
        this.commands = commands;
        this.battery = battery;
        this.validInput = validInput;
    }


    public String[][] getRoomMap() {
        return roomMap;
    }

    public void setRoomMap(String[][] roomMap) {
        this.roomMap = roomMap;
    }

    public HashMap<String, String> getStart() {
        return start;
    }

    public void setStart(HashMap<String, String> start) {
        this.start = start;
    }

    public String[] getCommands() {
        return commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public boolean isValidInput() {
        return validInput;
    }

    public void setValidInput(boolean validInput) {
        this.validInput = validInput;
    }

    // Static method for loading MQCleanerInput
    public static MQCleanerInput loadInput(String inputFileName){

        Boolean inputIsValid = true;

        final ObjectMapper objectMapper = new ObjectMapper();
        final File inputFile = new File(inputFileName);
        LinkedHashMap inputJson = null;
        try {
            inputJson = objectMapper.readValue(inputFile, LinkedHashMap.class);
        } catch (IOException e) {
            System.out.println("Error reading file: " + inputFileName);
        }

        // load room map
        ArrayList mapInputFromJson = (ArrayList) inputJson.get("map");
        int roomWidth = mapInputFromJson.size();
        ArrayList firstRow = (ArrayList)mapInputFromJson.get(0);
        int roomHeight = firstRow.size();
        String[][] inputRoomMap = new String[roomWidth][roomHeight];
        for (int i = 0; i < roomWidth; i++ ) {
            ArrayList innerlist = (ArrayList) mapInputFromJson.get(i);
            // validate, that room must be square
            if (mapInputFromJson.size() != roomWidth) {
                inputIsValid = false;
                System.out.println("Error in map: " + inputFileName + ". Map must be in rectangle format. Every row must have same width!");
                break;
            }

            for (int j = 0; j < roomHeight; j++ ) {
                if (innerlist.size() != roomHeight) {
                    System.out.println("Error in map: " + inputFileName + ". Map must be in rectangle format. Every column must have same height!");
                    inputIsValid = false;
                    break;
                }
                inputRoomMap[i][j] = innerlist.get(j).toString();
            }
        }

        // load battery input
        int inputBattery = 0;

        try {
            inputBattery = (int) inputJson.get("battery");
        } catch (Exception e) { // Battary Not found exception
            System.out.println("Could not load initial battery data in : " + inputFileName + ". Check format of json file.");
            inputIsValid = false;

        }

        // load start position
        HashMap<String, String> loadedPositionStart = new HashMap<>();
        LinkedHashMap startInput;
        try {
            startInput = (LinkedHashMap) inputJson.get("start");
            loadedPositionStart.put("X", String.valueOf(startInput.get("X")));
            loadedPositionStart.put("Y", String.valueOf(startInput.get("Y")));
            loadedPositionStart.put("facing", String.valueOf(startInput.get("facing")));

        } catch (Exception e) { // Start Position Not found exception
            System.out.println("Could not load start position in : " + inputFileName + ". Check format of json file.");
            inputIsValid = false;

        }


        ArrayList<String> commandsJson = new ArrayList<>();
        String[] inputCommands;
        // load commands
        try {
            commandsJson = (ArrayList<String>) inputJson.get("commands");
            inputCommands = commandsJson.toArray(new String[commandsJson.size()]);
        } catch (Exception e) { // Start Position Not found exception
            System.out.println("Could not load list of input commands : " + inputFileName + ". Check format of json file.");
            inputIsValid = false;
            inputCommands = new String[]{}; // commands
        }

        // Recovery sequences (could be loaded from configuration)
        // final List<List> recoverySequences // Idea: recovery sequences can be loaded separetly from config file

        // create input object
        MQCleanerInput mappedInput = new MQCleanerInput(inputRoomMap, loadedPositionStart, inputCommands, inputBattery, inputIsValid);

        return mappedInput;

    }



}