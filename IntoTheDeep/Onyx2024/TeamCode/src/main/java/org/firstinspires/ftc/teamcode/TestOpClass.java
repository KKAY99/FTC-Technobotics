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
import com.qualcomm.robotcore.hardware.TouchSensor;

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
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        int armMotorPosition=0;
        int lastArmMotorPosition=0;
        DcMotor armMotor=hardwareMap.dcMotor.get("armMotor");
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
// Reset the motor encoder so that it reads zero ticks
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setTargetPosition(armMotorPosition);
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armMotor.setPower(Constants.MotorConstants.armAutoSpeed);

        DcMotor viperMotor=hardwareMap.dcMotor.get("viperMotor");
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        viperMotor.setTargetPosition(armMotorPosition);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(Constants.MotorConstants.armAutoSpeed);

        CRServo intakeServo= hardwareMap.crservo.get("intakeServo");
        intakeServo.setDirection(DcMotorSimple.Direction.FORWARD);
        TouchSensor magArmSensor=hardwareMap.get(TouchSensor.class, "magArmSensor");
        CRServo wristServo= hardwareMap.crservo.get("wristServo");
        waitForStart();

        if (isStopRequested()) return;

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
                maxPower = Constants.MotorConstants.driveSpeed +
                        ((1 - Constants.MotorConstants.driveSpeed) * triggerValue);
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
               viperMotorSpeed=Constants.MotorConstants.viperMoveSpeed;
            }
            if(gamepad1.right_bumper){
                viperMotorSpeed=-Constants.MotorConstants.viperMoveSpeed;
                viperMotor.setTargetPosition(Constants.MotorConstants.viperTopPosition);
                viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }
            //viperMotor.setPower(viperMotorSpeed);



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
                intakeMotorSpeed=-Constants.MotorConstants.intakeMoveSpeed;
            }
            intakeServo.setPower(intakeMotorSpeed);
            if(gamepad2.y){
                wristMotorSpeed=Constants.MotorConstants.wristMoveUpSpeed;
            }
            if(gamepad2.a){
                wristMotorSpeed=Constants.MotorConstants.wristMoveDownSpeed;
            }
            wristServo.setPower(wristMotorSpeed);
            // If the right bumper is pressed, lower the arm
            if (gamepad2.right_bumper) {
                armMotorPosition=Constants.MotorConstants.armPositionDown;
            }
            // if the left bumber is pressed, raises arm

            if (gamepad2.left_bumper){
                armMotorPosition=Constants.MotorConstants.armPositionUp;
            }
            //if new position is more than threshold reset the target
            if(armMoveManual) {
                if (((armMotorPosition - lastArmMotorPosition) > Constants.MotorConstants.armMovementThreshold) ||
                        ((armMotorPosition - lastArmMotorPosition) < -Constants.MotorConstants.armMovementThreshold)) {
                    armMotor.setTargetPosition(armMotorPosition);
                    lastArmMotorPosition = armMotorPosition;
                }
            }
            telemetry.addData("Viper Position",viperMotor.getCurrentPosition());

            telemetry.addData("Arm Position",armMotor.getCurrentPosition());
            telemetry.addData("Arm Target",armMotorPosition);
            telemetry.update();
            /*if(bMoveUpMode){
                int currentPosition=armMotor.getCurrentPosition();
                int distance=Constants.MotorConstants.armPositionUp-currentPosition;
                if(currentPosition==Constants.MotorConstants){
                    armMotor.setPower(0);
                    bMoveUpMode=false;
                }
                else {

                }
            }*/
        }
    }
}
