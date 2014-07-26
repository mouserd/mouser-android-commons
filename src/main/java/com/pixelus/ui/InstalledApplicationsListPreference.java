package com.pixelus.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.ListPreference;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class InstalledApplicationsListPreference extends ListPreference {

  private int selectedApplicationIndex;

  public InstalledApplicationsListPreference(Context context, AttributeSet attrs) {
    super(context, attrs);

    List<CharSequence> entries = new ArrayList<CharSequence>();
    for (CharSequence entry : getEntries()) {
      entries.add(entry);
    }

    List<CharSequence> entryValues = new ArrayList<CharSequence>();
    for (CharSequence entryValue : getEntryValues()) {
      entryValues.add(entryValue);
    }

    Intent intentFilter = new Intent(Intent.ACTION_MAIN, null);
    intentFilter.addCategory(Intent.CATEGORY_LAUNCHER);

    PackageManager pm = context.getPackageManager();
    List<ResolveInfo> appList = pm.queryIntentActivities(intentFilter, PERMISSION_GRANTED);
    Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));

    for (ResolveInfo info : appList) {
      if ((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
        entryValues.add(new ComponentName(info.activityInfo.packageName, info.activityInfo.name).flattenToString());

        entries.add(pm.getApplicationLabel(info.activityInfo.applicationInfo));
      }
    }

    setEntries(entries.toArray(new CharSequence[entries.size()]));
    setEntryValues(entryValues.toArray(new CharSequence[entryValues.size()]));
  }

  @Override
  public void setEntries(CharSequence[] entries) {
    super.setEntries(entries);
    selectedApplicationIndex = -1;
  }

  @Override
  protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
    CharSequence[] entries = getEntries();
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
    CharSequence[] entryValues = getEntryValues();
    String val = getValue();

    if (val != null) {
      for (int i = 0; i < entryValues.length; i++) {
        CharSequence entry = entryValues[i];
        if (val.equals(entry)) {
          selectedApplicationIndex = i;
        }
      }
    }
  }

  @Override
  protected void onDialogClosed(boolean positiveResult) {

    CharSequence[] entryValues = getEntryValues();
    if (positiveResult && entryValues != null) {

      String value = entryValues[selectedApplicationIndex].toString();
      if (callChangeListener(value)) {
        setValue(value);
      }
    }
  }
}
