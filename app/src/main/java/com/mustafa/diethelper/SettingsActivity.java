package com.mustafa.diethelper;

import android.preference.ListPreference;
import android.preference.Preference;

import helpers.DateTimeHelper;

/**
 * Created by Boy Mustafa on 08/09/16.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final String KEY_GENERAL_CLOCK_MODE = "general_code_mode";
    public static final String KEY_NOTIFICATONS_ACTIVE = "notifications_active";
    public static final String KEY_NOTIFICATION_DAILY_REMINDER = "notifications_daily_reminder";
    public static final String KEY_NOTIFICATIONS_DAILY_REMINDER_TIME = "notifications_daily_reminder_time";


    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryTovalueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference instanceof ListPreference){
                /*
                look up the correct display value at the preference entries list
                 */
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(value.toString());

                //set the summary to reflect the new value
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else if (preference instanceof  TimePreference){
                TimePreference timePreference = (TimePreference) preference;
                preference.setSummary(DateTimeHelper.convertLocalTimeToString(preference.getContext(),timePreference.getHour(),
                        timePreference.getMinute()));

                Dail
            }

            return false;
        }
    }

}
