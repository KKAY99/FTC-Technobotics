package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Constants.MotorConstants.ARM_SPEED
import org.firstinspires.ftc.teamcode.Constants.MotorConstants.DRIVE_SPEED
import org.firstinspires.ftc.teamcode.Constants.MotorConstants.PICKUP_POSITION
import org.firstinspires.ftc.teamcode.Constants.MotorConstants.SCORE_POSITION
import org.firstinspires.ftc.teamcode.Constants.MotorConstants.SERVO_END_POSITION
import org.firstinspires.ftc.teamcode.Constants.MotorConstants.SERVO_POSITION_INTERVAL
import org.firstinspires.ftc.teamcode.Constants.MotorConstants.SERVO_START_POSITION
import kotlin.math.abs
import kotlin.math.max

private const val HARDWARE_MAP_FRONT_LEFT_MOTOR = "frontLeftMotor"
private const val HARDWARE_MAP_FRONT_RIGHT_MOTOR = "frontRightMotor"
private const val HARDWARE_MAP_BACK_LEFT_MOTOR = "backLeftMotor"
private const val HARDWARE_MAP_BACK_RIGHT_MOTOR = "backRightMotor"
private const val HARDWARE_MAP_ARM_MOTOR = "armMotor"
private const val HARDWARE_MAP_SERVO_MOTOR = "servo"

private const val TELEMETRY_KEY_ROTATIONS = "Rotations"
private const val TELEMETRY_KEY_SPEED = "Speed"
private const val TELEMETRY_KEY_TRIGGER = "Trigger"
private const val TELEMETRY_KEY_Y_VALUE = "Y Value"

@TeleOp
class KotlinMecanumTeleOp : LinearOpMode() {

    enum class ArmState {
        NOTMOVING,
        MOVINGUP,
        MOVINGDOWN,
        TOPOSITIONLOW,
        TOPOSITIONHIGH
    }

    override fun runOpMode() {
        // DECLARE OUR MOTORS
        // MAKE SURE YOUR ID'S MATCH YOUR CONFIGURATION
        val frontLeftMotor: DcMotor = hardwareMap.dcMotor.get(HARDWARE_MAP_FRONT_LEFT_MOTOR)
        val backLeftMotor: DcMotor = hardwareMap.dcMotor.get(HARDWARE_MAP_BACK_LEFT_MOTOR)
        val frontRightMotor: DcMotor = hardwareMap.dcMotor.get(HARDWARE_MAP_FRONT_RIGHT_MOTOR)
        val backRightMotor: DcMotor = hardwareMap.dcMotor.get(HARDWARE_MAP_BACK_RIGHT_MOTOR)

        val armMotor: DcMotor = hardwareMap.dcMotor.get(HARDWARE_MAP_ARM_MOTOR)
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)

        val lidServo: Servo = hardwareMap.servo.get(HARDWARE_MAP_SERVO_MOTOR)
        var servoPosition: Double = SERVO_START_POSITION

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE)
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE)

        waitForStart()

        if (isStopRequested()) return

        while (opModeIsActive()) {
            // START SETUP MECANUM DRIVETRAIN MOTORS
            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            val maxPower: Double = if (gamepad1.left_trigger == 0F) {
                DRIVE_SPEED
            } else {
                DRIVE_SPEED + ((1 - DRIVE_SPEED) * gamepad1.left_trigger)
            }

            //limit speed to MaxPower
            val leftStickX =
                (gamepad1.left_stick_x * 1.1) * maxPower // Counteract imperfect strafing
            val rightStickX = gamepad1.right_stick_x * maxPower
            val rightStickY =
                gamepad1.left_stick_y * maxPower // Remember, Y stick value is reversed
            val denominator = max(abs(rightStickY) + abs(leftStickX) + abs(rightStickX), 1.0)
            val frontLeftPower = (rightStickY + leftStickX + rightStickX) / denominator
            val backLeftPower = (rightStickY - leftStickX + rightStickX) / denominator
            val frontRightPower = (rightStickY - leftStickX - rightStickX) / denominator
            val backRightPower = (rightStickY + leftStickX - rightStickX) / denominator

            frontLeftMotor.setPower(0 - frontLeftPower)
            backLeftMotor.setPower(0 - backLeftPower)
            frontRightMotor.setPower(frontRightPower)
            backRightMotor.setPower(backRightPower)
            telemetry.addData(TELEMETRY_KEY_ROTATIONS, frontLeftMotor.getCurrentPosition())
            telemetry.addData(TELEMETRY_KEY_SPEED, frontLeftMotor.getPower())
            telemetry.addData(TELEMETRY_KEY_TRIGGER, gamepad1.right_trigger)
            telemetry.addData(TELEMETRY_KEY_Y_VALUE, rightStickY)
            // END SETUP MECANUM DRIVETRAIN MOTORS

            // START GET CURRENT ARM STATE AND SET ARM MOTOR MODE AND POWER
            when (getArmState()) {
                ArmState.NOTMOVING -> {
                    armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
                    armMotor.setPower(0.0)
                }

                ArmState.MOVINGDOWN, ArmState.MOVINGUP -> {
                    armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
                    armMotor.setPower(-ARM_SPEED)
                }

                ArmState.TOPOSITIONLOW -> {
                    armMotor.setPower(ARM_SPEED)
                    armMotor.setTargetPosition(PICKUP_POSITION)
                    armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION)
                }

                ArmState.TOPOSITIONHIGH -> {
                    armMotor.setPower(ARM_SPEED)
                    armMotor.setTargetPosition(SCORE_POSITION)
                    armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION)
                }
            }
            // END GET CURRENT ARM STATE AND SET ARM MOTOR MODE AND POWER

            // START SET LID SERVO MOTOR POSITION
            if (gamepad1.y && servoPosition > SERVO_START_POSITION) {
                servoPosition -= SERVO_POSITION_INTERVAL
                // ADD SLEEP TO SLOW DOWN SETTING SERVO POSITION
                // sleep(10L)
            } else if (gamepad1.x && servoPosition < SERVO_END_POSITION) {
                servoPosition += SERVO_POSITION_INTERVAL
                // ADD SLEEP TO SLOW DOWN SETTING SERVO POSITION
                // sleep(10L)
            }

            lidServo.setPosition(servoPosition)
            // END SET LID SERVO MOTOR POSITION
        }
    }

    private fun getArmState(): ArmState = when {
        gamepad1.dpad_up -> ArmState.MOVINGUP
        gamepad1.dpad_down -> ArmState.MOVINGDOWN
        gamepad1.left_bumper -> ArmState.TOPOSITIONLOW
        gamepad1.right_bumper -> ArmState.TOPOSITIONHIGH
        else -> ArmState.NOTMOVING
    }
}