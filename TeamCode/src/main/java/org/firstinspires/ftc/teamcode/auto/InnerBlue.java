package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Shooter;

import java.util.List;
import java.util.Vector;

@Autonomous(name="Inner Blue v10", group="MCC")
public class InnerBlue extends LinearOpMode {
    ElapsedTime time;

    Robot robot;
    SampleMecanumDrive drive;

    Pose2d startPose = new Pose2d(-60, 25, 0); // inner blue starting line

    public final double TOWER_SHOT_ANGLE = 15; // (degrees) rotate to face blue tower goal

    // CV stuff
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    // NOTE: To access this vuforia development key again, sign into the Omega Vuforia Developer account
    // https://developer.vuforia.com/
    private static final String VUFORIA_KEY =
            "AcDQfuj/////AAABmXUjqtdnMEsGh6gDa7LYFopzRUf6HRbC0ikH7cae27nG9ziqFCiHTvzrkU3J62YaqQmZ" +
                    "rgYj1sKCNNxd7Aka3GoB9C4ciJNEJLFvYi3cP9HzG8iJf3MftEoeuaEV894LYyYKbUNIErDIGROR" +
                    "LkDctd7d+pktHnT57AxufAXj+MXOD4KxeHXlAugxJTFvDDkChUC6LJiFd/4MVUyhVKgOwYaJLzuG" +
                    "ZPijETVf/LchUlikMDG1QFK4WCAe1N/ke98+rXej6aVMUqEFzpFka2Be7tn2R6D0lQ0HDs4ezlqH" +
                    "2Fvj5T67iHy7Wy4QI8uavaeZC/to7oSzvu5Ff3HXNO9MEo/yIGsUqUHRs3HZwufIQBZz";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        // ----- INIT -----
        time = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        // initialize robot hardware
        robot = new Robot(hardwareMap);
        robot.init(true);

        // initialize roadrunner stuff
        drive = new SampleMecanumDrive(hardwareMap);
        drive.setPoseEstimate(startPose);

        // initialize CV stuff
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 1.78 or 16/9).

            // Uncomment the following line if you want to adjust the magnification and/or the aspect ratio of the input images.
            tfod.setZoom(1.5, 1.78);
        }

        char targetZone = 'A'; // default to target zone A if CV can't classify the ring stack

        // detect ring stack and pick a path during init
        while (!isStopRequested() && !opModeIsActive()) {
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());

                    if (updatedRecognitions.size() == 0) {
                        // no ring stacks detected
                        targetZone = 'A';
                    } else {
                        // list is not empty, so ring stack of 1 or 4 was detected
                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());

                            // set target zone based on label
                            if (recognition.getLabel().equals("Single")) {
                                targetZone = 'B';
                            } else if (recognition.getLabel().equals("Quad")) {
                                targetZone = 'C';
                            }
                        }
                    }
                    telemetry.addData("Target Zone", targetZone);
                }
            }
            telemetry.update();
        }

        if (tfod != null) {
            tfod.shutdown();
        }

        waitForStart();

        // ----- START ----
        // run selected path
        if (opModeIsActive() && !isStopRequested()) {
            executeAutoPath(targetZone);
        }

        // ---- STOP ----
        if (isStopRequested()) return;
    }


    /**
     * Executes the (MCC) auto path, completing the following tasks:
     * - shoot 3 pre-loaded rings into low goal
     * - drop off pre-loaded wobble goal
     * - park on launch line
     *
     * Pre-conditions:
     * - robot is on the inner blue starting line
     * - robot is pre-loaded with 1 wobble goal, and the arm is in CARRY position
     * - robot is pre-loaded with 3 rings
     * - robot's webcam is facing the ring stack
     * @param targetZone  target zone for wobble goal drop off. Either A, B, or C.
     */
    public void executeAutoPath(char targetZone) {
        // --- DISTANCE CONSTANTS ---
        double DIST_RING_STACK_AVOID = 6;
        double DIST_TO_LAUNCH_PT = 62; // distance from start to launch point

        double DIST_TO_TARGET_ZONE; // distance from launch line to target zone
        double DIST_TO_BACK_UP; // distance robot needs to back up to be next to target zone after moving to launch point
        double DIST_TO_STRAFE; // distance robot needs to strafe from wobble goal drop off point to launch line
        if (targetZone == 'A') {
            DIST_TO_TARGET_ZONE = 5;
            DIST_TO_BACK_UP = 15;
            DIST_TO_STRAFE = 5;
        } else if (targetZone == 'B') {
            DIST_TO_TARGET_ZONE = 10;
            DIST_TO_BACK_UP = 1;
            DIST_TO_STRAFE = 27;
        } else { // target zone is C
            DIST_TO_TARGET_ZONE = 23;
            DIST_TO_BACK_UP = 10;
            DIST_TO_STRAFE = 48;
        }


        // --- BUILD TRAJECTORIES --

        // move to right before launch line
        // shoot 3 pre-loaded rings into high goal at optimal speed
        Trajectory traj0 = drive.trajectoryBuilder(startPose)
                .strafeRight(DIST_RING_STACK_AVOID) // to avoid hitting the ring stack
                .build();

        Trajectory traj1 = drive.trajectoryBuilder(startPose)
                .forward(DIST_TO_LAUNCH_PT) // move 60 in forward to launch line
                .build();

        // move to specified target zone
        Trajectory traj2;
        if (targetZone == 'A') {
            traj2 = drive.trajectoryBuilder(traj1.end())
                    .back(10)
                    .build();
        } else if (targetZone == 'B') {
            traj2 = drive.trajectoryBuilder(traj1.end())
                    .forward(DIST_TO_TARGET_ZONE)
                    .build();
        } else { // target zone is C
            traj2 = drive.trajectoryBuilder(traj1.end())
                    .forward(DIST_TO_TARGET_ZONE)
                    .build();
        }

        // move robot backward to target zone to drop off wobble goal
        Trajectory traj3 = drive.trajectoryBuilder(traj2.end())
                .back(DIST_TO_BACK_UP)
                .build();

        // strafe right to park on launch line
        Trajectory traj4 = drive.trajectoryBuilder(traj3.end())
                .strafeRight(DIST_TO_STRAFE)
                .build();


        // --- FOLLOW TRAJECTORIES ---
        drive.followTrajectory(traj0); // strafe right to avoid hitting ring stack
        drive.followTrajectory(traj1); // go to launch line to get ready to shoot
        drive.turn(Math.toRadians(TOWER_SHOT_ANGLE)); // turn toward tower shot
        shootThreeRings(); // shoot 3 rings into low goal
        drive.turn(Math.toRadians(-TOWER_SHOT_ANGLE)); // undo the turning of the robot toward the tower goal

        drive.followTrajectory(traj2); // go to forward target zone
        drive.turn(Math.toRadians(-100)); // turn robot to the right (offset from -90 deg)
        drive.followTrajectory(traj3); // go backward to target zone
        dropOffWobbleGoal(); // drop off wobble goal

        drive.followTrajectory(traj4); // park on launch line

    }

    /**
     * Shoots 3 rings into the blue tower goal
     */
    public void shootThreeRings() {
        robot.shooter.speedUpFlywheel(1);
        sleep(1500); // ms

        // run loop 4 times because sometimes the 3rd ring is missed
        for (int i = 0; i < 4; i++) {
            robot.shooter.pushRing();
            sleep(500);
            robot.shooter.readyIndexer();
            sleep(700);
        }

        sleep(500);
        robot.shooter.stopFlywheel();
    }

    /**
     * Drops off the wobble goal
     */
    public void dropOffWobbleGoal() {
        robot.arm.down();
        sleep(1000);
        robot.arm.open();
        sleep(500);
        robot.arm.stow();
        sleep(1000);
    }


    // CV methods

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        // by default, object tracking is enabled. uncomment below to disable
        // tfodParameters.useObjectTracker = false;
        tfodParameters.minResultConfidence = 0.8f; // 80% confident the object is what you're trying to detect
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
}
