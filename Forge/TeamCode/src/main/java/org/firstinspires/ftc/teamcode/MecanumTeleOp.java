package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.FTCArm;
import org.firstinspires.ftc.teamcode.FTCarmclaw;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.Constants;
@TeleOp
public class MecanumTeleOp extends LinearOpMode {
    /*DcMotor armRotationMotor = hardwareMap.dcMotor.get("armRotationMotor");
    Servo rightclawservo = hardwareMap.servo.get("rightClawServo");
    Servo leftclawservo = hardwareMap.servo.get("leftClawServo");
    FTCArm ftcArm= new FTCArm(armRotationMotor);
    FTCarmclaw ftcarmclaw= new FTCarmclaw(rightclawservo,leftclawservo);*/
    enum ArmStates {
        NOTMOVING,
        MOVINGUP,
        MOVINGDOWN,
        TOPOSITIONLOW,
        TOPOSITIONHIGH
    }
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
        DcMotor ftcArm = hardwareMap.dcMotor.get("armMotor");
        ftcArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Servo clawDrop=hardwareMap.servo.get("servo");
        double servoPosition=Constants.MotorConstants.servo_start_position;
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double trigger = gamepad1.right_trigger;

            ArmStates ftcArmStates;
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

            ftcArmStates=ArmStates.NOTMOVING;
            if (gamepad1.dpad_up) {
                ftcArmStates=ArmStates.MOVINGUP;
            }
            if (gamepad1.dpad_down) {
                ftcArmStates=ArmStates.MOVINGDOWN;
            }
            if (gamepad1.left_bumper) {
                ftcArmStates=ArmStates.TOPOSITIONLOW;
            }
            if (gamepad1.right_bumper) {
                ftcArmStates=ArmStates.TOPOSITIONHIGH;
            }
            switch(ftcArmStates){
                case NOTMOVING:
                    ftcArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    ftcArm.setPower(0);
                    break;
                case MOVINGDOWN:
                    ftcArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    ftcArm.setPower(-Constants.MotorConstants.armSpeed);
                    break;
                case MOVINGUP:
                    ftcArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    ftcArm.setPower(Constants.MotorConstants.armSpeed);
                    break;

                case TOPOSITIONLOW:
                    ftcArm.setPower(Constants.MotorConstants.armSpeed);
                    ftcArm.setTargetPosition(Constants.MotorConstants.pickupposition);
                    ftcArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    break;
                case TOPOSITIONHIGH:
                    ftcArm.setPower(Constants.MotorConstants.armSpeed);
                    ftcArm.setTargetPosition(Constants.MotorConstants.scoreposition);
                    ftcArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    break;
            }
           /*
            if(gamepad1.y){
                //clawDrop.setDirection(DcMotorSimple.Direction.FORWARD);
                clawDrop.setPosition(0);
            } else {
                if (gamepad1.x) {
                    // clawDrop.setDirection(DcMotorSimple.Direction.REVERSE);
                    clawDrop.setPosition(.25);
                } else {
                    clawDrop.setPosition(0);
                }
            }*/

            if (gamepad1.y && servoPosition > Constants.MotorConstants.servo_start_position ) {
                //clawDrop.setDirection(DcMotorSimple.Direction.FORWARD);
                servoPosition -= 0.01;
            } else if (gamepad1.x && servoPosition < Constants.MotorConstants.servo_end_position) {
                // clawDrop.setDirection(DcMotorSimple.Direction.REVERSE);
               servoPosition += 0.01;
            }

            clawDrop.setPosition(servoPosition);
        }
    }
}