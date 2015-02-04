package eu.musesproject.client.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import eu.musesproject.client.R;

import java.util.Random;

/**
 * Created by D on 04.02.15.
 */
public class InstallationDialogActivity extends DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View mainView = inflater.inflate(R.layout.installation_dialog, null);

        TextView deviceIdTV = (TextView) mainView.findViewById(R.id.deviceId);
        setDeviceId(deviceIdTV);

        builder.setView(mainView);

        builder.setPositiveButton(getString(android.R.string.ok), this);
        builder.setNegativeButton(getString(android.R.string.cancel), null);

        builder.setTitle("Situation prediction study");
        builder.setCancelable(false);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    private void setDeviceId(TextView tv) {
        int deviceId = new Random().nextInt(1000000);
        tv.setText(deviceId + "");
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                saveValues();
                break;
        }
    }

    private void saveValues() {

    }
}
