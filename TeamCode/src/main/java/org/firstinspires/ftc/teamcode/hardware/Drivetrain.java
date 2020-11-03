package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Drivetrain {
    // front is intake side of the robot
    // right is the right side if you are facing front
    // all motors are goBILDA 19.2:1 planetaries
    public DcMotor backLeft;
    public DcMotor backRight;
    public DcMotor frontLeft;
    public DcMotor frontRight;

    /**
     * Constructs a <code>Drivetrain</code> object
     * @param deviceManager  the robot's device manager
     */
    public Drivetrain(DeviceManager deviceManager) {
        backLeft = deviceManager.backLeftWheel;
        backRight = deviceManager.backRightWheel;
        frontLeft = deviceManager.frontLeftWheel;
        frontRight = deviceManager.frontRightWheel;

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
    }
}
