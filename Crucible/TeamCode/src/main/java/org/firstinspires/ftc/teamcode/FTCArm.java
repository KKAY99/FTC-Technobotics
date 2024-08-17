package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class FTCArm{
    DcMotor ArmRotation;
    public FTCArm(DcMotor armMotor) {
        ArmRotation=armMotor;
    }

    public void RunArm(double speed){
        ArmRotation.setPower(speed);

    }
    public void armstop(){
        ArmRotation.setPower(0);
    }
}
