package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous
public class AutoMoveToSampleScore extends LinearOpMode {
    private void autoDrive(double xSpeed,double ySpeed,int rotation,double distanceIn) {
        int kticksPerIn=11;


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
        double distance=distanceIn*kticksPerIn;
        double denominator, frontLeftPower, backLeftPower, frontRightPower, backRightPower;
        int distancetraveled = frontLeftMotor.getCurrentPosition();
        while (distancetraveled <= distance) {
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
        waitForStart();

        if (isStopRequested()) return;
        boolean autohasrun=false;
        while (opModeIsActive()) {
            if (autohasrun==false) {
                //liftarm(1000);
                autoDrive(-j                                                                                                                                                                        .6, 0, 0, 20);
                //autoDrive(.6, 0, 1, 120);
                //autoDrive(.6, 0, 0, 20);

                long timetoopen = 1000;
                // openclaw(timetoopen);
                autohasrun=true;
            }
        }
    }
}