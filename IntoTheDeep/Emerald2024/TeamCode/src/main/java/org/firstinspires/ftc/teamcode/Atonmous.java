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
public class Atonmous extends LinearOpMode {
    enum States{
        MOVEFORWARD,ARMEXTEND,ARMUP,WRISTADJUSTBACK,
        WRISTADJUSTFRONT,ARMEXTENDDOWN,ARMDOWN,MOVEBACK,
        WRISTADJUSTBACK2,ROBOTADJUST,ROBOTADJUST2,TURNRIGHT90,
        TURNRIGHT902,MOVETOSUB,ARMUP2,MOVEFORWARD2,MOVEBACK2,CLAWOPEN,
        ARMDOWN2,MOVESIDEWAYS,END
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

            if (armUpStablize) {
                udarmMotor.setTargetPosition(udarmMaxPos);
                udarmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                udarmMotor.setPower(-0.5);
            }

            switch(currentState){
                case MOVEFORWARD:
                    frontLeftMotor.setPower(-0.6);
                    frontRightMotor.setPower(0.6);
                    backLeftMotor.setPower(-0.6);
                    backRightMotor.setPower(0.6);

                    if(timer.seconds() > 0.8) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.ROBOTADJUST;
                    }
                    break;
                case ROBOTADJUST :
                    frontLeftMotor.setPower(0.6);
                    frontRightMotor.setPower(0.6);
                    backLeftMotor.setPower(0.6);
                    backRightMotor.setPower(0.6);

                    if(timer.seconds() > 0.4) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.MOVESIDEWAYS;
                    }
                    break;
                case MOVESIDEWAYS :
                    frontLeftMotor.setPower(-0.6);
                    frontRightMotor.setPower(-0.6);
                    backLeftMotor.setPower(0.6);
                    backRightMotor.setPower(0.6);

                    if(timer.seconds() > 0.4) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.ARMUP;
                    }
                    break;

                case ARMUP:
                    udarmMotor.setTargetPosition(udarmMaxPos);
                    udarmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    udarmMotor.setPower(-1);
                    armUpStablize = true;

                    currentState = States.WRISTADJUSTBACK;
                    sleep(delay);
                    timer.reset();
                    break;
                case WRISTADJUSTBACK:
                    wristServo.setPower(1);
                    if(timer.seconds() > 2) {
                        wristServo.setPower(0);
                        stop();
                        sleep(delay);
                        timer.reset();
                        currentState = States.ARMEXTEND;
                    }
                    break;
                case ARMEXTEND:
                    //armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION;
                    // armMotor.setTargetPosition();
                    //ADD CONSTANTS POSITION TALK TO KARL ABOUT GETTING POS VALUES
                    if (armLimit.getValue() == 1) {
                        armMotor.setTargetPosition(MaxPos);
                        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        armMotor.setPower(-1);
                    }else {
                        armMotor.setTargetPosition(limitedPos);
                        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        armMotor.setPower(1);
                    }
                    currentState = States.WRISTADJUSTFRONT;
                    sleep(1000);
                    timer.reset();
                    break;
                case WRISTADJUSTFRONT:
                    wristServo.setPower(-0.6);
                    if(timer.seconds() > 1.3) {
                        wristServo.setPower(0);
                        stop();
                        sleep(delay);
                        timer.reset();
                        currentState = States.CLAWOPEN;
                    }
                    break;
                case CLAWOPEN :
                    //wristServo.setPower(0.25);
                    //sleep(60);
                    clawServo.setPower(-0.6);
                    if(timer.seconds() > 0.5) {
                        clawServo.setPower(0);
                        stop();
                        sleep(delay);
                        timer.reset();
                        currentState = States.WRISTADJUSTBACK2;
                    }
                    break;
                case WRISTADJUSTBACK2 :
                    wristServo.setPower(0.4);
                    if(timer.seconds() > 1.5) {
                        wristServo.setPower(0);
                        stop();
                        sleep(delay);
                        timer.reset();
                        currentState = States.ARMEXTENDDOWN;
                    }
                    break;
                case ARMEXTENDDOWN :
                    armMotor.setTargetPosition(limitedPos);
                    armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    armMotor.setPower(1);
                    currentState = States.MOVEBACK;
                    sleep(delay);
                    timer.reset();
                    break;
                case MOVEBACK :
                    frontLeftMotor.setPower(0.6);
                    frontRightMotor.setPower(-0.6);
                    backLeftMotor.setPower(0.6);
                    backRightMotor.setPower(-0.6);

                    if(timer.seconds()>0.35) {
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        stop();
                        sleep(delay);
                        timer.reset();
                        currentState = States.ARMDOWN;
                    }
                    break;
                case ARMDOWN :
                    udarmMotor.setTargetPosition(0);
                    udarmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    udarmMotor.setPower(1);
                    armUpStablize = false;

                    currentState = States.ROBOTADJUST2;
                    sleep(delay);
                    timer.reset();
                    break;
                case ROBOTADJUST2 :
                    frontLeftMotor.setPower(-0.6);
                    frontRightMotor.setPower(-0.6);
                    backLeftMotor.setPower(-0.6);
                    backRightMotor.setPower(-0.6);

                    if(timer.seconds() > 0.2) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.MOVEBACK2;
                    }
                    break;
                case MOVEBACK2 :
                    frontLeftMotor.setPower(0.6);
                    frontRightMotor.setPower(-0.6);
                    backLeftMotor.setPower(0.6);
                    backRightMotor.setPower(-0.6);

                    if(timer.seconds()>0.2) {
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        stop();
                        sleep(delay);
                        timer.reset();
                        currentState = States.TURNRIGHT90;
                    }
                    break;
                case TURNRIGHT90 :
                    frontLeftMotor.setPower(-0.6);
                    frontRightMotor.setPower(-0.6);
                    backLeftMotor.setPower(-0.6);
                    backRightMotor.setPower(-0.6);

                    if(timer.seconds() > 0.64) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.MOVEFORWARD2;
                    }
                    break;
                case MOVEFORWARD2 :
                    frontLeftMotor.setPower(-0.6);
                    frontRightMotor.setPower(0.6);
                    backLeftMotor.setPower(-0.6);
                    backRightMotor.setPower(0.6);

                    if(timer.seconds() > 0.85) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.TURNRIGHT902;
                    }
                    break;
                case TURNRIGHT902 :
                    frontLeftMotor.setPower(-0.6);
                    frontRightMotor.setPower(-0.6);
                    backLeftMotor.setPower(-0.6);
                    backRightMotor.setPower(-0.6);

                    if(timer.seconds() > 0.5) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.ARMUP2;
                    }
                    break;
                case ARMUP2 :
                    udarmMotor.setTargetPosition(1000);
                    udarmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    udarmMotor.setPower(-1);
                    armUpStablize = false;

                    currentState = States.MOVETOSUB;
                    sleep(delay);
                    timer.reset();
                    break;
                case MOVETOSUB :
                    frontLeftMotor.setPower(-0.6);
                    frontRightMotor.setPower(0.6);
                    backLeftMotor.setPower(-0.6);
                    backRightMotor.setPower(0.6);

                    if(timer.seconds() > 0.5) {
                        stop();
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);
                        sleep(delay);
                        timer.reset();
                        currentState = States.ARMDOWN2;
                    }
                case ARMDOWN2 :
                    udarmMotor.setTargetPosition(920);
                    udarmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    udarmMotor.setPower(1);
                    armUpStablize = false;

                    currentState = States.END;
                    sleep(delay);
                    timer.reset();
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
