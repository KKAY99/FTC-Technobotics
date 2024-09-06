package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.Constants;
@TeleOp
public class MecanumTeleOp extends LinearOpMode {
    private void autoDrive(double xSpeed,double ySpeed,int rotation,double distance){

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
        double denominator,frontLeftPower,backLeftPower,frontRightPower,backRightPower;
        int distancetraveled=frontLeftMotor.getCurrentPosition();
        while(distancetraveled <= distance){
            telemetry.addData("Encoder ",frontLeftMotor.getCurrentPosition());

            distancetraveled=Math.abs(frontLeftMotor.getCurrentPosition());
            y = 0.6; // Remember, Y stick value is reversed
            x = 0; // Counteract imperfect strafing
            rx = 0;
            denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            frontLeftPower = (y + x + rx) / denominator;
            backLeftPower = (y - x + rx) / denominator;
            frontRightPower = (y - x - rx) / denominator;
            backRightPower = (y + x - rx) / denominator;
            frontLeftMotor.setPower(0-frontLeftPower);
            backLeftMotor.setPower(0-backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
        }
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);

    }
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        DcMotor armMotor = hardwareMap.dcMotor.get("armMotor");
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        CRServo clawWrist=hardwareMap.crservo.get("wristServo");
        CRServo clawRight=hardwareMap.crservo.get("rightServo");
        clawRight.setDirection(DcMotorSimple.Direction.REVERSE);
        CRServo clawLeft=hardwareMap.crservo.get("leftServo");
        clawLeft.setDirection(DcMotorSimple.Direction.FORWARD);
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
            double armtrigger=gamepad1.left_trigger;
            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double maxPower = 1;
            if (gamepad1.left_trigger == 0) {
                maxPower = Constants.MotorConstants.driveSpeed;
            } else {
                double triggerValue = (gamepad1.left_trigger);
                maxPower = Constants.MotorConstants.driveSpeed +
                        ((1 - Constants.MotorConstants.driveSpeed) * triggerValue);
            }
            //limit speed to MaxPower
            y=y*maxPower;
            x=x*maxPower;
            rx=rx*maxPower;

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
            if (gamepad1.share) {
                autoDrive(0.0, 0.6, 352, 352);
                //autoDrive(0.6,0,352,352*6);
                //autoDrive(0.0,-0.6,352,352*3);
                //autoDrive(-0.6,0,352,352*6);
            }
            if (gamepad1.dpad_up){

                armMotor.setPower(-Constants.MotorConstants.armSpeed);
            }
            else{
                if(gamepad1.dpad_down){
                    if (armtrigger>0){
                        armMotor.setPower(Constants.MotorConstants.armSpeed*2);
                    }else{
                        armMotor.setPower(Constants.MotorConstants.armSpeed);
                    }
            } else {
                    armMotor.setPower(0);
                }
            }
            if (gamepad1.a) {
                clawLeft.setPower(Constants.MotorConstants.intakeSpeed);
                clawRight.setPower(Constants.MotorConstants.intakeSpeed);
            } else {
                clawLeft.setPower(0);
                clawRight.setPower(0);
            }
            if (gamepad1.y) {
                clawLeft.setPower(-Constants.MotorConstants.intakeSpeed);
                clawRight.setPower(-Constants.MotorConstants.intakeSpeed);
            } else {
                clawLeft.setPower(0);
                clawRight.setPower(0);
            }
            if (gamepad1.x) {
                clawWrist.setPower(Constants.MotorConstants.wristSpeed);

            } else {

                if (gamepad1.b) {
                    clawWrist.setPower(-Constants.MotorConstants.wristSpeed);

                } else {
                    clawWrist.setPower(0);
                }
            }
        }
    }
}