package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@Autonomous
public class AutoDriveStraightAndDrop extends LinearOpMode {
    private void autoDrive(double xSpeed,double ySpeed,int rotation,double distance) {

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
        double denominator, frontLeftPower, backLeftPower, frontRightPower, backRightPower;
        int distancetraveled = frontLeftMotor.getCurrentPosition();
        while (distancetraveled <= distance) {
            telemetry.addData("Encoder ", frontLeftMotor.getCurrentPosition());

            distancetraveled = Math.abs(frontLeftMotor.getCurrentPosition());
            y = 0.6; // Remember, Y stick value is reversed
            x = 0; // Counteract imperfect strafing
            rx = 0;
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
private void openclaw(long timetoopen){
    CRServo clawRight=hardwareMap.crservo.get("rightServo");
    clawRight.setDirection(DcMotorSimple.Direction.REVERSE);
    CRServo clawLeft=hardwareMap.crservo.get("leftServo");
    clawLeft.setDirection(DcMotorSimple.Direction.FORWARD);
    clawLeft.setPower(Constants.MotorConstants.intakeSpeed);
    clawRight.setPower(Constants.MotorConstants.intakeSpeed);
    sleep(timetoopen);
    clawLeft.setPower(0);
    clawRight.setPower(0);

}
 private void liftarm(long timetoopen){
     DcMotor armMotor = hardwareMap.dcMotor.get("armMotor");
     armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armMotor.setPower(-Constants.MotorConstants.armSpeed);
        sleep(timetoopen);
        armMotor.setPower(0);
    }
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        if (isStopRequested()) return;
        boolean autohasrun=false;
        while (opModeIsActive()) {
            if (autohasrun==false) {
                liftarm(1000);
                autoDrive(.5, 0, 0, 1300);
                long timetoopen = 1000;
               // openclaw(timetoopen);
                autohasrun=true;
            }
        }
    }
}