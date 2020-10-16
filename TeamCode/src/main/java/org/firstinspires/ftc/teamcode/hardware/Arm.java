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
        GRAB(0),
        RELEASE(0);

        public double servoPos;

        Mode(double servoPos) {
            this.servoPos = servoPos;
        }
    }

    /** Joint position */
    public enum Position {
        // TODO tune target positions
        UP(0),
        DOWN(0),
        CARRY(0);

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

        // TODO may need to STOP_AND_RESET_ENCODER? depends on init position
        joint.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // set default state
        grabberMode = Mode.RELEASE;
        jointPosition = Position.UP;
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
