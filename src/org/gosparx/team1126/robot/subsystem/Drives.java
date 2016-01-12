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
	 * 
	 */
	private State currentDriveState;

	/**
	 * 
	 */
	private final double SHIFTING_SPEED = .1;

	/**
	 * 
	 */
	private final int LOWER_SHIFTING_SPEED = 10;

	/**
	 * 
	 */
	private final int UPPER_SHIFTING_SPEED = 100;

	/**
	 * 
	 */
	private final double SHIFTING_TIME = 0.25;

	/**
	 * 
	 */
	private final boolean LOW_GEAR = false;

	/**
	 * 
	 */
	private double shiftTime;

	/**
	 * 
	 */
	private static Drives drives;

	/**
	 * 
	 */
	private static double wantedLeftPower;

	/**
	 * 
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
	 * 
	 * @param name represents the 
	 * @param priority
	 */
	public Drives() {
		super("Drives", Thread.NORM_PRIORITY);
	}

	/**
	 * 
	 * @return
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
	 * 
	 */
	@Override
	protected void liveWindow() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected boolean execute() {
		rightData.calculateSpeed();
		leftData.calculateSpeed();
		currentSpeed = (rightData.getSpeed() + leftData.getSpeed()) / 2;
		switch(currentDriveState){
		case LOW_GEAR:
			if(Math.abs(currentSpeed) >= SHIFTING_SPEED){
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
			if(Math.abs(currentSpeed) <= SHIFTING_SPEED){
				currentDriveState = State.SHIFTING_LOW;
				shiftTime = Timer.getFPGATimestamp();
				if(currentSpeed < 0){
					wantedRightPower = (SHIFTING_SPEED * -1);
					wantedLeftPower = (SHIFTING_SPEED * -1);
				}else{
					wantedRightPower = (SHIFTING_SPEED);
					wantedLeftPower = (SHIFTING_SPEED);
				}
			}
			break;

		case SHIFTING_LOW:
			shifter.set(LOW_GEAR);
			if(Timer.getFPGATimestamp() >= shiftTime + SHIFTING_TIME){
				currentDriveState = State.LOW_GEAR;
			}
			shifter.set(LOW_GEAR);
			wantedRightPower = LOWER_SHIFTING_SPEED;
			wantedLeftPower = LOWER_SHIFTING_SPEED;
			break;

		case SHIFTING_HIGH:
			shifter.set(LOW_GEAR);
			if(Timer.getFPGATimestamp() >= shiftTime + SHIFTING_TIME){
				currentDriveState = State.HIGH_GEAR;
			}
			shifter.set(LOW_GEAR);
			wantedRightPower = UPPER_SHIFTING_SPEED;
			wantedLeftPower = UPPER_SHIFTING_SPEED;


			break;

		}
		leftDrives.set(wantedLeftPower);
		rightDrives.set(wantedRightPower);

		return false;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 */
	@Override
	protected void writeLog() {
		// TODO Auto-generated method stub

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

	public void setPower(double left, double right) {
		wantedLeftPower = left;
		wantedRightPower = right;

	}

}
