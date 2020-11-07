package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Robot;

@Autonomous(name="Red", group="MCC")
public class Red extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode() {
        // ----- INIT -----
        // initialize robot hardware
        robot = new Robot(hardwareMap);
        robot.init(true);

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // detect height of rings and pick a path
        while (opModeIsActive()) {
            // TODO write this
        }

        waitForStart();


        // ----- START ----
        // TODO write this - break up into helper functions
        // run selected path


        // ----- STOP -----
        if (isStopRequested()) return;
    }
}
