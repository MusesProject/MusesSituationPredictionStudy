package eu.musesproject.client.ui;

/*
 * #%L
 * MUSES Client
 * %%
 * Copyright (C) 2013 - 2014 Sweden Connectivity
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import eu.musesproject.MUSESBackgroundService;
import eu.musesproject.client.R;
import eu.musesproject.client.prediction.dataexport.DataExport;
import eu.musesproject.client.prediction.preferences.IsLabelingActivatedPreference;
import eu.musesproject.client.prediction.session.QuitService;

/**
 * MainActivity class handles List buttons on the main GUI
 *
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public class MainActivity extends Activity{

    private static String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.muses_main);


        // starts the background service of MUSES
        startService(new Intent(this, MUSESBackgroundService.class));
        Log.v(TAG, "muses service started ...");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}