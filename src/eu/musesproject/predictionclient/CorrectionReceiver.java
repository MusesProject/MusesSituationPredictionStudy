package eu.musesproject.predictionclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CorrectionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Not implemented. Coming soon...", Toast.LENGTH_LONG).show();
		
		Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		context.sendBroadcast(it);
	}

}
