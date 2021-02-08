package org.firstinspires.ftc.teamcode.drive;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/*
 * NOTE: For the most part, drive constants for Ultimate Goal have been copied exactly
 * from the last drive constants from Skystone since the drivetrains are very similar.
 * https://github.com/omega9656/skystone/blob/master/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/drive/DriveConstants.java
 * TODO: Tune track width and wheel base and anything else that needs it
 */


/*
 * Constants shared between multiple drive types.
 *
 * TODO: Tune or adjust the following constants to fit your robot. Note that the non-final
 * fields may also be edited through the dashboard (connect to the robot's WiFi network and
 * navigate to https://192.168.49.1:8080/dash). Make sure to save the values here after you
 * adjust them in the dashboard; **config variable changes don't persist between app restarts**.
 *
 * These are not the only parameters; some are located in the localizer classes, drive base classes,
 * and op modes themselves.
 */
@Config
public class DriveConstants {

    /*
     * These are motor constants that should be listed online for your motors.
     */
    // https://www.gobilda.com/5202-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-312-rpm-3-3-5v-encoder/
    public static final double TICKS_PER_REV = 537.6;  // encoder countable events per rev (output shaft)
    public static final double MAX_RPM = 312;  // no-load speed @ 12VDC

    /*
     * Set RUN_USING_ENCODER to true to enable built-in hub velocity control using drive encoders.
     * Set this flag to false if drive encoders are not present and an alternative localization
     * method is in use (e.g., tracking wheels).
     *
     * If using the built-in motor velocity PID, update MOTOR_VELO_PID with the tuned coefficients
     * from DriveVelocityPIDTuner.
     */
    public static final boolean RUN_USING_ENCODER = false; // using feedforward
    // old values from tuned skystone bot
//    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(38, 0.8, 16,
//            getMotorVelocityF(MAX_RPM / 60 * TICKS_PER_REV));

    // ignore this bc using feedforward
    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(0, 0, 0,
            getMotorVelocityF(MAX_RPM / 60 * TICKS_PER_REV));

    /*
     * These are physical constants that can be determined from your robot (including the track
     * width; it will be tune empirically later although a rough estimate is important). Users are
     * free to chose whichever linear distance unit they would like so long as it is consistently
     * used. The default values were selected with inches in mind. Road runner uses radians for
     * angular distances although most angular parameters are wrapped in Math.toRadians() for
     * convenience. Make sure to exclude any gear ratio included in MOTOR_CONFIG from GEAR_RATIO.
     */
    // https://www.gobilda.com/3606-series-mecanum-wheel-set-bearing-supported-rollers-100mm-diameter/
    public static double WHEEL_RADIUS = 1.9685; // in (~50 mm)

    public static double GEAR_RATIO = 1.3650793651; // thanks noah. ratio is 1:1, but multiplied that by offset during straight test

    // fusion calculated: 15.5906 in (~396 mm)
    public static double TRACK_WIDTH = 11; // tuned track width from TurnTest is there. measured is 16 in.

    /*
     * These are the feedforward parameters used to model the drive motor behavior. If you are using
     * the built-in velocity PID, *these values are fine as is*. However, if you do not have drive
     * motor encoders or have elected not to use them for velocity control, these values should be
     * empirically tuned.
     */
    public static double kV = 0.022;
    public static double kA = 0.005;
    public static double kStatic = 0;

    /*
     * These values are used to generate the trajectories for you robot. To ensure proper operation,
     * the constraints should never exceed ~80% of the robot's actual capabilities. While Road
     * Runner is designed to enable faster autonomous motion, it is a good idea for testing to start
     * small and gradually increase them later after everything is working. All distance units are
     * inches.
     */
    // max vel and accel were tuned during feedforward velocity tuning
    public static double MAX_VEL = 40;
    public static double MAX_ACCEL = 40;
    public static double MAX_ANG_VEL = 3.680; // empirically found from MaxAngularVeloTuner
    public static double MAX_ANG_ACCEL = 3.680; // 1:1 arbitrary ratio btwn max ang vel and max ang accel

    // todo in order
    // BackAndForth below (basically done here)
    //   heading PID - start kP low, then increase. if shaky/oscillating, increase kD
    //   translational PID - start kP low, if overshooting/oscillating, increase kD
    // follower pid (for fine tuning if needed)
    // SplineTest

    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    public static double rpmToVelocity(double rpm) {
        return rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0;
    }

    public static double getMotorVelocityF(double ticksPerSecond) {
        // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx
        return 32767 / ticksPerSecond;
    }
}
