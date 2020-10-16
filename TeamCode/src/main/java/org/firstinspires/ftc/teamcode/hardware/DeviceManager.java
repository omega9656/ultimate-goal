package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class DeviceManager {
    public HardwareMap hardwareMap;

    // drivetrain hardware
    public DcMotor backRightWheel;
    public DcMotor backLeftWheel;
    public DcMotor frontRightWheel;
    public DcMotor frontLeftWheel;

    // intake hardware
    public DcMotorEx leftIntake;
    public DcMotorEx rightIntake;

    // shooter hardware
    public DcMotorEx leftShooter;
    public DcMotorEx rightShooter;

    // arm hardware
    public DcMotorEx joint;
    public Servo grabber;

    // TODO declare any sensors here

    /**
     * Constructs a <code>DeviceManager</code> object given a
     * <code>HardwareMap</code>
     * @param hardwareMap  the robot's hardware map
     */
    public DeviceManager(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    /**
     * Initializes the robot's hardware by configuring them
     * on the REV Control or Expansion Hub
     */
    public void init() {
        // configure drivetrain hardware
        backRightWheel = hardwareMap.get(DcMotor.class, "back_right_wheel");
        backLeftWheel = hardwareMap.get(DcMotor.class, "back_left_wheel");
        frontRightWheel = hardwareMap.get(DcMotor.class, "front_right_wheel");
        frontLeftWheel = hardwareMap.get(DcMotor.class, "front_left_wheel");

        // configure intake hardware
        leftIntake = hardwareMap.get(DcMotorEx.class, "left_intake");
        rightIntake = hardwareMap.get(DcMotorEx.class, "right_intake");

        // configure shooter hardware
        leftShooter = hardwareMap.get(DcMotorEx.class, "left_shooter");
        rightShooter = hardwareMap.get(DcMotorEx.class, "right_shooter");

        // configure arm hardware
        joint = hardwareMap.get(DcMotorEx.class, "joint");
        grabber = hardwareMap.get(Servo.class, "grabber");

        // TODO configure any sensors here
    }
}
