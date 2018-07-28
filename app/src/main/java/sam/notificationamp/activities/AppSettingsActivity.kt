package sam.notificationamp.activities

import android.annotation.TargetApi
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.preference.*
import sam.notificationamp.R
import sam.notificationamp.preferences.ButtonPreference
import sam.notificationamp.preferences.CarRingtonePreference
import sam.notificationamp.utils.SharedPreferencesUtil


class AppSettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar.setDisplayHomeAsUpEnabled(true)

        val appPackage = intent.getStringExtra("package")
        val appName = intent.getStringExtra("name")

        actionBar.title = "Alarm settings"

        // Display the fragment as the main content.
        val fragment = AppPreferenceFragment()
        fragment.arguments = Bundle().apply {
            putString("name", appName)
            putString("package", appPackage)
        }
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AppPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.app_preferences)

            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val appName = arguments.getString("name")
            val appPackage = arguments.getString("package")

            val category = PreferenceCategory(preferenceScreen.context)
            category.title = appName
            preferenceScreen.addPreference(category)

            val enabledPreference = SwitchPreference(context)
            enabledPreference.key = SharedPreferencesUtil.enabledKey(appPackage)
            enabledPreference.title = "Amplify $appName"
            enabledPreference.setDefaultValue(false)
            category.addPreference(enabledPreference)

            val ringtonePreference = CarRingtonePreference(context)
            ringtonePreference.key = SharedPreferencesUtil.ringtoneKey(appPackage)
            ringtonePreference.title = "Sound"
            ringtonePreference.ringtoneType = RingtoneManager.TYPE_ALARM
            ringtonePreference.setDefaultValue("content://settings/system/alarm_sound")
            category.addPreference(ringtonePreference)
            ringtonePreference.registerSummaryUpdater()

            val vibratePreference = SwitchPreference(context)
            vibratePreference.key = SharedPreferencesUtil.vibrateKey(appPackage)
            vibratePreference.title = "Vibrate"
            vibratePreference.setDefaultValue(true)
            category.addPreference(vibratePreference)

            if (SharedPreferencesUtil.wasEnabled(appPackage, prefs)) {
                val forgetPreference = ButtonPreference(context)
                forgetPreference.title = "Forget settings of this app"
                forgetPreference.buttonText = "Forget"
                category.addPreference(forgetPreference)
                forgetPreference.setOnPreferenceClickListener {
                    SharedPreferencesUtil.forgetAll(appPackage, prefs)
                    activity.finish()
                    return@setOnPreferenceClickListener true
                }
            }

            enabledPreference.setOnPreferenceChangeListener { preference, newValue ->
                val enabled = newValue.toString().toBoolean()
                ringtonePreference.isEnabled = enabled
                vibratePreference.isEnabled = enabled
                if (enabled) {
                    SharedPreferencesUtil.setLastEnabled(appPackage, prefs)
                }
                true
            }
            val value = prefs.getBoolean(enabledPreference.key, false)
            enabledPreference.onPreferenceChangeListener.onPreferenceChange(enabledPreference, value)
        }
    }
}
