package sam.notificationamp.activities

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.v4.app.NotificationManagerCompat
import android.text.TextUtils
import android.view.View
import sam.notificationamp.R
import sam.notificationamp.preferences.CarRingtonePreference
import sam.notificationamp.utils.SharedPreferencesUtil
import java.util.*


class AppSettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appPackage = intent.getStringExtra("package")
        val appName = intent.getStringExtra("name")


        // Display the fragment as the main content.
        val fragment = AmpPreferenceFragment()
        fragment.arguments = Bundle().apply {
            putString("name", appName)
            putString("package", appPackage)
        }
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AmpPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setHasOptionsMenu(false)
            addPreferencesFromResource(R.xml.app_preferences)

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

            enabledPreference.setOnPreferenceChangeListener { preference, newValue ->
                val enabled = newValue.toString().toBoolean()
                ringtonePreference.isEnabled = enabled
                vibratePreference.isEnabled = enabled
                true
            }
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val value = prefs.getBoolean(enabledPreference.key, false)
            enabledPreference.onPreferenceChangeListener.onPreferenceChange(enabledPreference, value)
        }
    }
}
