package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import org.firstinspires.ftc.teamcode.Constants;
@TeleOp
public class TestOpClass extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        DcMotor armMotor = hardwareMap.dcMotor.get("armMotor");
        DcMotor udarmMotor = hardwareMap.dcMotor.get("udarmMotor");

        Servo clawServo = hardwareMap.servo.get("clawServo");
        CRServo wristServo = hardwareMap.crservo.get("wristServo");

        boolean servoToggle = true;
        double servoStartPosition = clawServo.getPosition();
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double trigger = gamepad1.right_trigger;
            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double maxPower = Constants.MotorConstants.driveSpeed;
            if (gamepad1.left_trigger !=0) {
                double triggerValue = (gamepad1.left_trigger);
                maxPower = Constants.MotorConstants.driveSpeed +
                        ((1 - Constants.MotorConstants.driveSpeed) * triggerValue);
            }

            if (gamepad1.right_trigger != 0) {
                double triggerValue = (gamepad1.right_trigger);
                maxPower = Constants.MotorConstants.driveSpeed-Constants.MotorConstants.driveSpeed * triggerValue;
                if (maxPower < 0.1) {
                    maxPower = 0.1;
                }
            }

            if (gamepad2.dpad_up) {
                armMotor.setPower(0.6);
            }
            else if (gamepad2.dpad_down) {
                armMotor.setPower(-0.6);
            }
            else {
                armMotor.setPower(0);
            }

            if (gamepad2.left_bumper) {
                udarmMotor.setPower(0.6);
            }
            else if (gamepad2.right_bumper) {
                udarmMotor.setPower(-0.6);
            }
            else {
                udarmMotor.setPower(0);
            }
            if (gamepad2.x){
                    clawServo.setPosition(servoStartPosition + Constants.ServoConstants.servoOpenDegree);
                }
            if (gamepad2.triangle) {
                  clawServo.setPosition(servoStartPosition);
                }
            if (gamepad2.left_trigger > 0.2) {
                wristServo.setPower(-0.5);
            } else {
                wristServo.setPower((0));
            }
            if (gamepad2.right_trigger > 0.2){
                wristServo.setPower(0.5 );
            }else {
                wristServo.setPower(0);
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

            

        }
    }
}
