package com.android.settings.kylinmod;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.WindowManager;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.AppMultiSelectListPreference;
import com.android.settings.widget.NumberPickerPreference;
import com.android.settings.widget.SeekBarPreference;
import com.android.settings.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LockscreenNotifications extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String KEY_LOCKSCREEN_NOTIFICATIONS = "lockscreen_notifications";
    private static final String KEY_POCKET_MODE = "pocket_mode";
    private static final String KEY_SHOW_ALWAYS = "show_always";
    private static final String KEY_HIDE_LOW_PRIORITY = "hide_low_priority";
    private static final String KEY_HIDE_NON_CLEARABLE = "hide_non_clearable";
    private static final String KEY_DISMISS_ALL = "dismiss_all";
    private static final String KEY_EXPANDED_VIEW = "expanded_view";
    private static final String KEY_FORCE_EXPANDED_VIEW = "force_expanded_view";
    private static final String KEY_WAKE_ON_NOTIFICATION = "wake_on_notification";
    private static final String KEY_NOTIFICATIONS_HEIGHT = "notifications_height";
    private static final String KEY_PRIVACY_MODE = "privacy_mode";
    private static final String KEY_OFFSET_TOP = "offset_top";
    private static final String KEY_CATEGORY_GENERAL = "category_general";
    private static final String KEY_EXCLUDED_APPS = "excluded_apps";

    private CheckBoxPreference mLockscreenNotifications;
    private CheckBoxPreference mPocketMode;
    private CheckBoxPreference mShowAlways;
    private CheckBoxPreference mWakeOnNotification;
    private CheckBoxPreference mHideLowPriority;
    private CheckBoxPreference mHideNonClearable;
    private CheckBoxPreference mDismissAll;
    private CheckBoxPreference mExpandedView;
    private CheckBoxPreference mForceExpandedView;
    private NumberPickerPreference mNotificationsHeight;
    private CheckBoxPreference mPrivacyMode;
    private SeekBarPreference mOffsetTop;
    private AppMultiSelectListPreference mExcludedAppsPref;

    private ContentResolver mResolver;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_notifications);

        mContext = getActivity().getApplicationContext();
        mResolver = mContext.getContentResolver();
        PreferenceScreen prefs = getPreferenceScreen();

        mLockscreenNotifications = (CheckBoxPreference) prefs.findPreference(KEY_LOCKSCREEN_NOTIFICATIONS);
        mLockscreenNotifications.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS, 1) == 1);
        if (Settings.System.getInt(mResolver,
                Settings.System.ENABLE_ACTIVE_DISPLAY, 0) == 1) {
            mLockscreenNotifications.setEnabled(false);
            mLockscreenNotifications.setSummary(getString(R.string.ad_or_ln, getString(R.string.active_display_title)));
        }

        mPocketMode = (CheckBoxPreference) prefs.findPreference(KEY_POCKET_MODE);
        mPocketMode.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_POCKET_MODE, 1) == 1);
        mPocketMode.setEnabled(mLockscreenNotifications.isChecked());

        mShowAlways = (CheckBoxPreference) prefs.findPreference(KEY_SHOW_ALWAYS);
        mShowAlways.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_SHOW_ALWAYS, 1) == 1);
        mShowAlways.setEnabled(mPocketMode.isChecked() && mPocketMode.isEnabled());

        mWakeOnNotification = (CheckBoxPreference) prefs.findPreference(KEY_WAKE_ON_NOTIFICATION);
        mWakeOnNotification.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_WAKE_ON_NOTIFICATION, 0) == 1);
        mWakeOnNotification.setEnabled(mLockscreenNotifications.isChecked());

        mHideLowPriority = (CheckBoxPreference) prefs.findPreference(KEY_HIDE_LOW_PRIORITY);
        mHideLowPriority.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_HIDE_LOW_PRIORITY, 0) == 1);
        mHideLowPriority.setEnabled(mLockscreenNotifications.isChecked());

        mHideNonClearable = (CheckBoxPreference) prefs.findPreference(KEY_HIDE_NON_CLEARABLE);
        mHideNonClearable.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_HIDE_NON_CLEARABLE, 1) == 1);
        mHideNonClearable.setEnabled(mLockscreenNotifications.isChecked());

        mDismissAll = (CheckBoxPreference) prefs.findPreference(KEY_DISMISS_ALL);
        mDismissAll.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_DISMISS_ALL, 1) == 1);
        mDismissAll.setEnabled(!mHideNonClearable.isChecked() && mLockscreenNotifications.isChecked());

        mPrivacyMode = (CheckBoxPreference) prefs.findPreference(KEY_PRIVACY_MODE);
        mPrivacyMode.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_PRIVACY_MODE, 0) == 1);
        mPrivacyMode.setEnabled(mLockscreenNotifications.isChecked());

        mExpandedView = (CheckBoxPreference) prefs.findPreference(KEY_EXPANDED_VIEW);
        mExpandedView.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_EXPANDED_VIEW, 1) == 1);
        mExpandedView.setEnabled(mLockscreenNotifications.isChecked() && !mPrivacyMode.isChecked());

        mForceExpandedView = (CheckBoxPreference) prefs.findPreference(KEY_FORCE_EXPANDED_VIEW);
        mForceExpandedView.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_FORCE_EXPANDED_VIEW, 0) == 1);
        mForceExpandedView.setEnabled(mLockscreenNotifications.isChecked() && mExpandedView.isChecked()
                    && !mPrivacyMode.isChecked());

        mOffsetTop = (SeekBarPreference) prefs.findPreference(KEY_OFFSET_TOP);
        int offsetValue = (int)(Settings.System.getFloat(mResolver,
                Settings.System.LOCKSCREEN_NOTIFICATIONS_OFFSET_TOP, 0.3f) * 100);
        mOffsetTop.setValue(offsetValue);
        mOffsetTop.setTitle(getResources().getText(R.string.offset_top));
        mOffsetTop.setOnPreferenceChangeListener(this);
        mOffsetTop.setEnabled(mLockscreenNotifications.isChecked());

        mNotificationsHeight = (NumberPickerPreference) prefs.findPreference(KEY_NOTIFICATIONS_HEIGHT);
        mNotificationsHeight.setValue(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_HEIGHT, 4));
        Point displaySize = new Point();
        ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(displaySize);
        int max = Math.round((float)displaySize.y * (1f - (offsetValue / 100f)) /
                (float)mContext.getResources().getDimensionPixelSize(R.dimen.notification_row_min_height));
        mNotificationsHeight.setMinValue(1);
        mNotificationsHeight.setMaxValue(max);
        mNotificationsHeight.setOnPreferenceChangeListener(this);
        mNotificationsHeight.setEnabled(mLockscreenNotifications.isChecked());

        mExcludedAppsPref = (AppMultiSelectListPreference) findPreference(KEY_EXCLUDED_APPS);
        Set<String> excludedApps = getExcludedApps();
        if (excludedApps != null) mExcludedAppsPref.setValues(excludedApps);
        mExcludedAppsPref.setOnPreferenceChangeListener(this);

        boolean hasProximitySensor = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
        if (!hasProximitySensor) {
            PreferenceCategory general = (PreferenceCategory) prefs.findPreference(KEY_CATEGORY_GENERAL);
            general.removePreference(mPocketMode);
            general.removePreference(mShowAlways);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mLockscreenNotifications) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS,
                    mLockscreenNotifications.isChecked() ? 1 : 0);
            mPocketMode.setEnabled(mLockscreenNotifications.isChecked());
            mShowAlways.setEnabled(mPocketMode.isChecked() && mPocketMode.isEnabled());
            mWakeOnNotification.setEnabled(mLockscreenNotifications.isChecked());
            mHideLowPriority.setEnabled(mLockscreenNotifications.isChecked());
            mHideNonClearable.setEnabled(mLockscreenNotifications.isChecked());
            mDismissAll.setEnabled(!mHideNonClearable.isChecked() && mLockscreenNotifications.isChecked());
            mNotificationsHeight.setEnabled(mLockscreenNotifications.isChecked());
            mOffsetTop.setEnabled(mLockscreenNotifications.isChecked());
            mForceExpandedView.setEnabled(mLockscreenNotifications.isChecked() && mExpandedView.isChecked()
                        && !mPrivacyMode.isChecked());
            mExpandedView.setEnabled(mLockscreenNotifications.isChecked() && !mPrivacyMode.isChecked());
        } else if (preference == mPocketMode) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_POCKET_MODE,
                    mPocketMode.isChecked() ? 1 : 0);
            mShowAlways.setEnabled(mPocketMode.isChecked());
        } else if (preference == mShowAlways) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_SHOW_ALWAYS,
                    mShowAlways.isChecked() ? 1 : 0);
        } else if (preference == mWakeOnNotification) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_WAKE_ON_NOTIFICATION,
                    mWakeOnNotification.isChecked() ? 1 : 0);
        } else if (preference == mHideLowPriority) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_HIDE_LOW_PRIORITY,
                    mHideLowPriority.isChecked() ? 1 : 0);
        } else if (preference == mHideNonClearable) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_HIDE_NON_CLEARABLE,
                    mHideNonClearable.isChecked() ? 1 : 0);
            mDismissAll.setEnabled(!mHideNonClearable.isChecked());
        } else if (preference == mDismissAll) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_DISMISS_ALL,
                    mDismissAll.isChecked() ? 1 : 0);
        } else if (preference == mExpandedView) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_EXPANDED_VIEW,
                    mExpandedView.isChecked() ? 1 : 0);
            mForceExpandedView.setEnabled(mExpandedView.isChecked());
        } else if (preference == mForceExpandedView) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_FORCE_EXPANDED_VIEW,
                    mForceExpandedView.isChecked() ? 1 : 0);
        } else if (preference == mPrivacyMode) {
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_PRIVACY_MODE,
                    mPrivacyMode.isChecked() ? 1 : 0);
            mForceExpandedView.setEnabled(mLockscreenNotifications.isChecked() && mExpandedView.isChecked()
                        && !mPrivacyMode.isChecked());
            mExpandedView.setEnabled(mLockscreenNotifications.isChecked() && !mPrivacyMode.isChecked());
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object value) {
        if (pref == mNotificationsHeight) {
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_HEIGHT, (Integer)value);
        } else if (pref == mOffsetTop) {
            Settings.System.putFloat(mResolver, Settings.System.LOCKSCREEN_NOTIFICATIONS_OFFSET_TOP,
                    (Integer)value / 100f);
        } else if (pref == mExcludedAppsPref) {
            storeExcludedApps((Set<String>) value);
            return true;
        } else {
            return false;
        }
        return true;
    }

    private Set<String> getExcludedApps() {
        String excluded = Settings.System.getString(mResolver,
                Settings.System.LOCKSCREEN_NOTIFICATIONS_EXCLUDED_APPS);
        if (TextUtils.isEmpty(excluded))
            return null;

        return new HashSet<String>(Arrays.asList(excluded.split("\\|")));
    }

    private void storeExcludedApps(Set<String> values) {
        StringBuilder builder = new StringBuilder();
        String delimiter = "";
        for (String value : values) {
            builder.append(delimiter);
            builder.append(value);
            delimiter = "|";
        }
        Settings.System.putString(mResolver,
                Settings.System.LOCKSCREEN_NOTIFICATIONS_EXCLUDED_APPS, builder.toString());
    }
}
