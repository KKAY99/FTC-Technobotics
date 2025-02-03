package org.firstinspires.ftc.teamcode;

import com.google.blocks.ftcrobotcontroller.hardware.HardwareItemMap;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp
public class TestOpClass extends LinearOpMode {

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
            double viperMotorSpeed=0;
            double intakeMotorSpeed=0;
            double wristMotorSpeed=0;
            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,6
            // but only if at least one is out of the range [-1, 1]
            double maxPower = 1;
            if (gamepad1.left_trigger == 0) {
                maxPower = Constants.MotorConstants.driveSpeed;
            } else {
                double triggerValue = (gamepad1.left_trigger);
                maxPower = Constants.MotorConstants.driveSpeed -
                        ((Constants.MotorConstants.driveSpeed) * triggerValue);
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
            boolean armMoveManual = false;

            frontLeftMotor.setPower(0 - frontLeftPower);
            backLeftMotor.setPower(0 - backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
            viperMotorSpeed=0;
            if (magArmSensor.isPressed()) {
                telemetry.addData("magSensor","true");
            } else { // Otherwise, run the motor
                telemetry.addData("magSensor", "false");
            }

            if(gamepad1.left_bumper){
                telemetry.addData("Turn Motor Off","false");
                viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
               viperMotorSpeed=Constants.MotorConstants.viperMoveDownSpeed;
                viperMotor.setTargetPosition(Constants.MotorConstants.viperBottomPosition);
                viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                viperMotor.setPower(viperMotorSpeed);
            }

            if(gamepad1.right_bumper){
                telemetry.addData("Turn Motor Off","false");
                viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                viperMotorSpeed=-Constants.MotorConstants.viperMoveUpSpeed;
                viperMotor.setTargetPosition(Constants.MotorConstants.viperTopPosition);
                viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                viperMotor.setPower(viperMotorSpeed);
            }else{
                if(viperMotor.getCurrentPosition()>=Constants.MotorConstants.viperBottomPosition) {
                    telemetry.addData("Turn Motor Off", "true");
                    viperMotor.setPower(0);
                    viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    viperMotor.setPower(0);          
                }
            }

            if(gamepad1.a){
                bucketServo.setPosition(Constants.MotorConstants.bucketDumpPosition);
            }
            if(gamepad1.b){
                bucketServo.setPosition(Constants.MotorConstants.bucketFlatPosition);
            }
            if(gamepad1.y){
                bucketServo.setPosition(Constants.MotorConstants.bucketLoadPosition);
            }



            if(gamepad2.dpad_up){
                armMoveManual=true;
                armMotorPosition=armMotorPosition+Constants.MotorConstants.armMovementSpeed;
            }
            if(gamepad2.dpad_down){
                armMoveManual=true;
                armMotorPosition=armMotorPosition-Constants.MotorConstants.armMovementSpeed;
            }
            //armMotor.setPower(armMotorSpeed);

            if(gamepad2.x){
                intakeMotorSpeed=Constants.MotorConstants.intakeMoveSpeed;
            }
            if(gamepad2.b){
                intakeMotorSpeed=Constants.MotorConstants.intakeMoveUpSpeed;
            }
            intakeServo.setPower(intakeMotorSpeed);
            if(gamepad2.y){
                wristMotorSpeed=Constants.MotorConstants.wristMoveUpSpeed;
            }
            if(gamepad2.a){
                wristMotorSpeed=Constants.MotorConstants.wristMoveDownSpeed;
            }
            wristServo.setPower(wristMotorSpeed);
            /*
            // If the right bumper is pressed, lower the arm
            if (gamepad2.right_bumper) {
                armMotorPosition=Constants.MotorConstants.armPositionDown;
                armAutoMove=true;
            }

            // if the left bumber is pressed, raises arm

            if (gamepad2.left_bumper){
                armMotorPosition=Constants.MotorConstants.armPositionUp;
                armAutoMove=true;
            }
            */
            //if new position is more than threshold reset the target
            if(armMoveManual) {
                armAutoMove=false;
                if (((armMotorPosition - lastArmMotorPosition) > Constants.MotorConstants.armMovementThreshold) ||
                        ((armMotorPosition - lastArmMotorPosition) < -Constants.MotorConstants.armMovementThreshold)) {
                    armMotor.setTargetPosition(armMotorPosition);
                    lastArmMotorPosition = armMotorPosition;
                }
            } else {
                if (armAutoMove){
                    armMotor.setTargetPosition(armMotorPosition);
                }else {
                    /// if movement is stopped than stop arm
                    armMotorPosition = armMotor.getCurrentPosition();
                    armMotor.setTargetPosition(armMotorPosition);
                }
            }
            telemetry.addData("Viper Position",viperMotor.getCurrentPosition());
            double bucketPos=bucketServo.getPosition();
            String bucketPosition=String.format("%.2f",bucketPos);
            telemetry.addData("Bucket Position",bucketPosition);
            telemetry.addData("Arm Position",armMotor.getCurrentPosition());
            telemetry.addData("Arm Target",armMotorPosition);
            //telemetry.addData("Elbow Position",wristServo.)
            telemetry.update();
            /*if(bMoveUpMode){
                int currentPosition=armMotor.getCurrentPosition();
                int distance=Constants.MotorConstants.armPositionUp-currentPosition;
                if(currentPosition==Constants.MotorConstants){
                    armMotcor.setPower(0);
                    bMoveUpMode=false;
                }
                else {

                }
            }*/
        }
    }
}
