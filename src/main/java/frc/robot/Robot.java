// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// https://docs.ctre-phoenix.com/en/stable/ch13_MC.html

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.DriveWithJoystick;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Sensors;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public static Robot instance;
    
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private static Drivetrain driveTrain = new Drivetrain();
  private static Climber climber = new Climber();
  private static Sensors sensors = new Sensors();

  private DriveWithJoystick.DoDrivetrain doDrivetrain;
  private DriveWithJoystick.DoClimber doClimber;

  private m_autoStates m_autoState = m_autoStates.startup;
  private long m_autoStartedTime = 0;
  private enum m_autoStates {startup, reverse, done};

  private enum m_controlModeValues {drive, climb, park}
  private m_controlModeValues m_controlMode = m_controlModeValues.drive;
  private boolean m_xButtonState = false; 

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    instance = this;

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    System.out.println("Hello Robot");

    CommandScheduler.getInstance().onCommandInterrupt(
      command -> System.out.println("Command " + command.getName() + " cancelled."));
    CommandScheduler.getInstance().onCommandInitialize(
      command -> System.out.println("Command " + command.getName() + " initialized."));
    CommandScheduler.getInstance().onCommandFinish(
      command -> System.out.println("Command " + command.getName() + " finished."));
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    //driveTrain.DrivePolar(-0.3, 0);
  
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    //driveTrain.SetMotors(0.7, 0.7); 
    //System.out.println(System.currentTimeMillis());
    System.out.println("Startup");
    switch (m_autoState){
      case startup:
        System.out.println("Starting robot...");
        m_autoState = m_autoStates.reverse;
        m_autoStartedTime = System.currentTimeMillis();
        driveTrain.Drive(-0.1, -0.1);
        //driveTrain.DrivePolar(-0.3, 0);
        break; 

      case reverse:
        long deltaTime = System.currentTimeMillis() - m_autoStartedTime;
        //System.out.println(m_autoStartedTime);
        if ( deltaTime >= 4000){
          m_autoState = m_autoStates.done; 
          System.out.println("Robot stopped. ");
          driveTrain.SetMotors(0, 0);
        }
        
        break;

      case done:
        //System.out.println("All done");
        break; 
        



    }
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    

    } 
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    doDrivetrain = new DriveWithJoystick.DoDrivetrain(driveTrain);
    doClimber = new DriveWithJoystick.DoClimber(climber);
  }
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if(m_xButtonState!=Control.getXboxCtrl().getXButton()){
      m_xButtonState = Control.getXboxCtrl().getXButton();
      if(m_xButtonState){
        if(m_controlMode == m_controlModeValues.drive){
          m_controlMode = m_controlModeValues.climb;
        }
        else if (m_controlMode == m_controlModeValues.climb){
          m_controlMode = m_controlModeValues.drive;
        }
      System.out.println(m_controlMode);
      }
    }
    if (m_controlMode==m_controlModeValues.drive){
      doDrivetrain.execute();
    }
    else if ( m_controlMode==m_controlModeValues.climb){
      doClimber.execute();
    }
    else if (m_controlMode == m_controlModeValues.park){

    }
    
  }


  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
    
  }
  
  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {

  }

}
