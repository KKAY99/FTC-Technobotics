package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous
public class AutoMoveToSampleScore extends LinearOpMode {
    private void autoDrive(double xSpeed,double ySpeed,int rotation,int ticksToTravel) {
      // pass in distance in ticks

        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);        // Reset the motor encoder so that it reads zero ticks

        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Turn the motor back on, required if you use STOP_AND_RESET_ENCODER
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        double x = xSpeed;
        double y = ySpeed;
        double rx = rotation;
        double denominator, frontLeftPower, backLeftPower, frontRightPower, backRightPower;
        int distancetraveled = frontLeftMotor.getCurrentPosition();
        while (distancetraveled <= ticksToTravel) {
            telemetry.addData("Encoder ", frontLeftMotor.getCurrentPosition());
            telemetry.update();
            distancetraveled = Math.abs(frontLeftMotor.getCurrentPosition());
            y = xSpeed; // Remember, Y stick value is reversed
            x = ySpeed; // Counteract imperfect strafing
            rx = rotation;
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
    private void liftViperSlide(DcMotor viperMotor) {

        viperMotor.setPower(-Constants.MotorConstants.viperMoveUpSpeed);
        viperMotor.setTargetPosition(Constants.MotorConstants.viperTopPosition);

        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (viperMotor.getCurrentPosition() > Constants.MotorConstants.viperTopPosition) {
            telemetry.addData("Viper Move Auto", "Waiting " + viperMotor.getCurrentPosition());
            telemetry.update();

        }
        telemetry.addData("Viper Move Auto", "Completed");
        telemetry.update();
    }
    private void downViperSlide(DcMotor viperMotor) {
        viperMotor.setPower(-Constants.MotorConstants.viperMoveDownSpeed);
        viperMotor.setTargetPosition(Constants.MotorConstants.viperBottomPosition);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (viperMotor.getCurrentPosition() < Constants.MotorConstants.viperBottomPosition) {
            telemetry.addData("Viper Move Auto", "Waiting " + viperMotor.getCurrentPosition());
            telemetry.update();

        }
        telemetry.addData("Viper Move Auto", "Completed");
        telemetry.update();
    }
    private void tiltBasketFoward() {
        Servo bucketServo= hardwareMap.servo.get("bucketServo");
        bucketServo.setPosition(Constants.MotorConstants.bucketAutoDropPosition);
        sleep(500);
    }
    private void flatBasket() {
        Servo bucketServo= hardwareMap.servo.get("bucketServo");
        bucketServo.setPosition(Constants.MotorConstants.bucketFlatPosition);
        sleep(500);
    }
    private void pickUpSample(CRServo intakeServo) {

        intakeServo.setDirection(DcMotorSimple.Direction.FORWARD);
        double intakeMotorSpeed = Constants.MotorConstants.intakeMoveSpeed;
        intakeServo.setPower(intakeMotorSpeed);
        sleep(500);
        intakeServo.setPower(0);
    }
    private void releaseSample(CRServo intakeServo) {

        intakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        double intakeMotorSpeed = Constants.MotorConstants.intakeMoveSpeed;
        intakeServo.setPower(intakeMotorSpeed);
        sleep(500);
        intakeServo.setPower(0);
    }
    private void moveWristUp() {
        CRServo wristServo= hardwareMap.crservo.get("wristServo");
        double wristMotorSpeed =Constants.MotorConstants.wristMoveUpSpeed;
        wristServo.setPower(wristMotorSpeed);
        sleep(500);
        wristServo.setPower(0);
    }

    private void moveWristDown() {
        CRServo wristServo= hardwareMap.crservo.get("wristServo");
        double wristMotorSpeed =Constants.MotorConstants.wristMoveDownSpeed;
        wristServo.setPower(wristMotorSpeed);
        sleep(500);
        wristServo.setPower(0);
    }
    private void autoStep(int delay,DcMotor armMotor,CRServo intakeServo, Servo bucketServo){
        double bucketPos=bucketServo.getPosition();
        String bucketPosition=String.format("%.2f",bucketPos);
        telemetry.addData("Bucket Position",bucketPosition);
      //  telemetry.addData("Intake Position",intakeServo.getP);
        telemetry.addData("Arm Position",armMotor.getCurrentPosition());
        //telemetry.addData("Elbow Position",wristServo.)
        telemetry.update();
        if(delay>0) {
            sleep(delay);
        }

    }

/* FROM 2023 Robot
    private void liftarm(long timetoopen){
        DcMotor armMotor = hardwareMap.dcMotor.get("armMotor");
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armMotor.setPower(-Constants.MotorConstants.armSpeed);
        sleep(timetoopen);
        armMotor.setPower(0);
    }


 */

    @Override
    public void runOpMode() throws InterruptedException {
        CRServo intakeServo= hardwareMap.crservo.get("intakeServo");
        Servo bucketServo= hardwareMap.servo.get("bucketServo");
        DcMotor armMotor=hardwareMap.dcMotor.get("armMotor");
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Reset the motor encoder so that it reads zero ticks
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DcMotor viperMotor=hardwareMap.dcMotor.get("viperMotor");
        viperMotor.setTargetPosition(0);  // set start position to 0
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        waitForStart();

        if (isStopRequested()) return;
        boolean autohasrun=false;
        while (opModeIsActive()) {
            if (autohasrun==false) {
                //liftarm(1000);
                autoStep(0,armMotor,intakeServo,bucketServo);
                autoDrive(0.6, 0, 0, 75);
                autoStep(50,armMotor,intakeServo,bucketServo);
                autoDrive(0, 0, 1, 250);
                autoStep(50,armMotor,intakeServo,bucketServo);

                autoDrive(-0.6, 0, 0, 287);
                autoStep(50,armMotor,intakeServo,bucketServo);

                liftViperSlide(viperMotor);
                autoStep(2000,armMotor,intakeServo,bucketServo);


                tiltBasketFoward();
                autoStep(20000,armMotor,intakeServo,bucketServo);

                autoDrive(0, 0, -1, 50);
                autoStep(2000,armMotor,intakeServo,bucketServo);

                autoDrive(-0.6, 0, 0, 575);
                autoStep(2000,armMotor,intakeServo,bucketServo);

                pickUpSample(intakeServo);
                autoStep(2000,armMotor,intakeServo,bucketServo);
                downViperSlide(viperMotor);
                autoStep(2000,armMotor,intakeServo,bucketServo);
                flatBasket();
                autoStep(2000,armMotor,intakeServo,bucketServo);
                moveWristUp();
                autoStep(2000,armMotor,intakeServo,bucketServo);
                releaseSample(intakeServo);
                autoStep(2000,armMotor,intakeServo,bucketServo);
                moveWristDown();
                autoStep(2000,armMotor,intakeServo,bucketServo);
                liftViperSlide(viperMotor);
                autoStep(2000,armMotor,intakeServo,bucketServo);
                autoDrive(-0.6, 0, 0, 575);
                autoStep(2000,armMotor,intakeServo,bucketServo);
                autoDrive(0, 0, -1, 25);
                autoStep(2000,armMotor,intakeServo,bucketServo);
                tiltBasketFoward();
                autoDrive(0, 0, -1, 26);
                autoStep(2000,armMotor,intakeServo,bucketServo);
                autoDrive(0.6, 0, 0, 2962);

                long timetoopen = 1000;
                // openclaw(timetoopen);
                autohasrun=true;
            }
        }
    }
}