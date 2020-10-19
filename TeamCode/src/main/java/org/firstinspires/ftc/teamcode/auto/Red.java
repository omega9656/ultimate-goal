package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.Robot;

// javadocs: https://first-tech-challenge.github.io/FtcRobotController/6.0.1/RobotCore/com/qualcomm/robotcore/eventloop/opmode/OpMode.html

@Autonomous(name="Red", group="MCC")
public class Red extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode() {
        // ----- INIT -----
        // initialize robot hardware
        robot = new Robot(hardwareMap);
        robot.init();

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
