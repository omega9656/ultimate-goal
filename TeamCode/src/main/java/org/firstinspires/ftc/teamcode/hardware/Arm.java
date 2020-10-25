package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    public DcMotorEx joint;  // controls angle of arm
    public Servo grabber;  // controls whether the claw is open/closed

    public Mode grabberMode;
    public Position jointPosition;

    /** Grabber mode */
    public enum Mode {
        // TODO tune servo positions
        CLOSE(0),
        OPEN(0);

        public double servoPos;

        Mode(double servoPos) {
            this.servoPos = servoPos;
        }
    }

    /** Joint position */
    public enum Position {
        // resting against hard stop; this is also init position
        STOWED(0),

        // TODO tune target positions
        // 90 deg CCW from STOWED (vertically straight up)
        // used when moving the wobble goal from one location to another
        CARRY(0),

        // 90 deg CCW from CARRY (horizontally straight across)
        // used when grabbing/releasing the wobble goal on the ground
        DOWN(0);

        public int targetPos;

        Position(int targetPos) {
            this.targetPos = targetPos;
        }
    }

    /**
     * Constructs an <code>Arm</code> object
     * given the robot's device manager
     * @param deviceManager  the robot's device manager
     */
    public Arm(DeviceManager deviceManager) {
        joint = deviceManager.joint;
        grabber = deviceManager.grabber;

        // assume it is manually put into stowed position at the start of each match
        joint.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);  // stowed is considered 0
        joint.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // set default state
        grabberMode = Mode.OPEN;
        jointPosition = Position.STOWED;
    }

    /**
     * Lifts the arm based on the given arm position
     * @param position  arm position
     */
    public void setJointPosition(Position position) {
        joint.setTargetPosition(position.targetPos);
        jointPosition = position;
    }

    /**
     * Opens or closes the grabber given the grabber mode
     * @param mode  grabber mode
     */
    public void setGrabberMode(Mode mode) {
        grabber.setPosition(mode.servoPos);
        grabberMode = mode;
    }
}
