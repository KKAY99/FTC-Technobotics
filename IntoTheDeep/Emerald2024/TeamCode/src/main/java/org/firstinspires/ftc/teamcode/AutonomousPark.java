package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
@Autonomous
public class AutonomousPark extends LinearOpMode {
    enum States{
        MOVEFORWARD,ARMEXTEND,ARMUP,WRISTADJUSTBACK,
        WRISTADJUSTFRONT,ARMEXTENDDOWN,ARMDOWN,MOVEBACK,
        WRISTADJUSTBACK2,ROBOTADJUST,ROBOTADJUST2,TURNRIGHT90,
        TURNRIGHT902,MOVETOSUB,ARMUP2,MOVEFORWARD2,MOVEBACK2,CLAWOPEN,
        ARMDOWN2,END
    }
//2667 is the scoring point

    // 0 is starting postion and set it to the starting position before turning it on
    States currentState=States.MOVEFORWARD;

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        DcMotor armMotor = hardwareMap.dcMotor.get("armMotor");
        DcMotor udarmMotor = hardwareMap.dcMotor.get("udarmMotor");

        CRServo clawServo = hardwareMap.crservo.get("clawServo");
        CRServo wristServo = hardwareMap.crservo.get("wristServo");

        TouchSensor armLimit = hardwareMap.touchSensor.get("armLimit");


        waitForStart();
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        boolean stop = true;
        boolean armUpStablize = false;
        double y=1;
        double x=0;
        double rx=0;
        int udarmMaxPos = 2667;
        double maxPower = Constants.MotorConstants.driveSpeed;
        int delay = 500;

        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        udarmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        int MaxPos = armMotor.getCurrentPosition() + Constants.ViperslideConstants.ViperSlideMaxPos;
        int limitedPos = armMotor.getCurrentPosition() + Constants.ViperslideConstants.ViperSlideLimitedPos;

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


            switch(currentState){
                case MOVEFORWARD:
                    frontLeftMotor.setPower(-0.6);
                    frontRightMotor.setPower(0.6);
                    backLeftMotor.setPower(-0.6);
                    backRightMotor.setPower(0.6);

                    if(timer.seconds() > 0.9) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.END;
                    }
                    break;
                case END:
            }
            telemetry.addData("current State", currentState);
            telemetry.addData("Viper Slide Postion", armMotor.getCurrentPosition());
            telemetry.addData("udarmMotorPOS", udarmMotor.getCurrentPosition());
            telemetry.update();
        }

    }
}
