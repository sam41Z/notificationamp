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

        fun wasEnabled(appPackage: String, prefs: SharedPreferences): Boolean {
            return prefs.getLong(lastEnabledKey(appPackage), 0) > 0
        }

        fun setLastEnabled(appPackage: String, prefs: SharedPreferences) {
            prefs.edit().putLong(lastEnabledKey(appPackage), System.currentTimeMillis()).apply()
        }

        fun getRingtone(appPackage: String, prefs: SharedPreferences): String {
            return prefs.getString(ringtoneKey(appPackage), "")
        }

        fun isVibrateEnabled(appPackage: String, prefs: SharedPreferences): Boolean {
            return prefs.getBoolean(vibrateKey(appPackage), false)
        }

        fun forgetAll(appPackage: String, prefs: SharedPreferences) {
            prefs.edit()
                    .remove(enabledKey(appPackage))
                    .remove(ringtoneKey(appPackage))
                    .remove(vibrateKey(appPackage))
                    .remove(lastEnabledKey(appPackage))
                    .apply()
        }

        fun enabledKey(appPackage: String): String {
            return "${appPackage}_enabled"
        }

        private fun lastEnabledKey(appPackage: String): String {
            return "${appPackage}_lastEnabled"
        }

        fun ringtoneKey(appPackage: String): String {
            return "${appPackage}_ringtone"
        }

        fun vibrateKey(appPackage: String): String {
            return "${appPackage}_vibrate"
        }
    }
}