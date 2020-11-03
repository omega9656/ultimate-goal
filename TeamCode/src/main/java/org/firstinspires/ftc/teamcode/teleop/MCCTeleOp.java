package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
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
    }

    public void drive() {
        // TODO mecanum drive (cubed?)
    }

    /**
     * Runs intake processes depending on gamepad input
     * and adds data for intake telemetry
     * G2 left bumper intakes rings
     * G2 right bumper outtakes rings
     * Otherwise, intake is stopped
     */
    public void intake() {
        if (gamepad2.left_bumper) {
            robot.intake.in();
        } else if (gamepad2.right_bumper) {
            robot.intake.out();
        } else {
            robot.intake.stop();
        }

        telemetry.addLine("Intake left")
                .addData("Power", robot.intake.left.getPower())
                .addData("Current", "%f amps", robot.intake.left.getCurrent(CurrentUnit.AMPS));
        telemetry.addLine("Intake right")
                .addData("Power", robot.intake.right.getPower())
                .addData("Current", "%f amps", robot.intake.right.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Intake state", robot.intake.state);
    }

    /**
     * Runs shooter processes depending on gamepad input
     * and adds data for shooter telemetry
     * G2 A button pressed runs shooter motor
     * Otherwise, shooter motor stops
     */
    public void shoot() {
        if (gamepad2.a) {
            robot.shooter.shoot();
        } else {
            robot.shooter.stop();
        }

        telemetry.addLine("Shooter")
                .addData("Velocity", "%f ticks/sec", robot.shooter.motor.getVelocity())
                .addData("Current", "%f amps", robot.shooter.motor.getCurrent(CurrentUnit.AMPS))
                .addData("Power", robot.shooter.motor.getPower())
                .addData("State", robot.shooter.state);
    }

    /**
     * Moves the joint of the arm depending on gampepad input
     * and adds data for arm telemetry
     * G2 dpad up puts arm in stowed position
     * G2 dpad down puts arm in down position
     * G2 dpad left puts arm in carry position
     */
    public void moveArm() {
        if (gamepad2.dpad_up) {
            robot.arm.stow();
        } else if (gamepad2.dpad_down) {
            robot.arm.down();
        } else if (gamepad2.dpad_left) {
            robot.arm.carry();
        }

        telemetry.addLine("Arm")
                .addData("Position", robot.arm.joint.getTargetPosition())
                .addData("Position tolerance", robot.arm.joint.getTargetPositionTolerance()) // todo ask what this is
                .addData("State", robot.arm.jointPosition);
    }

    /**
     * Moves the grabber depending on gamepad input
     * and adds data for grabber telemetry
     * G2 X button closes the grabber
     * G2 Y button opens the grabber
     */
    public void moveGrabber() {
        if (gamepad2.x) {
            robot.arm.close();
        } else if (gamepad2.y) {
            robot.arm.open();
        }

        telemetry.addLine("Grabber")
                .addData("Position", robot.arm.grabber.getPosition())
                .addData("State", robot.arm.grabberMode);
    }
}
