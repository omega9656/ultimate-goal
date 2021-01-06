package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;

@TeleOp(name="Test Drivetrain v4", group="Test")
public class TestDrivetrain extends OpMode {
    Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        robot.init(false);
    }

    @Override
    public void loop() {
        // test motor directions
        double power = -gamepad1.left_stick_y; // y axis is flipped
        robot.drivetrain.backLeft.setPower(power);
        telemetry.addData("Power", power);
    }
}
