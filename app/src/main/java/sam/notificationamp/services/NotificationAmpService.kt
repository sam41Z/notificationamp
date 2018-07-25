package sam.notificationamp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.PowerManager
import android.preference.PreferenceManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import sam.notificationamp.R
import java.util.*
import kotlin.collections.HashSet


class NotificationAmpService : NotificationListenerService() {

    private val TAG = this.javaClass.simpleName
    private val channelId = UUID.randomUUID().toString()
    private var enabledPackages: Set<String> = HashSet()
    private val ACTION_NOISE = "sam.notificationamp.ACTION_NOISE"
    private val SLACK = "slack_notifications"
    private val TEST = "test_notifications"
    private val noiseService = NoiseService()

    override fun onCreate() {
        super.onCreate()
        createChannel()

        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        prefs.registerOnSharedPreferenceChangeListener { prefs: SharedPreferences, _: String ->
            Log.i(TAG, "Reloading prefs")
            loadPrefs(prefs)
        }
        loadPrefs(prefs)

        var filter = IntentFilter()
        filter.addAction(ACTION_NOISE)
        registerReceiver(noiseService, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(noiseService)
    }

    private fun loadPrefs(prefs: SharedPreferences) {
        val packages = HashSet<String>()
        for (mutableEntry in prefs.all) {
            val key = mutableEntry.key
            if (key.endsWith("_enabled") && mutableEntry.value == true) {
                packages.add(key.substring(0, key.length - 8))
            }
        }
        enabledPackages = packages
    }


    override fun onNotificationPosted(sbn: StatusBarNotification) {

        Log.i(TAG, "NEW NOTIFICATION from " + sbn.packageName + " " + sbn.id)

        if (sbn.packageName == "sam.notificationamp" && sbn.id == 2) {
            createNotification(TEST)
        }

        if (enabledPackages.contains(sbn.packageName)) {
            Log.i(TAG, "Amplifying " + sbn.packageName)
            createNotification(sbn.packageName)
        }
    }

    private fun createChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Alarm", NotificationManager.IMPORTANCE_HIGH)
        channel.description = "Amplifies notifications of other apps"
        channel.enableLights(true)
        channel.lightColor = Color.CYAN
        channel.setShowBadge(true)

        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(packageName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val stopIntent = Intent()
        stopIntent.action = ACTION_NOISE
        stopIntent.putExtra("command", "stop")
        val pendingStopIntent = PendingIntent.getBroadcast(baseContext, 0, stopIntent, 0)

        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val notification = Notification.Builder(this@NotificationAmpService, channelId)
                .setContentTitle(packageManager.getApplicationLabel(appInfo))
                .setContentText("Got a new notification")
                .setSmallIcon(R.drawable.ic_postauto_sign)
                .setAutoCancel(true)
                .setContentIntent(pendingStopIntent)
                .setDeleteIntent(pendingStopIntent)
                .build()
        notificationManager.notify(1, notification)
        val startIntent = Intent()
        startIntent.action = ACTION_NOISE
        startIntent.putExtra("command", "start")
        startIntent.putExtra("packageName", packageName)
        sendBroadcast(startIntent)
        acquireWakeLock()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = pm?.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG)
        wakeLock?.acquire(6000L)
    }

}
