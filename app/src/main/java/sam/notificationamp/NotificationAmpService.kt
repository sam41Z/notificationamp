package sam.notificationamp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.PowerManager
import android.preference.PreferenceManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.*
import android.content.Intent




class NotificationAmpService : NotificationListenerService() {

    private val TAG = this.javaClass.simpleName
    private val channelId = UUID.randomUUID().toString()
    private var slackEnabled = false
    private val ACTION_NOISE = "sam.notificationamp.ACTION_NOISE"
    private val noiseService = NoiseService();

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
        slackEnabled = prefs.getBoolean("slack", false)
    }


    override fun onNotificationPosted(sbn: StatusBarNotification) {

        Log.i(TAG, "NEW NOTIFICATION from " + sbn.packageName + " " + sbn.id)

        if (sbn.packageName == "sam.notificationamp" && sbn.id == 2) {
            createNotification()
        }

        if (sbn.packageName == "com.Slack" && slackEnabled) {
            Log.i(TAG, "Amplifying slack")
            createNotification()
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

    private fun createNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val stopIntent = Intent()
        stopIntent.action = ACTION_NOISE
        stopIntent.putExtra("command", "stop")
        val pendingStopIntent = PendingIntent.getBroadcast(baseContext, 0, stopIntent, 0)

        val notification = Notification.Builder(this@NotificationAmpService, channelId)
                .setContentTitle("Alarm")
                .setContentText("Alarm")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(pendingStopIntent)
                .setDeleteIntent(pendingStopIntent)
                .build()
        notificationManager.notify(1, notification)
        val startIntent = Intent()
        startIntent.action = ACTION_NOISE
        startIntent.putExtra("command", "start")
        sendBroadcast(startIntent)
        acquireWakeLock()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = pm?.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG)
        wakeLock?.acquire(6000L)
    }

}
