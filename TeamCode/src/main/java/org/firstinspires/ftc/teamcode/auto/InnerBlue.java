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

@Autonomous(name="Inner Blue v6", group="MCC")
public class InnerBlue extends LinearOpMode {
    ElapsedTime time;

    Robot robot;
    SampleMecanumDrive drive;

    // coordinate constants
    // TODO tune coordinates/angles as needed
    Pose2d startPose = new Pose2d(-60, 25, 0); // inner blue starting line

    public final double TOWER_SHOT_ANGLE = 15; // (degrees) rotate to face blue tower goal
    public final Vector2d BEHIND_LAUNCH_LINE = new Vector2d(0, 25);

    public final Vector2d TARGET_ZONE_A = new Vector2d(10, 40);
    public final Vector2d TARGET_ZONE_B = new Vector2d(0, 0);
    public final Vector2d TARGET_ZONE_C = new Vector2d(0, 40);

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
        executeAutoPath(targetZone);

        // ----- STOP -----
        if (isStopRequested()) return;
    }


    /**
     * Executes the (MCC) auto path, completing the following tasks:
     * - shoot 3 pre-loaded rings into high goal
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
        Trajectory[] trajectories = buildTrajectories(targetZone);
        followPath(trajectories);
    }

    /**
     * Builds the trajectories for the auto path
     * @param targetZone  target zone for wobble goal drop off. Either A, B, or C.
     * @return an array of built {@link Trajectory} objects
     */
    public Trajectory[] buildTrajectories(char targetZone) {
        Trajectory[] trajectories = new Trajectory[3];

        // move to right before launch line
        // shoot 3 pre-loaded rings into high goal at optimal speed
        trajectories[0] = drive.trajectoryBuilder(startPose)
                .lineTo(BEHIND_LAUNCH_LINE)
                .addDisplacementMarker(() -> {
                    shootThreeRings();
                })
                .build();

        // choose the right target zone coordinates
        Vector2d TARGET_ZONE_POS;
        if (targetZone == 'A') {
            TARGET_ZONE_POS = TARGET_ZONE_A;
        } else if (targetZone == 'B') {
            TARGET_ZONE_POS = TARGET_ZONE_B;
        } else { // target zone is C
            TARGET_ZONE_POS = TARGET_ZONE_C;
        }

        // move to specified target zone
        trajectories[1] = drive.trajectoryBuilder(trajectories[0].end())
                .lineTo(TARGET_ZONE_POS) // TODO use lineTo with heading change when odo ready
                .build();

        // park on launch line
        // target zone A: already parked, don't do anything
        trajectories[2] = null;
        if (targetZone == 'B') {
            trajectories[2] = drive.trajectoryBuilder(trajectories[1].end())
                    .lineTo(new Vector2d(10, 15))
                    .build();
        } else if (targetZone == 'C') {
            trajectories[2] = drive.trajectoryBuilder(trajectories[1].end())
                    .lineTo(TARGET_ZONE_A)
                    .build();
        }

        return trajectories;
    }

    /**
     * Follows the trajectories that were built and executes turns
     * @param trajectories  array of built trajectories
     */
    public void followPath(Trajectory[] trajectories) {
        // move to launch line and shoot
        drive.followTrajectory(trajectories[0]);
        drive.turn(Math.toRadians(TOWER_SHOT_ANGLE));

        // move to target zone
        drive.followTrajectory(trajectories[1]);

        // turn to drop off wobble goal
        /* Turn angle explanation:
         * We want robot to face center of field (turn 90 degrees)
         * that way the wobble goal arm is facing the target zone.
         * We also need to turn an additional TOWER_SHOT_ANGLE degrees to offset
         * how much the robot turns when shooting the 3 rings.
         */
        drive.turn(Math.toRadians(-90 - TOWER_SHOT_ANGLE));
        dropOffWobbleGoal();

        // park on launch line (if not already parked)
        if (trajectories[2] != null) {
            drive.followTrajectory(trajectories[2]);
        }
    }

    /**
     * Shoots 3 rings into the blue tower goal
     */
    public void shootThreeRings() {
        // todo tune this as needed
        final int INDEXER_WAIT_TIME = 100; // milliseconds

        int ringsShot = 0;
        robot.shooter.speedUpFlywheel();

        while (ringsShot < 3) {
            if (robot.shooter.isAtTargetVelocity()) {
                robot.shooter.flywheelMode = Shooter.FlywheelMode.SHOOT;

                if (robot.shooter.indexerMode == Shooter.IndexerMode.READY) {
                    robot.shooter.pushRing();
                    time.reset();
                    ringsShot++;
                } else if (time.time() >= INDEXER_WAIT_TIME) {
                    robot.shooter.readyIndexer();
                }
            }
        }
    }

    /**
     * Drops off the wobble goal
     */
    public void dropOffWobbleGoal() {
        // todo tune sleeps as needed
        robot.arm.down();
        sleep(300);
        robot.arm.open();
        sleep(150);
        robot.arm.carry();
        sleep(300);
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
