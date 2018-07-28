package sam.notificationamp.services


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
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

            val vibrate = SharedPreferencesUtil.isVibrateEnabled(packageName, prefs)
            if (vibrate) {
                vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 1500, 1500, 1500, 1500, 1500, 1500), 1),
                        AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                                .setLegacyStreamType(AudioAttributes.USAGE_ALARM)
                                .build())
            }

            var ringtone = SharedPreferencesUtil.getRingtone(packageName, prefs)

            if (ringtone == "car_alarm") {
                ringtone = "android.resource://${context?.packageName}/${R.raw.car_alarm}"
            }
            if (ringtone != "") {
                val uri = Uri.parse(ringtone)
                player = MediaPlayer()
                player?.setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                        .build())
                player?.setDataSource(context, uri)
                player?.isLooping = true
                player?.prepare()
                player?.start()
            }
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