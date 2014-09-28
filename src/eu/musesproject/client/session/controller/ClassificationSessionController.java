package eu.musesproject.client.session.controller;

import android.content.Context;
import eu.musesproject.client.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.client.session.ISession;

public class ClassificationSessionController 
//extends BroadcastReceiver 
implements ISession {

	
	private Context mContext;

	public ClassificationSessionController(Context context){
		mContext = context;
	}

	@Override
	public void userPresent() {		
	}

	@Override
	public void screenOn() {
		UserContextMonitoringController.getInstance(mContext)
		.startContextObservation();		
	}

	@Override
	public void screenOff() {
		UserContextMonitoringController.getInstance(mContext)
		.stopContextObservation();		
	}

	@Override
	public void quit() {	
		UserContextMonitoringController.getInstance(mContext)
		.stopContextObservation();	
	}

}
