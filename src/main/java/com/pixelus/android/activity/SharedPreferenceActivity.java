package com.pixelus.android.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.MenuItem;

public abstract class SharedPreferenceActivity
    extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  public abstract int getIconResourceId();
  public abstract int getPreferencesXmlResourceId();

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      getActionBar().setDisplayHomeAsUpEnabled(true);
      getActionBar().setIcon(getIconResourceId());
    }

    setupSimplePreferencesScreen();

  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    initSummary(getPreferenceScreen());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      // TODO: if the previous activity on the stack isn't a ConfigurationActivity, launch it.
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Set up a listener whenever a key changes
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();

    // Unregister the listener whenever a key changes
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }


  private void setupSimplePreferencesScreen() {
    // In the simplified UI, fragments are not used at all and we instead
    // use the older PreferenceActivity APIs.

    // Add 'general' preferences.
    addPreferencesFromResource(getPreferencesXmlResourceId());
  }

  public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
    updatePrefSummary(findPreference(key));
  }

  private void initSummary(final Preference p) {

    if (p instanceof PreferenceGroup) {
      final PreferenceGroup pGrp = (PreferenceGroup) p;
      for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
        initSummary(pGrp.getPreference(i));
      }
    } else {
      updatePrefSummary(p);
    }
  }

  private void updatePrefSummary(final Preference p) {

    if (p instanceof ListPreference) {

      final ListPreference listPref = (ListPreference) p;
      p.setSummary(listPref.getEntry());
    }

    if (p instanceof EditTextPreference) {

      final EditTextPreference editTextPref = (EditTextPreference) p;
      if (p.getTitle().toString().contains("assword")) {
        p.setSummary("******");
      } else {
        p.setSummary(editTextPref.getText());
      }
    }

    if (p instanceof MultiSelectListPreference) {

      final EditTextPreference editTextPref = (EditTextPreference) p;
      p.setSummary(editTextPref.getText());
    }
  }
}