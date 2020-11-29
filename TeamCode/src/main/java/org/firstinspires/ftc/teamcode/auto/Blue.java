package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.path.Path;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Shooter;

@Autonomous(name="Blue", group="MCC")
public class Blue extends LinearOpMode {
    Robot robot;
    ElapsedTime time = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

    @Override
    public void runOpMode() {
        // ----- INIT -----
        // initialize robot hardware
        robot = new Robot(hardwareMap);
        robot.init(true);
        time.reset();

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(-60, -50, Math.toRadians(180));

        // detect height of rings and pick a path
        while (opModeIsActive()) {
            // TODO write this
        }

        waitForStart();

        drive.setPoseEstimate(startPose);

        // ----- START ----
        // TODO write this - break up into helper functions

//        Trajectory trajectory1 = drive.trajectoryBuilder(startPose)
//                .lineTo(new Vector2d(0, -40))
//                .lineTo(new Vector2d())
//                .build();

        Trajectory trajectory1 = makeTrajectory(drive, startPose,
                new double[][] {
                    {0, 35, 180}
                }
        );
        Trajectory trajectory2 = makeTrajectory(drive, startPose,
                new double[][] {
                        {-50, 25, 180}
                }
        );




//        Trajectory trajectoryPart1 = drive.trajectoryBuilder(startPose)
//                .lineToLinearHeading(new Pose2d(-60, 50, Math.toRadians(0)))
//                .lineToLinearHeading(new Pose2d(0, 35, Math.toRadians(180)));


        // run selected path

        drive.followTrajectory(trajectory1);
        shoot();
        drive.followTrajectory(trajectory2);

        // ----- STOP -----
        if (isStopRequested()) return;
    }

    public static Trajectory makeTrajectory(SampleMecanumDrive drive, Pose2d startPose, double[][] points) {
        TrajectoryBuilder trajectoryBuilder = drive.trajectoryBuilder(startPose);

        for (double[] coord : points) {
            if (coord.length == 2)
                trajectoryBuilder = trajectoryBuilder.lineTo(new Vector2d(coord[0], coord[1]));
            else if (coord.length == 3)
                trajectoryBuilder = trajectoryBuilder.lineToLinearHeading(new Pose2d(coord[0], coord[1], Math.toRadians(180)));
        }

        return trajectoryBuilder.build();
    }

    public void shoot() {
        final double INDEXER_WAIT_TIME = 100; // milliseconds

        // --------- RUNNING THE FLYWHEEL ------------
        // flywheel motor is running if it's in SHOOT or SPEEDING_UP mode

        boolean flywheelRunning =
                (robot.shooter.flywheelMode == Shooter.FlywheelMode.SHOOT) ||
                (robot.shooter.flywheelMode == Shooter.FlywheelMode.SPEEDING_UP);

        if (!flywheelRunning) {
            robot.shooter.speedUpFlywheel();
        } else {
            robot.shooter.stopFlywheel();
        }

        // --------- ACTUALLY SHOOTING (MOVING INDEXER) ------------
        if (robot.shooter.isAtTargetVelocity()) {
            robot.shooter.flywheelMode = Shooter.FlywheelMode.SHOOT;

            if (robot.shooter.indexerMode == Shooter.IndexerMode.READY) {
                robot.shooter.pushRing();
                time.reset();
            } else if (time.time() >= INDEXER_WAIT_TIME) {
                robot.shooter.readyIndexer();
            }
        }
    }

    public void test2() {

    }
}