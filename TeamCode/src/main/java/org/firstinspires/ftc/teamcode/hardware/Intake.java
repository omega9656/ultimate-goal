package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Intake {
    public DcMotorEx motor;  // 13.7:1 goBILDA yellow jacket planetary
    public Mode state;

    public enum Mode {
        IN(1),
        OUT(-0.3),  // lower power to avoid launching when outtaking
        STOP(0);

        public double power;

        Mode(double power) {
            this.power = power;
        }
    }

    /**
     * Constructs an <code>Intake</code> object given
     * the robot's device manager
     * @param deviceManager  the robot's device manager
     */
    public Intake(DeviceManager deviceManager) {
        motor = deviceManager.intake;

        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // when power is set to 0, motors will stop and actively
        // resists any external force that might try to get the motor to move
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // set default state
        state = Mode.STOP;
    }

    /**
     * Runs the intake motors at certain power levels
     * depending on the intake's mode
     * @param mode  the intake's current run mode
     */
    public void run(Mode mode) {
        motor.setPower(mode.power);
        state = mode;
    }

    /** Intakes rings */
    public void in() {
        run(Mode.IN);
    }

    /** Outtakes rings */
    public void out() {
        run(Mode.OUT);
    }

    /** Stops running the intake */
    public void stop() {
        run(Mode.STOP);
    }
}
