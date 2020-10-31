package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Arm;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Shooter;

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

        while (gamepad2.left_bumper){
            robot.intake.run(Intake.Mode.IN);
        }

        while (gamepad2.right_bumper){
            robot.intake.run(Intake.Mode.OUT);
        }

        robot.intake.run(Intake.Mode.STOP);

    }

    public void shoot() {

        if(gamepad2.a) {
            robot.shooter.run(Shooter.Mode.SHOOT);
        } else {
            robot.shooter.run(Shooter.Mode.STOP);
        }
    }

    public void moveArm() {

        if (gamepad2.dpad_up){
            robot.arm.setJointPosition(Arm.Position.STOWED);
        } else if (gamepad2.dpad_down){
            robot.arm.setJointPosition(Arm.Position.DOWN);
        } else if (gamepad2.dpad_left){
            robot.arm.setJointPosition(Arm.Position.CARRY);
        }
    }

    public void moveGrabber() {

        if (gamepad2.x) {
            robot.arm.setGrabberMode(Arm.Mode.CLOSE);
        } else if (gamepad2.y) {
            robot.arm.setGrabberMode(Arm.Mode.OPEN);
        }
    }
}
