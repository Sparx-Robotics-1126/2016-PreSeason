package org.gosparx.team1126.robot.subsystem;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

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
	 * the wanted power for the left drives (-1 to 1)
	 */
	private double leftPower;

	/**
	 * the wanted power for the rigth drives (-1 to 1)
	 */
	private double rightPower;

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
	 * @param name represents the 
	 * @param priority
	 */
	public Drives(String name, int priority) {
		super(name, priority);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected boolean init() {
		rightDrives = new Talon(8); 
		leftDrives = new Talon(8);
		leftEncoder = new Encoder(7,8);
		leftData = new EncoderData(leftEncoder, DISTANCE_PER_TICK);
		rightEncoder = new Encoder(7,8);
		rightData = new EncoderData(rightEncoder, DISTANCE_PER_TICK);
		shifter = new Solenoid(8888);
		currentDriveState = State.LOW_GEAR;
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
		leftPower = 0;
		rightPower = 0;
		rightData.calculateSpeed();
		leftData.calculateSpeed();
		currentSpeed = (rightData.getSpeed() + leftData.getSpeed()) / 2;
		switch(currentDriveState){
		case LOW_GEAR:
			if(Math.abs(currentSpeed) >= SHIFTING_SPEED){
				currentDriveState = State.SHIFTING_HIGH;
				if(currentSpeed < 0){
					rightPower = (SHIFTING_SPEED * - 1);
					leftPower = (SHIFTING_SPEED * - 1);
				}else {
					rightPower = (SHIFTING_SPEED);
					leftPower = (SHIFTING_SPEED);
				}
			}else{
				rightPower = (currentSpeed);
				leftPower = (currentSpeed);
			}
			break;
			
		case HIGH_GEAR:
			if(Math.abs(currentSpeed) <= SHIFTING_SPEED){
				currentDriveState = State.SHIFTING_LOW;
				if(currentSpeed < 0){
					rightPower = (SHIFTING_SPEED * -1);
					leftPower = (SHIFTING_SPEED * -1);
				}else{
					rightPower = (SHIFTING_SPEED);
					leftPower = (SHIFTING_SPEED);
				}
			}else{
				rightPower = (currentSpeed);
				leftPower = (currentSpeed);
			}
			break;
			
		case SHIFTING_LOW:
			
			break;
			
		case SHIFTING_HIGH:
			
			break;
			
		}
		leftDrives.set(leftPower);
		rightDrives.set(rightPower);
			
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

}
