package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp
public class xTestMotors extends LinearOpMode {

    private void homeArm(DcMotor armMotor,TouchSensor armSensor){
        int startArmPos= armMotor.getCurrentPosition(); // get current position
        int curentArmPos=startArmPos;
        int targetArmPos;
        telemetry.addData("Arm Homing:","started");
        telemetry.update();
        //move arm down no more than HomeLimit ticks to try to get in the right position
        // while arm sensor is false and distance between positions < HomeLlimitTicks move arm
        while (armSensor.isPressed()==false && ((startArmPos-curentArmPos)<Constants.homeLimitTicks)){
            targetArmPos=curentArmPos-10;
            armMotor.setTargetPosition(targetArmPos);
            curentArmPos=armMotor.getCurrentPosition();
        }
        /// if movement is stopped than stop a
        targetArmPos=armMotor.getCurrentPosition();
        armMotor.setTargetPosition(targetArmPos);
        telemetry.addData("Arm Homing:","completed");
        telemetry.update();
    }
    @Override
    public void runOpMode() throws InterruptedException {
        RevBlinkinLedDriver blinkinLedDriver;
        RevBlinkinLedDriver.BlinkinPattern pattern;
        blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP2_LARSON_SCANNER);
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
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        int armMotorPosition=0;
        int lastArmMotorPosition=0;
        boolean armHasHomed=false;
        boolean armAutoMove=false;
        DcMotor armMotor=hardwareMap.dcMotor.get("armMotor");
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
// Reset the motor encoder so that it reads zero ticks
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setTargetPosition(armMotorPosition);
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armMotor.setPower(Constants.MotorConstants.armAutoSpeed);

        DcMotor viperMotor=hardwareMap.dcMotor.get("viperMotor");
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        viperMotor.setTargetPosition(0);  // set start position to 0
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(Constants.MotorConstants.armAutoSpeed);

        Servo bucketServo= hardwareMap.servo.get("bucketServo");

        CRServo intakeServo= hardwareMap.crservo.get("intakeServo");
        intakeServo.setDirection(DcMotorSimple.Direction.FORWARD);
        TouchSensor magArmSensor=hardwareMap.get(TouchSensor.class, "magArmSensor");
        CRServo wristServo= hardwareMap.crservo.get("wristServo");
        waitForStart();

        if (isStopRequested()) return;
       // if (armHasHomed==false){
         //   homeArm(armMotor,magArmSensor);
           // armHasHomed=true;
        //}
        while (opModeIsActive()) {

            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double trigger = gamepad1.right_trigger;
            boolean runmotors=false;
            if(gamepad1.a) {
                frontLeftMotor.setPower(0.4);
                runmotors=true;
            }
            if(gamepad1.b) {
                backLeftMotor.setPower(0.4);
                runmotors=true;
            }
            if(gamepad1.x) {
                frontRightMotor.setPower(0.4);
                runmotors=true;
            }
            if(gamepad1.y) {
                backRightMotor.setPower(0.4);
                runmotors=true;
            }
            if (!runmotors){
                frontRightMotor.setPower(0);
                frontLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                backLeftMotor.setPower(0);
            }
        }
    }
}
