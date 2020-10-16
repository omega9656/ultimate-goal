package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Intake {
    public DcMotorEx left;
    public DcMotorEx right;

    public Mode state;

    public enum Mode {
        // TODO tune motor power levels
        IN(1, -1),
        OUT(-0.3, 0.3),  // slightly lower power to avoid launching when outtaking
        STOP(0, 0);

        public double leftPower;
        public double rightPower;

        Mode(double leftPower, double rightPower) {
            this.leftPower = leftPower;
            this.rightPower = rightPower;
        }
    }

    /**
     * Constructs an <code>Intake</code> object given
     * the robot's device manager
     * @param deviceManager  the robot's device manager
     */
    public Intake(DeviceManager deviceManager) {
        left = deviceManager.leftIntake;
        right = deviceManager.rightIntake;

        // run motors without encoders
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // when power is set to 0, motors will stop and actively
        // resists any external force that might try to get the motor to move
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // set default state
        state = Mode.STOP;
    }

    /**
     * Runs the intake motors at certain power levels
     * depending on the intake's mode
     * @param mode  the intake's current run mode
     */
    public void run(Mode mode) {
        left.setPower(mode.leftPower);
        right.setPower(mode.rightPower);

        state = mode;
    }
}
