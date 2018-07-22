package sam.notificationamp

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : Activity() {
    private val prefSlack = "slack"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slackSwitch.setOnCheckedChangeListener { _: CompoundButton, enabled: Boolean ->
            PreferenceManager.getDefaultSharedPreferences(this.baseContext).edit().putBoolean(prefSlack, enabled).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        slackSwitch.isChecked = PreferenceManager.getDefaultSharedPreferences(this.baseContext).getBoolean(prefSlack, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        if (item?.itemId == R.id.actionTest) {
            createNotification()
        }
        return true
    }

    private fun createNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = UUID.randomUUID().toString()
        val channel = NotificationChannel(channelId, "Test", NotificationManager.IMPORTANCE_LOW)
        channel.description = "Amplifies notifications of other apps"
        channel.setShowBadge(true)
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(this@MainActivity, channelId)
                .setContentTitle("Test")
                .setContentText("Test")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .build()
        notificationManager.notify(2, notification)
    }
}
