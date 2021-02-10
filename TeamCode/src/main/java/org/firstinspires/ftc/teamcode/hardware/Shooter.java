package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class Shooter {
    public DcMotorEx flywheel; // gobilda 1:1 yellow jacket planetary
    public Servo indexer; // pushes ring into place

    // https://www.gobilda.com/5202-series-yellow-jacket-motor-1-1-ratio-24mm-length-6mm-d-shaft-6000-rpm-3-3-5v-encoder/
    public static final int TICKS_PER_REVOLUTION = 28;  // encoder countable events per revolution (output shaft)
    public static final int MAX_RPM = 5400;  // no-load speed @ 12VDC (tested)
    public static final int MAX_TICKS_PER_SEC = MAX_RPM * TICKS_PER_REVOLUTION / 60;

    public FlywheelMode flywheelMode;
    public IndexerMode indexerMode;

    // todo tune these values
    public static final double DEFAULT_SHOOTING_SPEED = 0.8;  // 80% of max speed
    public static final double DEFAULT_SHOOTING_SPEED_TOLERANCE = 0.01;  // +/- 1% of target velocity

    public enum FlywheelMode {
        // TODO tune shooting speed - do variable velocities after MCC
        SHOOT,  // at target shooting velocity
        SPEEDING_UP,  // speeding up the flywheel to target velocity
        STOP,
    }

    public enum IndexerMode {
        SHOOT(0.3),  // pushing the ring to the flywheel
        READY(0);  // not pushing the ring

        public double servoPos;

        IndexerMode(double servoPos) {
            this.servoPos = servoPos;
        }
    }

    /**
     * Constructs a {@link Shooter} object
     * given the robot's device manager
     * @param deviceManager  the robot's device manager
     */
    public Shooter(DeviceManager deviceManager) {
        flywheel = deviceManager.flywheel;
        indexer = deviceManager.indexer;

        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // when power is set to 0, motors will stop and actively
        // resists any external force that might try to get the motor to move
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // set default states
        flywheelMode = FlywheelMode.STOP;
        readyIndexer();
    }

    /**
     * Returns the shooting speed in ticks per second given
     * a desired percentage of the motor's max speed as a decimal
     * @param percent  percent of the motor's max speed as a decimal
     * @return the shooting speed in ticks per second
     */
    public static double getShootingSpeed(double percent) {
        return MAX_TICKS_PER_SEC * percent;
    }

    /**
     * Speeds up the flywheel to the default shooting speed
     * of 80% of max speed
     */
    public void speedUpFlywheel() {
        flywheel.setVelocity(getShootingSpeed(DEFAULT_SHOOTING_SPEED));
        flywheelMode = FlywheelMode.SPEEDING_UP;
    }

    /**
     * Speeds up the flywheel to a percentage of the
     * max speed
     * @param percent  percentage of the max speed as a decimal
     */
    public void speedUpFlywheel(double percent) {
        flywheel.setVelocity(getShootingSpeed(percent));
        flywheelMode = FlywheelMode.SPEEDING_UP;
    }

    /** Stops running the flywheel motor */
    public void stopFlywheel() {
        flywheel.setVelocity(0);
        flywheelMode = FlywheelMode.STOP;
    }

    // todo figure out if the isAtTargetVelocity methods are really needed and if they're programmed right
    /**
     * Returns <code>true</code> if the flywheel motor
     * is at default target velocity (within the default shooting
     * speed tolerance).
     * @return <code>true</code> if the flywheel motor is at target velocity
     */
    public boolean isAtTargetVelocity() {
        double targetVelocity = getShootingSpeed(DEFAULT_SHOOTING_SPEED);
        return Math.abs(flywheel.getVelocity() - targetVelocity) <= (DEFAULT_SHOOTING_SPEED_TOLERANCE * targetVelocity);
    }

    /**
     * Returns <code>true</code> if the flywheel motor
     * is at a given target velocity (within the default shooting speed tolerance).
     * @param targetVelocityPercent  target velocity of the flywheel motor, as a percent (decimal) of the max motor speed
     * @return <code>true</code> if the flywheel motor is at target velocity
     */
    public boolean isAtTargetVelocity(double targetVelocityPercent) {
        double targetVelocity = getShootingSpeed(targetVelocityPercent);
        return Math.abs(flywheel.getVelocity() - targetVelocity) <= (DEFAULT_SHOOTING_SPEED_TOLERANCE * targetVelocity);
    }

    /**
     * Returns <code>true</code> if the flywheel motor
     * is at a given target velocity (within a given shooting
     * speed tolerance).
     * @param targetVelocityPercent  target velocity of the flywheel motor, as a percent (decimal) of the max motor speed
     * @param tolerance  target velocity tolerance, as a percent (decimal) of the calculated target velocity
     * @return <code>true</code> if the flywheel motor is at target velocity
     */
    public boolean isAtTargetVelocity(double targetVelocityPercent, double tolerance) {
        double targetVelocity = getShootingSpeed(targetVelocityPercent);
        return Math.abs(flywheel.getVelocity() - targetVelocity) <= (tolerance * targetVelocity);
    }

    /** Puts the indexer in SHOOT mode, pushing a ring toward the flywheel */
    public void pushRing() {
        indexer.setPosition(IndexerMode.SHOOT.servoPos);
        indexerMode = IndexerMode.SHOOT;
    }

    /** Puts the indexer in READY mode */
    public void readyIndexer() {
        indexer.setPosition(IndexerMode.READY.servoPos);
        indexerMode = IndexerMode.READY;
    }
}
