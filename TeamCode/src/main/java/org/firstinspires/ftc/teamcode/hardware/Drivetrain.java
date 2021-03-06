package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Drivetrain {
    // front is shooter side of the robot
    // "right" means the right side if you are in the POV of the robot (facing front)
    // all motors are goBILDA 19.2:1 planetaries
    public DcMotor backLeft;
    public DcMotor backRight;
    public DcMotor frontLeft;
    public DcMotor frontRight;

    /**
     * Constructs a {@link Drivetrain} object
     * @param deviceManager  the robot's device manager
     */
    public Drivetrain(DeviceManager deviceManager) {
        backLeft = deviceManager.backLeft;
        backRight = deviceManager.backRight;
        frontLeft = deviceManager.frontLeft;
        frontRight = deviceManager.frontRight;

        // set motor modes
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // set 0 power behavior to brake (actively resists movement)
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // All motors should rotate toward the front of the robot
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }
}
