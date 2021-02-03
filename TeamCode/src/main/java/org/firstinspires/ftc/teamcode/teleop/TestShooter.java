package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.hardware.Robot;

@TeleOp(name="Test Shooter v1", group="Test")
public class TestShooter extends OpMode {
    Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        robot.init(false);
    }

    @Override
    public void loop() {
        // test flywheel motor
        DcMotorEx flywheel = robot.shooter.flywheel;
        flywheel.setPower(1);
        telemetry.addData("Power", flywheel.getPower());
        telemetry.addData("Current (amps)", flywheel.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Velocity (ticks/sec)", flywheel.getVelocity());
    }
}
