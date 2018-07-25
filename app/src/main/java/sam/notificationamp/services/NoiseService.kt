package sam.notificationamp.services


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import sam.notificationamp.R
import sam.notificationamp.utils.SharedPreferencesUtil

class NoiseService : BroadcastReceiver() {

    private var player = null as MediaPlayer?
    private var vibrator = null as Vibrator?

    private fun startAlarm(context: Context?, packageName: String?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        if (player == null && packageName != null && SharedPreferencesUtil.isEnabled(packageName, prefs)) {
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build()

            if (SharedPreferencesUtil.isVibrateEnabled(packageName, prefs)) {
                vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(1500, 1500), 0), audioAttributes)
            }

            var ringtone = SharedPreferencesUtil.getRingtone(packageName, prefs)
            if (ringtone == "") {
                return
            }

            if (ringtone == "car_alarm") {
                ringtone = "android.resource://${context?.packageName}/${R.raw.car_alarm}"
            }
            val uri = Uri.parse(ringtone)
            player = MediaPlayer()
            player?.setAudioAttributes(audioAttributes)
            player?.setDataSource(context, uri)
            player?.isLooping = true
            player?.prepare()

            player?.start()
        }
    }

    private fun stopAlarm() {

        vibrator?.cancel()
        vibrator = null

        if (player?.isPlaying == true) {
            player?.stop()
            player?.release()
            player = null
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val command = intent?.getStringExtra("command")
        val packageName = intent?.getStringExtra("packageName")
        if (command == "start") {
            startAlarm(context, packageName)
        } else if (command == "stop") {
            stopAlarm()
        }
    }
}