package org.usfirst.frc.team3863.robot;


/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	//================ROBOT I/O MAPPINGS===============//
	//Joystick IDs
	public static int leftDSJoystick = 0;
	public static int rightDSJoystick = 1;
	public static int partnerDSJoystick = 2;
	
	//CAN bus IDs
	public static int rightADriveTalonID = 2;
	public static int rightBDriveTalonID = 12;
	public static int leftADriveTalonID = 3;
	public static int leftBDriveTalonID = 15;
	public static int PCM_ID = 0;
	public static int PDB_ID = 0;
	public static int winchATalonID = 13;
	public static int winchBTalonID = 14;
	public static int intakeTalonID = 6;
	public static int flywheelATalonID = 0;
	public static int flywheelBTalonID = 1;
	public static int flywheelBeltTalonID = 7;
	public static int flywheelCoverTalonID = 5;
	public static int churnTalonID = 4;
	
	//PCM I/O pins
	public static int solTransHigh = 0;
	public static int solTransLow = 1;
	public static int solGateOpen = 2;
	public static int solGateClose = 3;

	//FLYWHEEL PID
	public static double flywheelKp = 0.12;
	public static double flywheelKi = 0;
	public static double flywheelKd = 0.5;
	
	//==============CALIBRATION CONSTANTS==============//
	//Drive code calibration constants (DO NOT CHANGE UNLESS YOU KNOW WHAT YOU ARE DOING!)
	public static double MAX_DRIVE_SPEED = 4000;
	
	//=====================SETTINGS====================//
	//Joystick Button Mappings
	public static int drive_tankLeftForwardAxis = 1;
	public static int drive_tankRightForwardAxis = 1;
	public static int drive_tankToArcadeButton = 2;
	public static int drive_arcadeForwardAxis = 1;
	public static int drive_arcadeRotateAxis = 2;
	
	public static int drive_transToggleButton = 1;
	
	//Tunable drive code parameters
	public static double driveShiftLow_HighSpeed = 0.7*MAX_DRIVE_SPEED;
	public static double driveShiftHigh_LowSpeed = 0.3*MAX_DRIVE_SPEED;
	
	public static double LEFT_CORRECTION = 1.00;
	public static double RIGHT_CORRECTION = 1.00;
	
}
