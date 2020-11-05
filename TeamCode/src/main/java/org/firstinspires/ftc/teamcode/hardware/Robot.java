package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class Robot {
    // todo set imu axis?
    public DeviceManager deviceManager;

    public Drivetrain drivetrain;
    public Intake intake;
    public Shooter shooter;
    public Arm arm;

    /**
     * Constructs a <code>Robot</code> object given
     * the OpMode's <code>HardwareMap</code>
     * @param hardwareMap  hardware map
     */
    public Robot(HardwareMap hardwareMap) {
        deviceManager = new DeviceManager(hardwareMap);
    }

    /** Initializes the robot's hardware */
    public void init() {
        // configure hardware on REV Control or Expansion Hub
        deviceManager.init();

        // construct software representations of subassemblies
        drivetrain = new Drivetrain(deviceManager);
        intake = new Intake(deviceManager);
        shooter = new Shooter(deviceManager);
        arm = new Arm(deviceManager);
    }
}
