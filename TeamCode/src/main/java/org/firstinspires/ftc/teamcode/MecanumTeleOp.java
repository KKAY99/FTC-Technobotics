package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.FTCArm;
import org.firstinspires.ftc.teamcode.FTCarmclaw;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp
public class MecanumTeleOp extends LinearOpMode {
    /*DcMotor armRotationMotor = hardwareMap.dcMotor.get("armRotationMotor");
    Servo rightclawservo = hardwareMap.servo.get("rightClawServo");
    Servo leftclawservo = hardwareMap.servo.get("leftClawServo");
    FTCArm ftcArm= new FTCArm(armRotationMotor);
    FTCarmclaw ftcarmclaw= new FTCarmclaw(rightclawservo,leftclawservo);*/
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
        Servo clawDrop=hardwareMap.servo.get("claw");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

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
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            frontLeftMotor.setPower(0-frontLeftPower);
            backLeftMotor.setPower(0-backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
            telemetry.addData("Rotations",frontLeftMotor.getCurrentPosition());
            telemetry.addData("speed",frontLeftMotor.getPower());
            telemetry.addData("Trigger",trigger);
            telemetry.addData("Y value",y);
            if (gamepad1.share){
                autoDrive(0.0,0.6,352,352);
                //autoDrive(0.6,0,352,352*6);
                //autoDrive(0.0,-0.6,352,352*3);
                //autoDrive(-0.6,0,352,352*6);
            }
            if(gamepad1.dpad_up){
                ftcArm.setPower(0.5);
            }
            else{
               ftcArm.setPower(0);
            }
            if(gamepad1.dpad_down){
                ftcArm.setPower(-0.5);
            }
            else{
                ftcArm.setPower(0);
            }
            if(gamepad1.y){
                clawDrop.setPosition(90);
            }
            if(gamepad1.x){
                clawDrop.setPosition(0);
            }
            /*
            if(gamepad1.x){
                ftcarmclaw.toggle();
            }*/
        }
    }
}