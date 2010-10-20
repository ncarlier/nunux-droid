/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid;

import android.app.Application;

/**
 *
 * @author Nicolas Carlier
 */
public class DroidApplication extends Application {

    public void onCreate() {
        /*
         * This populates the default values from the preferences XML file. See
         * {@link DefaultValues} for more details.
         */
        //PreferenceManager.setDefaultValues(this, R.xml.default_values, false);
    }

    public void onTerminate() {
    }
}
