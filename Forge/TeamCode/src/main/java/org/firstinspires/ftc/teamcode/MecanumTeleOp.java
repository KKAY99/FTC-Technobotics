package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class MecanumTeleOp extends LinearOpMode {

    enum ArmState {
        NOTMOVING,
        MOVINGUP,
        MOVINGDOWN,
        TOPOSITIONLOW,
        TOPOSITIONHIGH
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // DECLARE OUR MOTORS
        // MAKE SURE YOUR ID'S MATCH YOUR CONFIGURATION
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        DcMotor ftcArm = hardwareMap.dcMotor.get("armMotor");
        ftcArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Servo lidServo = hardwareMap.servo.get("servo");
        double servoPosition = Constants.MotorConstants.SERVO_START_POSITION;

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            // START SETUP MECANUM DRIVETRAIN MOTORS
            double y = gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double trigger = gamepad1.right_trigger;

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double maxPower;
            if (gamepad1.left_trigger == 0) {
                maxPower = Constants.MotorConstants.DRIVE_SPEED;
            } else {
                double triggerValue = (gamepad1.left_trigger);
                maxPower = Constants.MotorConstants.DRIVE_SPEED +
                        ((1 - Constants.MotorConstants.DRIVE_SPEED) * triggerValue);
            }
            //limit speed to MaxPower
            y = y * maxPower;
            x = x * maxPower;
            rx = rx * maxPower;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            frontLeftMotor.setPower(0 - frontLeftPower);
            backLeftMotor.setPower(0 - backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
            telemetry.addData("Rotations", frontLeftMotor.getCurrentPosition());
            telemetry.addData("speed", frontLeftMotor.getPower());
            telemetry.addData("Trigger", trigger);
            telemetry.addData("Y value", y);
            // END SETUP MECANUM DRIVETRAIN MOTORS

            // START GET CURRENT ARM STATE AND SET ARM MOTOR MODE AND POWER
            ArmState ftcArmState = getArmState();
            switch (ftcArmState) {
                case NOTMOVING:
                    ftcArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    ftcArm.setPower(0);
                    break;
                case MOVINGDOWN:
                    ftcArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    ftcArm.setPower(-Constants.MotorConstants.ARM_SPEED);
                    break;
                case MOVINGUP:
                    ftcArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    ftcArm.setPower(Constants.MotorConstants.ARM_SPEED);
                    break;
                case TOPOSITIONLOW:
                    ftcArm.setPower(Constants.MotorConstants.ARM_SPEED);
                    ftcArm.setTargetPosition(Constants.MotorConstants.PICKUP_POSITION);
                    ftcArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    break;
                case TOPOSITIONHIGH:
                    ftcArm.setPower(Constants.MotorConstants.ARM_SPEED);
                    ftcArm.setTargetPosition(Constants.MotorConstants.SCORE_POSITION);
                    ftcArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    break;
            }
            // END GET CURRENT ARM STATE AND SET ARM MOTOR MODE AND POWER

            // START SET LID SERVO MOTOR POSITION
            if (gamepad1.y && servoPosition > Constants.MotorConstants.SERVO_START_POSITION) {
                servoPosition -= Constants.MotorConstants.SERVO_POSITION_INTERVAL;
                // ADD SLEEP TO SLOW DOWN SETTING SERVO POSITION
                // sleep(10L);
            } else if (gamepad1.x && servoPosition < Constants.MotorConstants.SERVO_END_POSITION) {
                servoPosition += Constants.MotorConstants.SERVO_POSITION_INTERVAL;
                // ADD SLEEP TO SLOW DOWN SETTING SERVO POSITION
                // sleep(10L);
            }

            lidServo.setPosition(servoPosition);
            // END SET LID SERVO MOTOR POSITION
        }
    }

    private void autoDrive(double xSpeed, double ySpeed, int rotation, double distance) {
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        // Reset the motor encoder so that it reads zero ticks
        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Turn the motor back on, required if you use STOP_AND_RESET_ENCODER
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        double x = xSpeed;
        double y = ySpeed;
        double rx = 0;
        double denominator, frontLeftPower, backLeftPower, frontRightPower, backRightPower;
        int distancetraveled = frontLeftMotor.getCurrentPosition();
        while (distancetraveled <= distance) {
            telemetry.addData("Encoder ", frontLeftMotor.getCurrentPosition());

            distancetraveled = Math.abs(frontLeftMotor.getCurrentPosition());
            y = 0.6; // Remember, Y stick value is reversed
            x = 0; // Counteract imperfect strafing
            rx = 0;
            denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            frontLeftPower = (y + x + rx) / denominator;
            backLeftPower = (y - x + rx) / denominator;
            frontRightPower = (y - x - rx) / denominator;
            backRightPower = (y + x - rx) / denominator;
            frontLeftMotor.setPower(0 - frontLeftPower);
            backLeftMotor.setPower(0 - backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
        }
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);

    }

    private @NonNull ArmState getArmState() {
        ArmState ftcArmState = ArmState.NOTMOVING;
        if (gamepad1.dpad_up) {
            ftcArmState = ArmState.MOVINGUP;
        }
        if (gamepad1.dpad_down) {
            ftcArmState = ArmState.MOVINGDOWN;
        }
        if (gamepad1.left_bumper) {
            ftcArmState = ArmState.TOPOSITIONLOW;
        }
        if (gamepad1.right_bumper) {
            ftcArmState = ArmState.TOPOSITIONHIGH;
        }
        return ftcArmState;
    }
}