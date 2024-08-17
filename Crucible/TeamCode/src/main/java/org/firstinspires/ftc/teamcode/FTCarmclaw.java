package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Servo;
public class FTCarmclaw {
    Servo clawmotorright;
    Servo clawmotorleft;

    double rightclawopen;
    double rightclawclose;
    double leftclawopen;
    double leftclawclose;

    boolean toggle=true;
    public FTCarmclaw(Servo Right,Servo Left){
       clawmotorright = Right;
       clawmotorleft = Left;
    }
    public void clawopen(){
        clawmotorright.setPosition(rightclawopen);
        clawmotorleft.setPosition(leftclawopen);
    }
    public void clawclose(){
        clawmotorright.setPosition(rightclawclose);
        clawmotorleft.setPosition(leftclawclose);
    }
    public void toggle(){
        if(toggle){
             clawopen();
             toggle=false;
        }
        else{
            clawclose();
            toggle=true;
        }
    }
}
