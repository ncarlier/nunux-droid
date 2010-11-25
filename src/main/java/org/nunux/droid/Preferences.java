package org.nunux.droid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import org.nunux.droid.service.DroidService;
import org.nunux.droid.tools.ServiceToolkit;

/**
 * Preferences screen.
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

        CheckBoxPreference cbp = (CheckBoxPreference) getPreferenceScreen().findPreference(KEY_ACTIVATION_PREFERENCE);
        cbp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean activating = (Boolean) newValue;
                if (activating.booleanValue()) {
                    stopService(new Intent(getApplicationContext(), DroidService.class));
                    startService(new Intent(getApplicationContext(), DroidService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), DroidService.class));
                }
                return true;
            }
        });
        cbp.setChecked(ServiceToolkit.isServiceExisted(this, DroidService.class.getName()) != null);

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference pref = getPreferenceScreen().getPreference(i);
            if (pref instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) pref;
                pref.setSummary(etp.getText());
            } else if (pref instanceof ListPreference) {
                ListPreference lp = (ListPreference) pref;
                pref.setSummary(lp.getValue());
            }
        }

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().
                unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        } else if (pref instanceof ListPreference) {
            ListPreference lp = (ListPreference) pref;
            pref.setSummary(lp.getValue());
        }
    }

}
