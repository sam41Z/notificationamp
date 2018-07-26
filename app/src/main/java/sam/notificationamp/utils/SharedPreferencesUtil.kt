package sam.notificationamp.utils

import android.content.SharedPreferences


class SharedPreferencesUtil {
    companion object {
        fun isEnabled(appPackage: String, prefs: SharedPreferences): Boolean {
            if (appPackage == "sam.notificationamp") {
                return true
            }
            return prefs.getBoolean(enabledKey(appPackage), false)
        }

        fun exists(appPackage: String, prefs: SharedPreferences): Boolean {
            return prefs.contains(enabledKey(appPackage))
        }
        fun getRingtone(appPackage: String, prefs: SharedPreferences): String {
            return prefs.getString(ringtoneKey(appPackage), "")
        }

        fun isVibrateEnabled(appPackage: String, prefs: SharedPreferences): Boolean {
            return prefs.getBoolean(vibrateKey(appPackage), false)
        }

        fun enabledKey(appPackage: String) :String {
            return "${appPackage}_enabled"
        }

        fun ringtoneKey(appPackage: String) :String {
            return "${appPackage}_ringtone"
        }

        fun vibrateKey(appPackage: String) :String {
            return "${appPackage}_vibrate"
        }
    }
}