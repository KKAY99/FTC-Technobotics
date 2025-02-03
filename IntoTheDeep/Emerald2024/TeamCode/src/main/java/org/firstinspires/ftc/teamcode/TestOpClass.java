package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

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
        DcMotor armMotor = hardwareMap.dcMotor.get("armMotor");
        DcMotor udarmMotor = hardwareMap.dcMotor.get("udarmMotor");

        CRServo clawServo = hardwareMap.crservo.get("clawServo");
        CRServo wristServo = hardwareMap.crservo.get("wristServo");
        TouchSensor armLimit = hardwareMap.touchSensor.get("armLimit");

        ElapsedTime timer = new ElapsedTime();
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        //This statement is to initialize the position counter at zero.  Don't use with current code but this may be handy later. -Coach Matt
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        int udarmPos = udarmMotor.getCurrentPosition();
        int viperslidepos = armMotor.getCurrentPosition();

        int limitedPos = armMotor.getCurrentPosition() + Constants.ViperslideConstants.ViperSlideLimitedPos;
        int MaxPos = armMotor.getCurrentPosition() + Constants.ViperslideConstants.ViperSlideMaxPos;

        boolean buttonpress = false;
        boolean button0 = false;
        boolean buttonpress2 = false;
        boolean setViperPOS = false;

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y;
            double maxPower = Constants.MotorConstants.driveSpeed;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;
            double trigger = gamepad1.right_trigger;// Remember, Y stick value is reversed
            // Counteract imperfect strafing


            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1
            if (gamepad1.left_trigger != 0) {
                double triggerValue = (gamepad1.left_trigger);
                maxPower = Constants.MotorConstants.driveSpeed +
                        ((1 - Constants.MotorConstants.driveSpeed) * triggerValue);
            }

            if (gamepad1.right_trigger != 0) {
                double triggerValue = (gamepad1.right_trigger);
                maxPower = Constants.MotorConstants.driveSpeed - Constants.MotorConstants.driveSpeed * triggerValue;
                if (maxPower < 0.1) {
                    maxPower = 0.1;
                }
            }
  /*
            //if (armLimit.getValue())
            if (armLimit.getValue() == 1 && !buttonpress) {
                buttonpress = true;
            } else if (armLimit.getValue() == 0 && buttonpress && !button0) {
                button0 = true;
            } else if (button0 && armLimit.getValue() == 1) {
                buttonpress2 = true;
            } else if (armLimit.getValue() == 0 && buttonpress && button0 && buttonpress2)
              {
                buttonpress2 = false;
                buttonpress = false;
                button0 = false;
		//Following If statement is to bring the arm in when its being lowered so it doesnt exceed the horizontal limit of 42 inches
		        if (armMotor.getCurrentPosition() < limitedPos)
                   {
			        armMotor.setTargetPosition(limitedPos);
			        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
			        armMotor.setPower(0.6);
		            }
                }
      */
            //if (armLimit.getValue())
            if (armLimit.getValue() == 1)
            {
                buttonpress = true;
                setViperPOS = false;
            } else
            {
                buttonpress = false;
                //Following If statement is to bring the arm in when its being lowered so it doesnt exceed the horizontal limit of 42 inches
                if (armMotor.getCurrentPosition() < limitedPos)
                {
                    if (!setViperPOS) {
                        setViperPOS = true;
                        armMotor.setTargetPosition(limitedPos);
                        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        armMotor.setPower(1);
                    }
                } else
                {
                    if (setViperPOS) {
                        armMotor.setPower(0);
                        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        setViperPOS = false;
                    }
                }
            }



	  //Get current position of the viperslide to be able to fight gravity by holding the slide in the following dpad If logic
           viperslidepos = armMotor.getCurrentPosition();	
	   if (gamepad2.dpad_up && armMotor.getCurrentPosition() >= MaxPos) {
                if (armMotor.getCurrentPosition() >= limitedPos || buttonpress) {
                    armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    armMotor.setPower(-1);
                } else {
                    armMotor.setTargetPosition(viperslidepos);
                    armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
            } else if (gamepad2.dpad_down) {
                armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                armMotor.setPower(1);
            } else {
                     if (!setViperPOS)
                     {
                         armMotor.setTargetPosition(viperslidepos);
                         armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                     }
            }

            if (gamepad2.left_bumper) {
                udarmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                udarmMotor.setPower(0.5);
                udarmPos = udarmMotor.getCurrentPosition();
            } else if (gamepad2.right_bumper) {
                udarmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                udarmMotor.setPower(-0.5);
                udarmPos = udarmMotor.getCurrentPosition();
            } else {
                udarmMotor.setTargetPosition(udarmPos);
                udarmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }

            double clawSpeed=0;
            if (gamepad2.x) {
                clawSpeed = 0.5;
            }
            if (gamepad2.triangle) {
                clawSpeed = -0.5;
            }
            clawServo.setPower(clawSpeed);

            double wristSpeed=0;
            if (gamepad2.left_trigger >= 0.2) {
                wristSpeed = -0.3;
            }
            if (gamepad2.right_trigger >= 0.2) {
                wristSpeed = 0.3;
            }
            wristServo.setPower(wristSpeed);


            //limit speed to MaxPower
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


            telemetry.addData("Viper Slide Postion", armMotor.getCurrentPosition());
            telemetry.addData("armLimit", armLimit.getValue());
            telemetry.addData("buttonpress", buttonpress);
            telemetry.addData("limitePos", limitedPos);
            telemetry.addData("MaxPos", MaxPos);
            telemetry.addData("setViperPOS", setViperPOS);
            telemetry.update();

        }
    }
}
