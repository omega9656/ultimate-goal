package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;

@TeleOp(name="MCC TeleOp")
public class MCCTeleOp extends OpMode {
    Robot robot;

    @Override
    /** Initializes the robot */
    public void init() {
        robot = new Robot(hardwareMap);
        robot.init();
    }

    @Override
    /** Continuously checks gamepads for input while the OpMode is running */
    public void loop() {
        drive();
        intake();
        shoot();
        moveArm();
        moveGrabber();
        updateTelemetry(telemetry);
    }


    // TODO for each subassembly process, do telemetry.addData() for relevant stuff
    // https://first-tech-challenge.github.io/FtcRobotController/6.0.1/RobotCore/org/firstinspires/ftc/robotcore/external/Telemetry.html
    // gamepads: https://first-tech-challenge.github.io/FtcRobotController/6.0.1/RobotCore/com/qualcomm/robotcore/hardware/Gamepad.html

    public void drive() {
        // TODO this
    }

    public void intake() {
        // TODO this
    }

    public void shoot() {
        // TODO this
    }

    public void moveArm() {
        // TODO this
    }

    public void moveGrabber() {
        // TODO this
    }
}
