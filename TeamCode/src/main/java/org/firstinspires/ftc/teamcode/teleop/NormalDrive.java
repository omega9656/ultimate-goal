package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Shooter;


@TeleOp(name="Normal Drive")
public class NormalDrive extends OpMode {
    Robot robot;
    ElapsedTime time = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    boolean stalled = false; // whether the intake motor is stalled

    /**
     * Initializes the robot
     * <p>
     * - Indexer will move into READY position upon init
     * - Pre-condition: drive team has manually moved the arm into STOWED position
     * </p>
     */
    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        robot.init(false);
        time.reset();
    }

    /** Continuously checks gamepads for input while the OpMode is running */
    @Override
    public void loop() {
        // TODO see if drivers prefer normal, squared, or cubed drive
        // TODO tune strafe correction constant
        drive(DriveMode.NORMAL, 2);

        // todo tested: can't intake - hardware issue? outtake is fine.
        // todo also need to test if stall automation works, but that's low priority
        intake(false, false);

        shoot(true);
        moveArm(false);
        moveGrabber(false);
    }

    /**
     * Allows the drivers to drive the robot with G1
     * left and right joysticks. This is robot-centric drive.
     * <p>
     * G1 left stick - vertical/horizontal motion of robot
     * G1 right stick - rotation of robot
     * </p>
     * @param driveMode  the drive mode (normal, squared, cubed)
     * @param strafe  an experimentally determined constant that is multiplied
     *                by the x value to counteract imperfect strafing
     */
    public void drive(DriveMode driveMode, double strafe) {
        // https://gm0.copperforge.cc/en/stable/docs/software/mecanum-drive.html
        // https://www.chiefdelphi.com/t/paper-mecanum-and-omni-kinematic-and-force-analysis/106153/5 (3rd paper)

        // moving left joystick up means robot moves forward
        double vertical = -gamepad1.left_stick_y;  // flip sign because y axis is reversed on joystick

        // moving left joystick to the right means robot moves right
        double horizontal = gamepad1.left_stick_x * strafe;  // counteract imperfect strafing by multiplying by constant

        // moving right joystick to the right means clockwise rotation of robot
        double rotate = gamepad1.right_stick_x;

        // calculate initial power from gamepad inputs
        // to understand this, draw force vector diagrams (break into components)
        // and observe the goBILDA diagram on the GM0 page (linked above)
        double frontLeftPower = vertical + horizontal + rotate;
        double backLeftPower = vertical - horizontal + rotate;
        double frontRightPower = vertical - horizontal - rotate;
        double backRightPower = vertical + horizontal - rotate;

        // if there is a power level that is out of range
        if (
                Math.abs(frontLeftPower) > 1 ||
                Math.abs(backLeftPower) > 1 ||
                Math.abs(frontRightPower) > 1 ||
                Math.abs(backRightPower) > 1
        ) {
            // scale the power within [-1, 1] to keep the power levels proportional
            // (if the power is over 1 the FTC SDK will just make it 1)

            // find the largest power
            double max = Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower));
            max = Math.max(Math.abs(frontRightPower), max);
            max = Math.max(Math.abs(backRightPower), max);

            // scale everything with the ratio max:1
            // don't need to worry about signs because max is positive
            frontLeftPower /= max;
            backLeftPower /= max;
            frontRightPower /= max;
            backRightPower /= max;
        }

        // square or cube gamepad inputs
        if (driveMode == DriveMode.SQUARED) {
            // need to keep the sign, so multiply by absolute value of itself
            frontLeftPower *= Math.abs(frontLeftPower);
            backLeftPower *= Math.abs(backLeftPower);
            frontRightPower *= Math.abs(frontRightPower);
            backRightPower *= Math.abs(backRightPower);
        } else if (driveMode == DriveMode.CUBED) {
            frontLeftPower = Math.pow(frontLeftPower, 3);
            backLeftPower = Math.pow(backLeftPower, 3);
            frontRightPower = Math.pow(frontRightPower, 3);
            backRightPower = Math.pow(backRightPower, 3);
        } // if drive mode is normal, don't do anything

        // set final power values to motors
        robot.drivetrain.frontLeft.setPower(frontLeftPower);
        robot.drivetrain.backLeft.setPower(backLeftPower);
        robot.drivetrain.frontRight.setPower(frontRightPower);
        robot.drivetrain.backRight.setPower(backRightPower);
    }

    /**
     * Runs intake processes depending on gamepad input
     * and adds data for intake telemetry
     * <p>
     * G2 left bumper intakes rings
     * G2 right bumper outtakes rings
     * Otherwise, intake is stopped
     * </p>
     * @param showTelemetry  whether to display intake telemetry info
     * @param stallAutomation  whether you want to enable stall automation
     */
    public void intake(boolean showTelemetry, boolean stallAutomation) {
        // time to reverse if intake motor current is above stall current
        // TODO tune this as needed
        final double WAIT_TIME = 500; // milliseconds

        // https://www.gobilda.com/5202-series-yellow-jacket-planetary-gear-motor-13-7-1-ratio-435-rpm-3-3-5v-encoder/
        final double STALL_CURRENT = 9.2; // amps

        if (gamepad2.left_bumper) {
            robot.intake.in();

            // todo test stall automation
            if (stallAutomation) {
                // if the intake motor is stalling, run intake outward
                if (robot.intake.motor.getCurrent(CurrentUnit.AMPS) >= STALL_CURRENT && !stalled) {
                    stalled = true;
                    robot.intake.out();
                    time.reset();
                }

                // if the time we run the intake outward is expired, stop the intake
                if (time.time() > WAIT_TIME && stalled) {
                    robot.intake.stop();
                    stalled = false;
                }
            }
        } else if (gamepad2.right_bumper) {
            robot.intake.out();
        } else {
            robot.intake.stop();
        }


        if (showTelemetry) {
            telemetry.addLine("Intake")
                    .addData("Power", robot.intake.motor.getPower())
                    .addData("Current", "%.3f amps", robot.intake.motor.getCurrent(CurrentUnit.AMPS))
                    .addData("State", robot.intake.state);
        }
    }

    /**
     * Runs shooter processes depending on gamepad input
     * and adds data for shooter telemetry
     * <p>
     * G2 A button pressed speeds up the flywheel to target shooting velocity.
     * G2 B button pressed stops flywheel.
     * G2 right trigger moves the indexer to actually "shoot" the ring.
     * </p>
     * @param showTelemetry  whether to display shooter telemetry info
     */
    public void shoot(boolean showTelemetry) {
        // time it takes for indexer to move from READY to SHOOT position
        final double INDEXER_WAIT_TIME = 100; // milliseconds

        // --------- RUNNING THE FLYWHEEL ------------
        if (gamepad2.a) {
            robot.shooter.speedUpFlywheel(1);
        } else if (gamepad2.b) {
            robot.shooter.stopFlywheel();
        }

        // --------- ACTUALLY SHOOTING (MOVING INDEXER) ------------
        // if G2 right trigger pressed and indexer in ready mode, push ring
        if (gamepad2.right_trigger > 0.5 && robot.shooter.indexerMode == Shooter.IndexerMode.READY) {
            robot.shooter.pushRing();
            time.reset();
        }

        // if indexer in shoot mode and wait time has elapsed, move indexer back to ready position
        if (robot.shooter.indexerMode == Shooter.IndexerMode.SHOOT && time.time() >= INDEXER_WAIT_TIME) {
            robot.shooter.readyIndexer();
        }

        // --------- TELEMETRY ------------
        // todo figure out how to make flywheel run faster - use telemetry to check current velocity
        if (showTelemetry) {
            telemetry.addLine("Flywheel")
                    // todo revert velocity telemetry if needed
                  //  .addData("Velocity", "%.3f ticks/sec", robot.shooter.flywheel.getVelocity())
                  //  .addData("At target velocity?", robot.shooter.isAtTargetVelocity(1, 0.3))
                    .addData("Current", "%.3f amps", robot.shooter.flywheel.getCurrent(CurrentUnit.AMPS))
                    .addData("Power", robot.shooter.flywheel.getPower())
                    .addData("State", robot.shooter.flywheelMode);

            telemetry.addLine("Indexer")
                    .addData("Position", robot.shooter.indexer.getPosition())
                    .addData("State", robot.shooter.indexerMode);
        }
    }

    /**
     * Moves the joint of the arm depending on gampepad input
     * and adds data for arm telemetry
     * <p>
     * G2 dpad up puts arm in stowed position
     * G2 dpad down puts arm in down position
     * G2 dpad left puts arm in carry position
     * </p>
     * @param showTelemetry  whether to display shooter telemetry info
     */
    public void moveArm(boolean showTelemetry) {
        if (gamepad2.dpad_up) {
            robot.arm.stow();
        } else if (gamepad2.dpad_down) {
            robot.arm.down();
        } else if (gamepad2.dpad_left) {
            robot.arm.carry();
        } else if (gamepad2.dpad_right) {
            robot.arm.over_wall();
        }


        if (showTelemetry) {
            telemetry.addLine("Arm")
                    .addData("Position", robot.arm.joint.getTargetPosition())
                    .addData("State", robot.arm.jointPosition);
        }
    }

    /**
     * Moves the grabber depending on gamepad input
     * and adds data for grabber telemetry
     * <p>
     * G2 X button closes the grabber
     * G2 Y button opens the grabber
     * </p>
     * @param showTelemetry  whether to display shooter telemetry info
     */
    public void moveGrabber(boolean showTelemetry) {
        if (gamepad2.x) {
            robot.arm.close();
        } else if (gamepad2.y) {
            robot.arm.open();
        }

        if (showTelemetry) {
            telemetry.addLine("Grabber")
                    .addData("Position", robot.arm.grabber.getPosition())
                    .addData("State", robot.arm.grabberMode);
        }
    }
}
