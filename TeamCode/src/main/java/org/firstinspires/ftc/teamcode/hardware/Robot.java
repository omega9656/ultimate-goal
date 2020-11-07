package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Models our Ultimate Goal robot with its subassemblies
 * @see DeviceManager
 * @see Drivetrain
 * @see Intake
 * @see Shooter
 * @see Arm
 */
public class Robot {
    public DeviceManager deviceManager;

    public Drivetrain drivetrain;
    public Intake intake;
    public Shooter shooter;
    public Arm arm;

    /**
     * Constructs a {@link Robot} object given
     * the OpMode's {@link HardwareMap}
     * @param hardwareMap  hardware map
     */
    public Robot(HardwareMap hardwareMap) {
        deviceManager = new DeviceManager(hardwareMap);
    }

    /**
     * Initializes the robot's hardware
     * @param runningAuto  whether an autonomous opmode is being run
     */
    public void init(boolean runningAuto) {
        // configure hardware on REV Control or Expansion Hub
        deviceManager.init(runningAuto);

        // construct software representations of subassemblies
        // only create Drivetrain object if running teleop (if auto, roadrunner handles it)
        if (!runningAuto) {
            drivetrain = new Drivetrain(deviceManager);
        }

        intake = new Intake(deviceManager);
        shooter = new Shooter(deviceManager);
        arm = new Arm(deviceManager);
    }
}
