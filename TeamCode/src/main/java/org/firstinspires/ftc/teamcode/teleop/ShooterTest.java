package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.Robot;

import java.util.ResourceBundle;

@Autonomous(name="Shooter Test")
public class ShooterTest extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap);
        robot.init(false);

        waitForStart();

        while (opModeIsActive()) {
            shootThreeRings(1, 3, 1500, 500, 700, 500);
            if (isStopRequested()) {
                return;
            }
        }
    }

    public void shootThreeRings(
            double flywheelSpeed,
            int numRings,
            long speedUpWaitTime,
            long pushRingWaitTime,
            long readyIndexerWaitTime,
            long stopWaitTime
    ) {
        robot.shooter.speedUpFlywheel(flywheelSpeed);
        sleep(speedUpWaitTime); // ms

        for (int i = 0; i < numRings; i++) {
            robot.shooter.pushRing();
            sleep(pushRingWaitTime);
            robot.shooter.readyIndexer();
            sleep(readyIndexerWaitTime);
        }

        sleep(stopWaitTime);
        robot.shooter.stopFlywheel();
    }
}
