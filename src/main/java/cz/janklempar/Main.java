package cz.janklempar;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: cleaning_robot <source.json> <result.json>");
            return;
        }
        RobotCleaner mqCleaner;
        // Initiate input from args
        try {
            mqCleaner = new RobotCleaner(args[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // if input is valid and loaded, perform calculation and print result to json file
        if (mqCleaner.getMyQCleanerInput() != null && mqCleaner.getMyQCleanerInput().isValidInput()) {

            mqCleaner.calculateAndFillResult();
            mqCleaner.getMyQCleanerResult().printResultToJsonFile(args[1]);

        }


    }
}