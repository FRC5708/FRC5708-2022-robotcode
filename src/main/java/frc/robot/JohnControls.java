package frc.robot;

import frc.robot.ControlScheme;

public class JohnControls extends ControlScheme {
    @Override
    public double getWinch2Power(){
        double power = Control.getXboxCtrl().getRightTriggerAxis() - Control.getXboxCtrl().getLeftTriggerAxis();
        if(power > 0.05 || power < -0.05){
            return power;
        }
        return 0;
    }
}