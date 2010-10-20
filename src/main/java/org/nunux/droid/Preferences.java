/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import org.nunux.droid.service.XmppService;

/**
 *
 * @author Nicolas Carlier
 */
public class Preferences extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String SHARED_PREFERENCE_NAME = "NunuxDroid";

    public static final String KEY_ACTIVATION_PREFERENCE = "activation";
    public static final String KEY_EXT_XMPP_ACCOUNT_PREFERENCE = "extXmppAccount";
    public static final String KEY_LOGIN_PREFERENCE = "login";
    public static final String KEY_PASSWORD_PREFERENCE = "password";
    public static final String KEY_SERVER_HOST_PREFERENCE = "serverHost";
    public static final String KEY_SERVER_PORT_PREFERENCE = "serverPort";
    public static final String KEY_SERVICE_NAME_PREFERENCE = "serviceName";
    public static final String KEY_SASL_MECHANISM_PREFERENCE = "serviceName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(SHARED_PREFERENCE_NAME);
        addPreferencesFromResource(R.xml.preferences);
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference pref = getPreferenceScreen().getPreference(i);
            if (pref instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) pref;
                pref.setSummary(etp.getText());
            }
        }
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (KEY_ACTIVATION_PREFERENCE.equals(key) && sharedPreferences.getBoolean(key, false)) {
            if (sharedPreferences.getBoolean(key, false)) {
                stopService(new Intent(getApplicationContext(), XmppService.class));
                startService(new Intent(getApplicationContext(), XmppService.class));
            } else {
                stopService(new Intent(getApplicationContext(), XmppService.class));
            }
        } else {
            Preference pref = findPreference(key);
            if (pref instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) pref;
                pref.setSummary(etp.getText());
            }
        }
    }

}
