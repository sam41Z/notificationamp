package sam.notificationamp.activities

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.v4.app.NotificationManagerCompat
import sam.notificationamp.R
import sam.notificationamp.preferences.ButtonPreference
import sam.notificationamp.preferences.CarRingtonePreference
import sam.notificationamp.utils.SharedPreferencesUtil
import java.util.*


class MainSettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar.setDisplayHomeAsUpEnabled(true)

        actionBar.title = "App settings"

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, MainPreferenceFragment())
                .commit()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class MainPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)

            val category = PreferenceCategory(preferenceScreen.context)
            category.title = "Test"
            preferenceScreen.addPreference(category)

            val ringtonePreference = CarRingtonePreference(context)
            ringtonePreference.key = SharedPreferencesUtil.ringtoneKey(context.packageName)
            ringtonePreference.title = "Sound"
            ringtonePreference.ringtoneType = RingtoneManager.TYPE_ALARM
            ringtonePreference.setDefaultValue("content://settings/system/alarm_sound")
            category.addPreference(ringtonePreference)
            ringtonePreference.registerSummaryUpdater()

            val vibratePreference = SwitchPreference(context)
            vibratePreference.key = SharedPreferencesUtil.vibrateKey(context.packageName)
            vibratePreference.title = "Vibrate"
            vibratePreference.setDefaultValue(true)
            category.addPreference(vibratePreference)

            val testPreference = ButtonPreference(context)
            testPreference.title = "Test notification"
            testPreference.buttonText = "Send"
            category.addPreference(testPreference)
            testPreference.setOnPreferenceClickListener {
                createNotification()
                return@setOnPreferenceClickListener true
            }
            findPreference("notificationSettings")?.setOnPreferenceClickListener {
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
                return@setOnPreferenceClickListener true
            }
        }

        override fun onResume() {
            super.onResume()
            checkNotificationListenerSettingAndSetSummary(findPreference("notificationSettings"))
        }

        private fun checkNotificationListenerSettingAndSetSummary(preference: Preference) {
            var weHaveNotificationListenerPermission = false
            for (service in NotificationManagerCompat.getEnabledListenerPackages(activity)) {
                if (service == activity.packageName) {
                    weHaveNotificationListenerPermission = true
                    break
                }
            }

            if (weHaveNotificationListenerPermission) {
                preference.summary = "We have notification listener permissions"
            } else {
                preference.summary = "We do NOT have notification listener permissions"
            }

        }

        private fun createNotification() {
            val notificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelId = UUID.randomUUID().toString()
            val channel = NotificationChannel(channelId, "Test", NotificationManager.IMPORTANCE_LOW)
            channel.description = "Amplifies notifications of other apps"
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)

            val notification = Notification.Builder(activity, channelId)
                    .setContentTitle("Test")
                    .setContentText("Test")
                    .setSmallIcon(R.drawable.ic_postauto_sign)
                    .setAutoCancel(true)
                    .build()
            notificationManager.notify(2, notification)
        }
    }
}
