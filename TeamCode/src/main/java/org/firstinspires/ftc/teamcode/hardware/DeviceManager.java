package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * The <code>DeviceManager</code> class is used to configure
 * hardware objects in code with the REV Control/Expansion Hub.
 * Credit to FTC 16197 SWARM for this idea.
 * @see Robot
 */
public class DeviceManager {
    public HardwareMap hardwareMap;

    // drivetrain hardware
    public DcMotor backRight;
    public DcMotor backLeft;
    public DcMotor frontRight;
    public DcMotor frontLeft;

    // intake hardware
    public DcMotorEx intake;

    // shooter hardware
    public DcMotorEx flywheel;
    public Servo indexer;

    // arm hardware
    public DcMotorEx joint;
    public Servo grabber;

    /**
     * Constructs a {@link DeviceManager} object given a
     * {@link HardwareMap}
     * @param hardwareMap  the robot's hardware map
     */
    public DeviceManager(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    /**
     * Initializes the robot's hardware by configuring them
     * on the REV Control or Expansion Hub depending on if
     * an auto opmode is running.
     * <p>This matters because if we're running auto, roadrunner configures the drivetrain
     * wheels in {@link org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive}
     * so we don't have to. But if we're running teleop, we need to configure the wheels
     * ourselves.
     * </p>
     * @param runningAuto  whether the opmode is autonomous
     */
    public void init(boolean runningAuto) {
        // only configure dt hardware in teleop
        if (!runningAuto) {
            // configure drivetrain hardware
            backRight = hardwareMap.get(DcMotor.class, "back_right");
            backLeft = hardwareMap.get(DcMotor.class, "back_left");
            frontRight = hardwareMap.get(DcMotor.class, "front_right");
            frontLeft = hardwareMap.get(DcMotor.class, "front_left");
        }

        // configure intake hardware
        intake = hardwareMap.get(DcMotorEx.class, "intake");

        // configure shooter hardware
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        indexer = hardwareMap.get(Servo.class, "indexer");

        // configure arm hardware
        joint = hardwareMap.get(DcMotorEx.class, "joint");
        grabber = hardwareMap.get(Servo.class, "grabber");
    }
}
