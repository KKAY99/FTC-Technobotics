package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
@Autonomous
public class Atonmous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        DcMotor armMotor = hardwareMap.dcMotor.get("armMotor");
        DcMotor udarmMotor = hardwareMap.dcMotor.get("udarmMotor");

        Servo clawServo = hardwareMap.servo.get("clawServo");
        CRServo wristServo = hardwareMap.crservo.get("wristServo");

        waitForStart();
        ElapsedTime timer=new ElapsedTime();
        timer.reset();
        boolean stop = true;

        double y=1;
        double x=0;
        double rx=0;
        double maxPower = Constants.MotorConstants.driveSpeed;

        if (isStopRequested()) return;

        while (opModeIsActive()) {
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

            while (timer.seconds() < 3){
                frontLeftMotor.setPower(-0.6);
                frontRightMotor.setPower(0.6);
                backLeftMotor.setPower(-0.6);
                backRightMotor.setPower(0.6);
                stop();

            }
            while (timer.seconds() > 3 && timer.seconds()< 4.7){
                frontLeftMotor.setPower(-0.6);
                frontRightMotor.setPower(-0.6);
                backLeftMotor.setPower(0.6);
                backRightMotor.setPower(0.6);
                stop();
            }
        }

    }
}
