package com.rahulxyz.foodish.Fragment;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.rahulxyz.foodish.R;

/**
 * Created by raul_Will on 10/23/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_setting);
    }
}
