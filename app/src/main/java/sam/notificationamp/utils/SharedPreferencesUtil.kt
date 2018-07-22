package sam.notificationamp.utils

import android.content.SharedPreferences


class SharedPreferencesUtil {
    companion object {
        fun isEnabled(key: String, prefs: SharedPreferences): Boolean {
            if (key == "test_notifications") {
                return true
            }
            return prefs.getBoolean(key + "_enabled", false)
        }

        fun getRingtone(key: String, prefs: SharedPreferences): String {
            return prefs.getString(key + "_ringtone", "")
        }

        fun isVibrateEnabled(key: String, prefs: SharedPreferences): Boolean {
            return prefs.getBoolean(key + "_vibrate", false)
        }
    }
}