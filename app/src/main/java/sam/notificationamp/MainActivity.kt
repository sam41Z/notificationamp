package sam.notificationamp

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.v4.app.NotificationManagerCompat
import android.text.TextUtils
import java.util.*


class MainActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Display the fragment as the main content.
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, AmpPreferenceFragment())
                .commit()
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AmpPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
            setHasOptionsMenu(false)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("slack_notifications_ringtone"))
            bindPreferenceSummaryToValue(findPreference("test_notifications_ringtone"))
            findPreference("test_notifications_button")?.setOnPreferenceClickListener {
                createNotification()
                return@setOnPreferenceClickListener true
            }
            findPreference("notification_settings")?.setOnPreferenceClickListener {
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
                return@setOnPreferenceClickListener true
            }
        }

        override fun onResume() {
            super.onResume()
            checkNotificationListenerSettingAndSetSummary(findPreference("notification_settings"))
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

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0)
                            listPreference.entries[index]
                        else
                            null)

            } else if (preference is RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent)

                } else if (stringValue == "car_alarm") {
                    preference.setSummary("\uD83D\uDE97\uD83D\uDE97\uD83D\uDE97\uD83D\uDE97\uD83D\uDE97")
                } else {
                    val ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue))

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null)
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        val name = ringtone.getTitle(preference.getContext())
                        preference.setSummary(name)
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            val prefs = PreferenceManager.getDefaultSharedPreferences(preference.context)

            val value = prefs.getString(preference.key, "")

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value)
        }
    }
}
