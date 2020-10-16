package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Shooter {
    public DcMotorEx left;
    public DcMotorEx right;

    public Mode state;

    public enum Mode {
        // TODO tune shoot power level - or variable velocity?
        SHOOT(0, 0),
        STOP(0, 0);

        public double leftPower;
        public double rightPower;

        Mode(double leftPower, double rightPower) {
            this.leftPower = leftPower;
            this.rightPower = rightPower;
        }
    }

    /**
     * Constructs a <code>Shooter</code> object
     * given the robot's device manager
     * @param deviceManager  the robot's device manager
     */
    public Shooter(DeviceManager deviceManager) {
        left = deviceManager.leftShooter;
        right = deviceManager.rightShooter;

        // TODO set motor run modes

        // when power is set to 0, motors will stop and actively
        // resists any external force that might try to get the motor to move
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // set default state
        state = Mode.STOP;
    }

    /**
     * Runs the shooter given a shooter <code>Mode</code>
     * @param mode  the shooter's mode
     */
    public void run(Mode mode) {
        left.setPower(mode.leftPower);
        right.setPower(mode.rightPower);

        state = mode;
    }
}
