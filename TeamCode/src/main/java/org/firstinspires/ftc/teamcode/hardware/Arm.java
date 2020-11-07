package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    public DcMotorEx joint;  // controls angle of arm - 50.9:1 goBILDA yellow jacket planetary
    public Servo grabber;  // controls whether the claw is open/closed

    public Mode grabberMode;
    public Position jointPosition;

    // "encoder countable events per revolution" on goBILDA spec sheet
    // https://www.gobilda.com/5202-series-yellow-jacket-planetary-gear-motor-50-9-1-ratio-117-rpm-3-3-5v-encoder/
    public static final double TICKS_PER_REVOLUTION = 1425.2;

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

        // 90 deg CCW from STOWED (vertically straight up)
        // used when moving the wobble goal from one location to another
        CARRY(degreesToTicks(90)),

        // 180 deg CCW from STOWED (horizontally straight across)
        // used when grabbing/releasing the wobble goal on the ground
        DOWN(degreesToTicks(180));

        public int targetPos;

        Position(int targetPos) {
            this.targetPos = targetPos;
        }
    }

    /**
     * Constructs an {@link Arm} object
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
     * Returns the number of encoder ticks for this motor
     * for the given number of degrees
     * <p> This could result in some error because the method
     * casts the calculation to an <code>int</code> (encoder values are integers),
     * but the ticks per revolution of this motor is a <code>double</code>.
     * </p>
     * @param degrees  angle of rotation (counter-clockwise is positive)
     * @return the number of encoder ticks for this motor
     * for the given number of degrees
     */
    public static int degreesToTicks(int degrees) {
        final int DEGREES_PER_REVOLUTION = 360;
        return (int) ((TICKS_PER_REVOLUTION / DEGREES_PER_REVOLUTION) * degrees);
    }

    /**
     * Sets the joint position
     * @param position  arm position
     */
    public void setJointPosition(Position position) {
        joint.setTargetPosition(position.targetPos);
        jointPosition = position;
    }

    /** Puts the joint in the stowed position */
    public void stow() {
        setJointPosition(Position.STOWED);
    }

    /** Puts the joint in the carry position */
    public void carry() {
        setJointPosition(Position.CARRY);
    }

    /** Puts the joint in the down position */
    public void down() {
        setJointPosition(Position.DOWN);
    }

    /**
     * Sets the grabber mode
     * @param mode  grabber mode
     */
    public void setGrabberMode(Mode mode) {
        grabber.setPosition(mode.servoPos);
        grabberMode = mode;
    }

    /** Opens the grabber */
    public void open() {
        setGrabberMode(Mode.OPEN);
    }

    /** Closes the grabber */
    public void close() {
        setGrabberMode(Mode.CLOSE);
    }
}
