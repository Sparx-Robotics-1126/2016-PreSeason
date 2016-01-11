package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.util.AdvancedJoystick;
import org.gosparx.team1126.robot.util.AdvancedJoystick.ButtonEvent;
import org.gosparx.team1126.robot.util.AdvancedJoystick.JoystickListener;


public class Controls extends GenericSubsystem implements JoystickListener{

	/**
	 * declares a Joystick object named driverJoyLeft
	 */
	private AdvancedJoystick driverJoyLeft;

	/**
	 * declares a Joystick object named driverJoyRight
	 */
	private AdvancedJoystick driverJoyRight;

	/**
	 * declares a Drives object named drives
	 */
	private Drives drives;

	/**
	 * declares a Controls object named controls
	 */
	private static Controls controls;
	
	private static final int NEW_JOY_Y_AXIS = 1;
	private static final int NEW_JOY_X_AXIS = 0;

	/**
	 * if controls == null, make a new controls
	 * @return the new controls
	 */
	public static synchronized Controls getInstance(){
		if(controls == null){
			controls = new Controls();
		}
		return controls;
	}

	/**
	 * @param name the name of the control
	 * @param priority the priority of the control
	 * constructor for the Controls
	 */
	private Controls() {
		super("Controls", Thread.NORM_PRIORITY);
	}

	/**
	 * 
	 */
	@Override
	protected boolean init() {
		driverJoyLeft = new AdvancedJoystick("Left Driver", 0, 0, 0.05);
		driverJoyLeft.start();
		driverJoyRight = new AdvancedJoystick("Right Driver", 1, 0, 0.05);
		driverJoyRight.start();
		return true;
	}

	/**
	 * 
	 */
	@Override
	protected void liveWindow() {
		
	}

	@Override
	protected boolean execute() {
		if(ds.isOperatorControl()){
			double left = -driverJoyLeft.getAxis(NEW_JOY_Y_AXIS);
			double right = -driverJoyRight.getAxis(NEW_JOY_Y_AXIS);
			drives.setPower(left, right);
		}
	
		return false;
	}

	@Override
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void writeLog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ButtonEvent e) {
		// TODO Auto-generated method stub
		
	}
}
