package org.gosparx.team1126.robot.subsystem;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;

public class Vision extends GenericSubsystem {

	int currSession;
	int sessionfront;
	int sessionback;
	Image frame;
	NIVision.Rect rect;
	NIVision.Rect rect2;
	private static Vision v;

	public static synchronized Vision getInstance(){
		if(v == null){
			v = new Vision();
		}
		return v;
	}
	public Vision() {
		super("vision", Thread.MIN_PRIORITY);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean init() {
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);

		// the camera name (ex "cam0") can be found through the roborio web interface
		sessionfront = NIVision.IMAQdxOpenCamera("cam0",
				NIVision.IMAQdxCameraControlMode.CameraControlModeController);
		sessionback = NIVision.IMAQdxOpenCamera("cam1",
				NIVision.IMAQdxCameraControlMode.CameraControlModeController);
		currSession = sessionfront;
		NIVision.IMAQdxConfigureGrab(currSession);
		try{
			NIVision.IMAQdxStartAcquisition(currSession);
		} catch (Exception e){
			e.printStackTrace();
		}
		/**
		 * grab an image, draw the circle, and provide it for the camera server
		 * which will in turn send it to the dashboard.
		 */
		rect = new NIVision.Rect(0, 50, 480, 10);
		rect2 = new NIVision.Rect(0, 580, 480, 10);
		return true;
	}

	@Override
	protected void liveWindow() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean execute() {
		// TODO Auto-generated method stub
		NIVision.IMAQdxGrab(currSession, frame, 1);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 1f);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect2, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 1f);
        CameraServer.getInstance().setImage(frame);
		return false;
	}

	@Override
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	protected void writeLog() {
		// TODO Auto-generated method stub

	}

	public void switchView(){
		if(currSession == sessionfront){
			NIVision.IMAQdxStopAcquisition(currSession);
			currSession = sessionback;
			NIVision.IMAQdxConfigureGrab(currSession);
		} else if(currSession == sessionback){
			NIVision.IMAQdxStopAcquisition(currSession);
			currSession = sessionfront;
			NIVision.IMAQdxConfigureGrab(currSession);
		}
	}

}
