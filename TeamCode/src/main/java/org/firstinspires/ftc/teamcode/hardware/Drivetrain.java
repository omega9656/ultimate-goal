package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Drivetrain {
    // front is intake side of the robot
    // right is the right side if you are facing front
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

        // TODO may need to set motor modes here?
    }
}
