package com.pixelus.android.widget;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.ListPreference;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.content.pm.PackageManager.GET_ACTIVITIES;

public class InstalledApplicationsListPreference extends ListPreference {

  private int selectedApplicationIndex = 0;
  private PackageManager packageManager;

  public InstalledApplicationsListPreference(final Context context, final AttributeSet attrs) {
    super(context, attrs);

    packageManager = getContext().getPackageManager();

    populateList();
  }

  protected void populateList() {
    final List<ResolveInfo> applicationList = getInstalledApplications();

    final List<CharSequence> entries = new ArrayList<CharSequence>(Arrays.asList(getEntries()));
    final List<CharSequence> entryValues = new ArrayList<CharSequence>(Arrays.asList(getEntryValues()));
    for (ResolveInfo info : applicationList) {
      entryValues.add(new ComponentName(info.activityInfo.packageName, info.activityInfo.name).flattenToString());
      entries.add(packageManager.getApplicationLabel(info.activityInfo.applicationInfo));
    }

    setEntries(entries.toArray(new CharSequence[entries.size()]));
    setEntryValues(entryValues.toArray(new CharSequence[entryValues.size()]));
  }

  protected List<ResolveInfo> getInstalledApplications() {

    final Intent intentFilter = new Intent(ACTION_MAIN, null);
    intentFilter.addCategory(CATEGORY_LAUNCHER);

    final List<ResolveInfo> applicationList = packageManager.queryIntentActivities(intentFilter, GET_ACTIVITIES);
    Collections.sort(applicationList, new ResolveInfo.DisplayNameComparator(packageManager));

    return applicationList;
  }

  @Override
  public void setEntries(final CharSequence[] entries) {
    super.setEntries(entries);
    selectedApplicationIndex = 0;
  }

  @Override
  protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
    final CharSequence[] entries = getEntries();
    final CharSequence[] entryValues = getEntryValues();
    if (entries == null || entryValues == null || entries.length != entryValues.length) {
      throw new IllegalStateException("Irregular array length");
    }

    restoreCheckedEntry();

    builder.setSingleChoiceItems(entries, selectedApplicationIndex, new DialogInterface.OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {

        selectedApplicationIndex = which;
      }
    });
  }

  private void restoreCheckedEntry() {
    final CharSequence[] entryValues = getEntryValues();
    final String val = getValue();

    if (val != null) {
      for (int i = 0; i < entryValues.length; i++) {
        final CharSequence entry = entryValues[i];
        if (val.equals(entry)) {
          selectedApplicationIndex = i;
        }
      }
    }
  }

  @Override
  protected void onDialogClosed(final boolean affirmative) {

    final CharSequence[] entryValues = getEntryValues();
    if (affirmative && entryValues != null) {

      final String value = entryValues[selectedApplicationIndex].toString();
      if (callChangeListener(value)) {
        setValue(value);
      }
    }
  }
}
