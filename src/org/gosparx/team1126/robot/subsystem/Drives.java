package org.gosparx.team1126.robot.subsystem;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

import org.gosparx.team1126.robot.sensors.EncoderData;

/**
 * makes the drives system which makes the robot move
 * @author Michael
 * @author Allison
 * 
 */
public class Drives extends GenericSubsystem{

	/**
	 * controls the right drives motor 
	 */
	private Talon rightDrives;

	/**
	 * controls the left drives motor
	 */
	private Talon leftDrives;

	/**
	 * is used to get the distance that robot has traveled for the left drives
	 */
	private Encoder leftEncoder;

	/**
	 * makes the left encoder data which calculates how far the robot has traveled (inches)
	 */
	private EncoderData leftData;

	/**
	 * is used to get the distance that the robot has traveled for the right drives
	 */
	private Encoder rightEncoder;

	/**
	 * makes the right encoder data which calculates how far the robot has traveled (inches)
	 */
	private EncoderData rightData;

	/**
	 * controls the pnu that changes it from high to low gear and vice versa
	 */
	private Solenoid shifter;

	/**
	 * the average speed of the left and tight motors combined
	 */
	private double currentSpeed;

	/**
	 * the amount of distance the robot goes per tick
	 */
	private static double DISTANCE_PER_TICK = 0.04908739;

	/**
	 * the state that you're currently in
	 */
	private State currentDriveState;

	/**
	 * the required speed in order to shift
	 */
	private final double SHIFTING_SPEED = .7;

	/**
	 * the speed it takes to down shift in inches/second
	 */
	private final int LOWER_SHIFTING_SPEED = 10;

	/**
	 * the speed it takes to up shift in inches/second
	 */
	private final int UPPER_SHIFTING_SPEED = 25;

	/**
	 * the time it should take to shift in seconds
	 */
	private final double SHIFTING_TIME = 0.25;

	/**
	 * whether or not we're in low gear
	 */
	private final boolean LOW_GEAR = false;

	/**
	 * the actual time it takes to shift
	 */
	private double shiftTime;

	/**
	 * an instance of Drives
	 */
	private static Drives drives;

	/**
	 * the wanted power for the left side
	 */
	private static double wantedLeftPower;

	/**
	 * the wanted power for the right side
	 */
	private static double wantedRightPower;

	/**
	 * if drives == null, make a new drives
	 * @return the new drives
	 */
	public static synchronized Drives getInstance(){
		if(drives == null){
			drives = new Drives();
		}
		return drives;
	}

	/**
	 * constructs a Drives object
	 * @param name represents the the name of the subsystem 
	 * @param priority the priority of the subsystem
	 */
	public Drives() {
		super("Drives", Thread.NORM_PRIORITY);
	}

	/**
	 * Initializes all the objects and gives data to the variables
	 * @return true, ends loop 
	 */
	@Override
	protected boolean init() {
		rightDrives = new Talon(5); 
		leftDrives = new Talon(1);
		leftEncoder = new Encoder(2,3);
		leftData = new EncoderData(leftEncoder, DISTANCE_PER_TICK);
		rightEncoder = new Encoder(0,1);
		rightData = new EncoderData(rightEncoder, DISTANCE_PER_TICK);
		shifter = new Solenoid(0);
		currentDriveState = State.LOW_GEAR;
		shiftTime = 0;
		return true;
	}

	/**
	 * allows you to input data during test mode
	 */
	@Override
	protected void liveWindow() {
		// TODO Auto-generated method stub

	}

	/**
	 * executes the code, makes the robot drive
	 * @return false to continue the loop
	 */
	@Override
	protected boolean execute() {
		rightData.calculateSpeed();
		leftData.calculateSpeed();
		currentSpeed = (Math.abs(rightData.getSpeed()) + Math.abs(leftData.getSpeed())) / 2;
		switch(currentDriveState){
		case LOW_GEAR:
			if(Math.abs(currentSpeed) >= UPPER_SHIFTING_SPEED){
				System.out.println("SHIFTING HIGH");
				shifter.set(LOW_GEAR);
				shiftTime = Timer.getFPGATimestamp();
				currentDriveState = State.SHIFTING_HIGH;
			
			if(currentSpeed < 0){
				wantedRightPower = (SHIFTING_SPEED * - 1);
				wantedLeftPower = (SHIFTING_SPEED * - 1);
			}else {
				wantedRightPower = (SHIFTING_SPEED);
				wantedLeftPower = (SHIFTING_SPEED);
			}
			}
			break;

		case HIGH_GEAR:
			if(Math.abs(currentSpeed) <= LOWER_SHIFTING_SPEED){
				System.out.println("SHIFTING LOW");
				shifter.set(!LOW_GEAR);
				shiftTime = Timer.getFPGATimestamp();
				currentDriveState = State.SHIFTING_LOW;
				if(currentSpeed < 0){
					wantedRightPower = (SHIFTING_SPEED * - 1);
					wantedLeftPower = (SHIFTING_SPEED * - 1);
				}else {
					wantedRightPower = (SHIFTING_SPEED);
					wantedLeftPower = (SHIFTING_SPEED);
				}
			}
			break;

		case SHIFTING_LOW:
			if(Timer.getFPGATimestamp() >= shiftTime + SHIFTING_TIME){
				currentDriveState = State.LOW_GEAR;
			}

			break;

		case SHIFTING_HIGH:
			if(Timer.getFPGATimestamp() >= shiftTime + SHIFTING_TIME){
				currentDriveState = State.HIGH_GEAR;
			}

			break;

		}
		leftDrives.set(wantedLeftPower);
		rightDrives.set(wantedRightPower);

		return false;
	}

	/**
	 * makes the class rest for a period of time
	 * @return amount that the class rests for, in milliseconds
	 */
	@Override
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 20;
	}

	/**
	 * writes a log to the console every 5 seconds
	 */
	@Override
	protected void writeLog() {
		rightData.calculateSpeed();
		leftData.calculateSpeed();
		System.out.println("The current state is: " + currentDriveState);
		System.out.println("The wanted drive speeds are as follows: left,right: "
				+ " " + wantedLeftPower + ", " + wantedRightPower);
		System.out.println("The encoder data as follows: right, left: " + rightData.getSpeed() 
		 + ", " + leftData.getSpeed());

	}

	/**
	 * Makes the states for drives
	 * @author Michael
	 */
	public enum State{
		SHIFTING_LOW,
		SHIFTING_HIGH,
		HIGH_GEAR,
		LOW_GEAR;

		/**
		 * Acquires the name of the state
		 * @return the state that we're in
		 */
		@Override
		public String toString(){
			switch (this){
			case SHIFTING_LOW:
				return "We are shifting to low gear!";
			case SHIFTING_HIGH:
				return "We are shifting to high gear!";
			case HIGH_GEAR:
				return "We are in high gear!";
			case LOW_GEAR:
				return "We are in low gear!";
			default:
				return "There is an error :(";

			}
		}
	}

	/**
	 * sets the power based off the joysticks 
	 * @param left the left joystick input
	 * @param right the right joystick input
	 */
	public void setPower(double left, double right) {
		wantedLeftPower = left;
		wantedRightPower = right;

	}
}
