package cz.janklempar;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static cz.janklempar.WorldDirection.FACE_MAP;
import static cz.janklempar.WorldDirection.Facing;

public class RobotCleaner {

    private MQCleanerInput myQCleanerInput;
    private MyQCleanerResult myQCleanerResult;

    enum MQElement { S, C, OTHER }
    enum CmdType { TL, TR, B, A, C }

    // Recovery Sequences // could be loaded separately
    static final List<CmdType> recoverySequence_1 = Arrays.asList(CmdType.TR, CmdType.A, CmdType.TL);
    static final List<CmdType> recoverySequence_2 = Arrays.asList(CmdType.TR, CmdType.A, CmdType.TR);
    static final List<CmdType> recoverySequence_3 = Arrays.asList(CmdType.TR, CmdType.A, CmdType.TR);
    static final List<CmdType> recoverySequence_4 = Arrays.asList(CmdType.TR, CmdType.B, CmdType.TR, CmdType.A);
    static final List<CmdType> recoverySequence_5 = Arrays.asList(CmdType.TL, CmdType.TL, CmdType.A);

    // Robot values;
    RobotPosition currentPosition;
    int currentBattery = 0;
    boolean lowBattery = false;


    final ArrayList<Point> cleaned = new ArrayList<>();
    final ArrayList<Point> visited = new ArrayList<>();
    boolean robotStucked = false;

    public RobotCleaner(String inputFileName) throws IOException {

        // Load Input for RobotCleaner.MQCleanerInput by static method;
        this.myQCleanerInput = MQCleanerInput.loadInput(inputFileName);

        try {
            this.currentPosition = new RobotPosition(Integer.parseInt(this.myQCleanerInput.getStart().get("X")),
                    Integer.parseInt(this.myQCleanerInput.getStart().get("Y")),
                    WorldDirection.Facing.valueOf(this.myQCleanerInput.getStart().get("facing")));

        } catch (Exception e) {
            System.err.println("Could not load start position from input json file: " + inputFileName);
        }
    }

    // based on correct input, provide result data and fill robot's MyQCleanerResult.
    public void calculateAndFillResult() {

        if (this.myQCleanerResult == null) {
            this.currentBattery = this.myQCleanerInput.getBattery();;

            // irrelevant check for more readable algorithm. Start position is automatically considered visited.
            if (!visited.contains(currentPosition.getLocation())) {
                visited.add(0,currentPosition.getLocation()); // due to formating purposes, put at index 0
            }
            // --------  MAIN EXECUTION OF INPUT COMMANDS  --------
            final List<String> commands = Arrays.asList(this.myQCleanerInput.getCommands());
            commands.forEach(command -> {

                // if input cmd is invalid, robot is stuck
                boolean cmdValid = Arrays.stream(CmdType.values()).map(Enum::name).toList().contains(command);
                if (!cmdValid) {
                    this.robotStucked = true;
                }

                // regular non-recoveryCMD
                if (!this.robotStucked && !this.lowBattery) {
                    this.performCommand(CmdType.valueOf(command),0);
                }
            });
        }

        // Fill result with calculated final values
        this.myQCleanerResult = new MyQCleanerResult(this.visited, this.cleaned, this.currentPosition, this.currentBattery);


        this.logResult();


    }

    // performs one of commands: C, TR, TL, A, B and returns true, if performed correctly
    private boolean performCommand(CmdType cmd, int recoveryCmd) {

        boolean performedCorrectly = true;

        if (recoveryCmd != 0) {
            System.out.println("    ".repeat(Math.max(0, recoveryCmd)) + "executing recovery cmd: <" + cmd + "> from sequence: " + recoveryCmd + ", facing: " + this.currentPosition.getFacing());
        } else {
            System.out.println("executing following cmd: <" + cmd + "> current battery: " + this.currentBattery + ", facing: " + this.currentPosition.getFacing());
        }

        switch (cmd) {
            case C -> {
                if (this.currentBattery >= 5) {
                    this.currentBattery -= 5;
                    if (!cleaned.contains(currentPosition.getLocation())) {
                        cleaned.add(0, currentPosition.getLocation()); // due to formating purposes, put at index 0
                    }
                } else {
                    System.out.println("LOW BATTERY needed 5 for C");
                    lowBattery = true;
                    return false;
                }
            }
            case TR -> {

                if (this.currentBattery >= 1) {
                    this.currentBattery -= 1;
                    this.currentPosition.setFacing(FACE_MAP.next(Arrays.asList(Facing.values()).indexOf(this.currentPosition.getFacing())));
                } else {
                    System.out.println("LOW BATTERY needed 1 for TR");
                    lowBattery = true;
                    return false;
                }

            }
            case TL -> {
                if (this.currentBattery >= 1) {
                    this.currentBattery -= 1;
                    this.currentPosition.setFacing(FACE_MAP.previous(Arrays.asList(Facing.values()).indexOf(this.currentPosition.getFacing())));
                } else {
                    System.out.println("LOW BATTERY needed 1 for TL!");
                    lowBattery = true;
                    return false;
                }
            }
            case A -> {

                if (this.currentBattery >= 2) {
                    this.currentBattery -= 2;
                    performedCorrectly = moveRobot(this.myQCleanerInput.getRoomMap(), true, recoveryCmd);

                } else {
                    System.out.println("LOW BATTERY needed 2 for A!");
                    lowBattery = true;
                    return false;
                }
            }
            case B -> {

                // can go back???
                if (this.currentBattery >= 3) {
                    this.currentBattery -= 3;
                    performedCorrectly = moveRobot( myQCleanerInput.getRoomMap(), false, recoveryCmd);
                } else {
                    System.out.println("LOW BATTERY needed 3 for B!");
                    lowBattery = true;
                    return false;
                }
            }

        }

        return performedCorrectly;
    }

    // moves robot and returns true if moved correctly
    public boolean moveRobot(String[][] roomMap, boolean foward, int recoverySequenceIndex) {
        boolean movedCorrectly = true;

        Point wantedDestination = new Point();

        switch (this.currentPosition.getFacing()) {
            case N -> wantedDestination.setLocation(this.currentPosition.getX(), this.currentPosition.getY() - (foward ? 1 : -1 ) );
            case E -> wantedDestination.setLocation(this.currentPosition.getX()+ (foward ? 1 : -1 ), this.currentPosition.getY());
            case S -> wantedDestination.setLocation(this.currentPosition.getX(), this.currentPosition.getY() + (foward ? 1 : -1 ));
            case W -> wantedDestination.setLocation(this.currentPosition.getX() - (foward ? 1 : -1 ) , this.currentPosition.getY());
        }

        boolean validDestination = false;

        // check room boundaries
        if (wantedDestination.getY() < roomMap.length && wantedDestination.getY() >= 0 &&
                wantedDestination.getX() < roomMap[0].length && wantedDestination.getX() >= 0) {

            // Check value of destination (readable)
            String wantedDestinationValue = roomMap[ (int) wantedDestination.getY() ][ (int) wantedDestination.getX() ];

            if (wantedDestinationValue != null && Arrays.stream(MQElement.values()).map(Enum::name).toList().contains(wantedDestinationValue) &&
                    MQElement.valueOf(wantedDestinationValue).equals(MQElement.S)) {

                validDestination = true;
            }
        }


        if (validDestination) {
            if (!this.visited.contains(wantedDestination)) {
                this.visited.add(0,wantedDestination); // due to formating purposes, put at index 0
            }
            // valid position, free to move
            this.currentPosition.setLocation(wantedDestination);
            movedCorrectly = true;

        } else {
            movedCorrectly = false;

            // start recovery Sequences, beware of recursion
            if (recoverySequenceIndex == 0) {
                System.out.println("RECOVERY 1 needed, non-valid destination of: <" + (int) wantedDestination.getX() + "," + (int) wantedDestination.getY() + ">");

                for (CmdType recoveryCmd : recoverySequence_1) {
                    if (!performCommand(recoveryCmd, 1)) {
                        break;
                    }
                }

            } else if (recoverySequenceIndex == 1) {
                System.out.println("RECOVERY 2 needed");
                for (CmdType recoveryCmd : recoverySequence_2) {
                    if (!performCommand(recoveryCmd, 2)) {
                        break;
                    }
                }
            } else if (recoverySequenceIndex == 2) {
                System.out.println("RECOVERY 3 needed");
                for (CmdType recoveryCmd : recoverySequence_3) {
                    if (!performCommand(recoveryCmd, 3)) {
                        break;
                    }
                }
            } else if (recoverySequenceIndex == 3) {
                System.out.println("RECOVERY 4 needed");
                for (CmdType recoveryCmd : recoverySequence_4) {
                    if (!performCommand(recoveryCmd, 4)) {
                        break;
                    }
                }
            } else if (recoverySequenceIndex == 4) {
                System.out.println("RECOVERY 5 needed");
                for (CmdType recoveryCmd : recoverySequence_5) {
                    if (!performCommand(recoveryCmd, 5)) {
                        break;
                    }
                }
            } else if (recoverySequenceIndex == 5) {
                this.robotStucked = true;
                System.out.println("STUCK / ending program");
            }



        }
        return movedCorrectly;
    }

    private void logResult() {

        // LOGing ofinal values
        System.out.println("\nCalculation ended! Results:");
        System.out.print("visited: [");
        this.visited.forEach(visitedElement -> {
            System.out.print("{X: " + (int)visitedElement.getX() + ", " + "Y: " +(int)visitedElement.getY() + "}");
        });
        System.out.println("]");
        System.out.print("cleaned: [");
        this.cleaned.forEach(cleanedElement -> {
            System.out.print("{X: " + (int)cleanedElement.getX() + ", " + "Y: " +(int)cleanedElement.getY() + "}");
        });
        System.out.println("]");
        this.currentPosition.logRobotPosition();
        System.out.println("battery: " + this.currentBattery);

    }

    public MQCleanerInput getMyQCleanerInput() {
        return myQCleanerInput;
    }

    public void setMyQCleanerInput(MQCleanerInput myQCleanerInput) {
        this.myQCleanerInput = myQCleanerInput;
    }

    public MyQCleanerResult getMyQCleanerResult() {
        return myQCleanerResult;
    }

    public void setMyQCleanerResult(MyQCleanerResult myQCleanerResult) {
        this.myQCleanerResult = myQCleanerResult;
    }


    public int getCurrentBattery() {
        return currentBattery;
    }

    public void setCurrentBattery(int currentBattery) {
        this.currentBattery = currentBattery;
    }

    public RobotPosition getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(RobotPosition currentPosition) {
        this.currentPosition = currentPosition;
    }

    public ArrayList<Point> getCleaned() {
        return cleaned;
    }

    public ArrayList<Point> getVisited() {
        return visited;
    }


}
