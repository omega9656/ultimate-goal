package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Shooter {
    public DcMotorEx motor; // gobilda 1:1 yellow jacket planetary

    // https://www.gobilda.com/5202-series-yellow-jacket-motor-1-1-ratio-24mm-length-6mm-d-shaft-6000-rpm-3-3-5v-encoder/
    public static final int TICKS_PER_REVOLUTION = 28;  // encoder countable events per revolution (output shaft)
    public static final int MAX_RPM = 5400;  // no-load speed @ 12VDC (tested)
    public static final int MAX_TICKS_PER_SEC = MAX_RPM * TICKS_PER_REVOLUTION / 60;

    public Mode state;

    public enum Mode {
        // TODO tune shooting speed - do variable velocities after MCC
        SHOOT(getShootingSpeed(0.8)),  // optimal performance at 80% of max speed
        STOP(0);

        public double velocity;

        Mode(double velocity) {
            this.velocity = velocity;
        }
    }

    /**
     * Constructs a <code>Shooter</code> object
     * given the robot's device manager
     * @param deviceManager  the robot's device manager
     */
    public Shooter(DeviceManager deviceManager) {
        motor = deviceManager.shooter;

        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // when power is set to 0, motors will stop and actively
        // resists any external force that might try to get the motor to move
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // set default state
        state = Mode.STOP;
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
     * Runs the shooter given a shooter <code>Mode</code>
     * @param mode  the shooter's mode
     */
    public void run(Mode mode) {
        motor.setVelocity(mode.velocity);
        state = mode;
    }

    /** Runs the shooter motor */
    public void shoot() {
        run(Mode.SHOOT);
    }

    /** Stops the shooter motor */
    public void stop() {
        run(Mode.STOP);
    }
}
