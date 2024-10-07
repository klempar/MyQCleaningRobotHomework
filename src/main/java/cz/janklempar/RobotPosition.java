package cz.janklempar;

import java.awt.*;

import static cz.janklempar.WorldDirection.Facing;

public class RobotPosition extends Point {

    private Facing facing;

    public Facing getFacing() {
        return facing;
    }

    public void setFacing(Facing facing) {
        this.facing = facing;
    }


    public RobotPosition(int x, int y, Facing facing) {
        super(x, y);
        this.facing = facing;
    }

    public void logRobotPosition() {
        System.out.print("final={ X: " + (int)this.getX() + ", Y: " + (int)this.getY() + ", facing: " + this.facing + "}");
    }

}