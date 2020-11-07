package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Shooter;


@TeleOp(name="MCC TeleOp")
public class MCCTeleOp extends OpMode {
    Robot robot;
    ElapsedTime time = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

    /** Initializes the robot */
    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        robot.init(false);
        time.reset();
    }

    /** Continuously checks gamepads for input while the OpMode is running */
    @Override
    public void loop() {
        drive();
        intake();
        shoot();
        moveArm();
        moveGrabber();
    }

    /**
     * Allows the drivers to drive the robot with G1
     * left and right joysticks. Also lets drivers
     * automatically align the robot to face one of the alliance goals.
     * G1 left, right joystick - drivetrain control
     * G1 B button - align robot to face to red goal
     * G1 X button - align robot to face blue goal
     */
    public void drive() {
        // todo mecanum drive (cubed?)
        // left and right stick for G1 - driving
    }

    /**
     * Runs intake processes depending on gamepad input
     * and adds data for intake telemetry
     * G2 left bumper intakes rings
     * G2 right bumper outtakes rings
     * Otherwise, intake is stopped
     */
    public void intake() {
        // time to reverse if intake motor current is above stall current
        final double TIME_TO_PRAY = 500; //milliseconds

        if (gamepad2.left_bumper) {
            robot.intake.in();

        // per https://www.gobilda.com/5202-series-yellow-jacket-planetary-gear-motor-13-7-1-ratio-435-rpm-3-3-5v-encoder/
            if (robot.intake.motor.getCurrent(CurrentUnit.AMPS) >= 9.2) {
                time.reset();

                //will run in 0.5 second intervals until current is less than stall current
                if (time.time() <= TIME_TO_PRAY){
                    robot.intake.out();
                }

            }
        } else if (gamepad2.right_bumper) {
            robot.intake.out();
        } else {
            robot.intake.stop();
        }

        telemetry.addLine("Intake")
                .addData("Power", robot.intake.motor.getPower())
                .addData("Current", "%.3f amps", robot.intake.motor.getCurrent(CurrentUnit.AMPS))
                .addData("State", robot.intake.state);
    }

    /**
     * Runs shooter processes depending on gamepad input
     * and adds data for shooter telemetry
     * G2 A button pressed speeds up the flywheel to target shooting velocity
     * Otherwise, shooter motor stops
     */
    public void shoot() {
        // time it takes for indexer to move from READY to SHOOT position
        final double INDEXER_WAIT_TIME = 100; // milliseconds

        // --------- RUNNING THE FLYWHEEL ------------
        // flywheel motor is running if it's in SHOOT or SPEEDING_UP mode
        boolean flywheelRunning =
                (robot.shooter.flywheelMode == Shooter.FlywheelMode.SHOOT) ||
                (robot.shooter.flywheelMode == Shooter.FlywheelMode.SPEEDING_UP);

        if (gamepad2.a && !flywheelRunning) {
            robot.shooter.speedUpFlywheel();
        } else if (gamepad2.a) {
            // pressing A again when the flywheel is running will stop the flywheel
            robot.shooter.stopFlywheel();
        }

        // --------- ACTUALLY SHOOTING (MOVING INDEXER) ------------
        while (gamepad2.right_trigger > 0.5) {
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


        // --------- TELEMETRY ------------
        telemetry.addLine("Flywheel")
                .addData("Velocity", "%.3f ticks/sec", robot.shooter.flywheel.getVelocity())
                .addData("Current", "%.3f amps", robot.shooter.flywheel.getCurrent(CurrentUnit.AMPS))
                .addData("Power", robot.shooter.flywheel.getPower())
                .addData("State", robot.shooter.flywheelMode);

        telemetry.addLine("Indexer")
                .addData("Position", robot.shooter.indexer.getPosition())
                .addData("State", robot.shooter.indexerMode);
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
