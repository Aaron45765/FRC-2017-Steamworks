
package org.usfirst.frc.team3863.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3863.robot.commands.*;
import org.usfirst.frc.team3863.robot.subsystems.DriveTrain;
import org.usfirst.frc.team3863.robot.subsystems.*;
import org.usfirst.frc.team3863.robot.commands.AutoTransmission;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    int lastPOV = 0;
    String lastAutonSelect;
    
    boolean lastIncShoot = false;
    boolean lastIncFly = false;
    boolean lastIntakeToggle = false;
	//public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;

	Command autonomousCommand;
	Command autoTransCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();
	SendableChooser<Command> autonChooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		System.out.println("if it doesnt work, just add foam!"  );
		BaseCommand.init();
		oi = new OI();
		//autoTransCommand = new AutoTransmission();
		
		autonChooser.addDefault("No Autonomous", null);
		autonChooser.addObject("Compressor Only", null);
		autonChooser.addObject("Drive Forward 3s", new driveForwardAuto());
		autonChooser.addObject("Gear Auto", new gearAuto());
		SmartDashboard.putData("auton_select", autonChooser);
		CameraServers.initCamServers();
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}
	@Override
	public void disabledPeriodic() {
		autonomousCommand = autonChooser.getSelected();
		if (autonomousCommand != null){
			if (!(autonomousCommand.getName() == lastAutonSelect)){
				System.out.println("Selected Auton: "+autonomousCommand.getName());
				lastAutonSelect = autonomousCommand.getName();
			}
		}
		
		
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		Command autonomousCommand = autonChooser.getSelected();
		
		 
		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			System.out.println("Running Auton: "+autonomousCommand.getName());
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		
		if (autoTransCommand != null)
			autoTransCommand.start();
		
		Thread driverThread = new Thread(() -> {
			while (!Thread.interrupted()) {
				double leftSpeed, rightSpeed;
		    	if (!oi.arcadeDSstick.getRawButton(RobotMap.drive_tankToArcadeButton)){
		    		leftSpeed = oi.leftDSstick.getRawAxis(RobotMap.drive_tankLeftForwardAxis);
		        	rightSpeed = oi.rightDSstick.getRawAxis(RobotMap.drive_tankRightForwardAxis);
		    	}else{
		    		double Forward = oi.arcadeDSstick.getRawAxis(RobotMap.drive_arcadeForwardAxis);
		        	double Twist = oi.arcadeDSstick.getRawAxis(RobotMap.drive_arcadeRotateAxis);	
		        	leftSpeed = Forward-Twist;
		        	rightSpeed = Forward+Twist;
		    	}
		    	DriveTrain.setPower(-leftSpeed, -rightSpeed);
			}
		});
		driverThread.setDaemon(true);
		driverThread.start();
		
		DriveTrain.enable();
		Command dis = new disableMode();
		dis.start();
		Intake.startIntake();
		//ShooterMechanism.extendShroud();
		//ShooterMechanism.enableIntakeMode();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {

		ShooterMechanism.debugShroud();
		Command shroudCommand = null;
		if (oi.partnerDSstick.getPOV() == 0 && lastPOV == -1){
			shroudCommand = new enableShooterMode();
		}
		else if (oi.partnerDSstick.getPOV() == 270 && lastPOV == -1){
			shroudCommand = new enableIntakeMode();
		}
		else if (oi.partnerDSstick.getPOV() == 90 && lastPOV == -1){
			shroudCommand = new enableLowShooterMode();
        }
		else if (oi.partnerDSstick.getPOV() == 180 && lastPOV == -1){
			shroudCommand = new disableMode();
        }
		if (shroudCommand != null)
			shroudCommand.start();
		lastPOV = oi.partnerDSstick.getPOV();
		
		if (oi.partnerDSstick.getRawButton(7) && oi.partnerDSstick.getRawButton(8) && !lastIntakeToggle){
			Command curCMD = new toggleShooterBelt();
			curCMD.start();
		}
		lastIntakeToggle = (oi.partnerDSstick.getRawButton(7) && oi.partnerDSstick.getRawButton(8));
		
		Command posCommand = null;
		if (oi.partnerDSstick.getRawButton(6) && !lastIncShoot){
			posCommand = new incrementShroud();
		}
		else if (oi.partnerDSstick.getRawAxis(3) >= 0.98 && !lastIncShoot){
			posCommand = new decrementShroud();
		}
		if (posCommand != null)
			posCommand.start();
		lastIncShoot = (oi.partnerDSstick.getRawButton(6) | oi.partnerDSstick.getRawAxis(3) >= 0.98);
		
		Command flyCommand = null;
		if (oi.partnerDSstick.getRawButton(5) && !lastIncFly){
			flyCommand = new incrementFlywheelSpeed();
		}
		else if (oi.partnerDSstick.getRawAxis(2) >= 0.98 && !lastIncFly){
			flyCommand = new decrementFlywheelSpeed();
		}
		if (flyCommand != null)
			flyCommand.start();
		lastIncFly = (oi.partnerDSstick.getRawButton(5) | oi.partnerDSstick.getRawAxis(2) >= 0.98);		
				//butGateToggle.whenPressed(new pulseArms());
		if (oi.partnerDSstick.getRawButton(4) | oi.arcadeDSstick.getRawButton(6)){
			Winch.setWinchPower(-1);
			Winch.enableBreak();
		}else{
			Winch.setWinchPower(0);
			//Winch.disableBreak();
		}
		
		if (oi.partnerDSstick.getRawButton(3)){
			Churner.churnForward();
		}else{
			Churner.setPower(0);
			//Winch.disableBreak();
		}
    	
    	SmartDashboard.putData(Scheduler.getInstance());
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
    	
		LiveWindow.run();
		
	}
	
}
