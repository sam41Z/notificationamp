package sam.notificationamp


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator

class NoiseService : BroadcastReceiver() {

    private var player = null as MediaPlayer?
    private var vibrator = null as Vibrator?

    private fun startAlarm(context: Context?) {
        if (player == null) {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build()
            player = MediaPlayer()
            player?.setAudioAttributes(audioAttributes)
            player?.setDataSource(uri.toString())
            player?.isLooping = true
            player?.prepare()

            vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(1500, 1500), 0), audioAttributes)

            player?.start()
        }
    }

    private fun stopAlarm() {
        if (player?.isPlaying == true) {
            player?.stop()
            vibrator?.cancel()

            player?.release()
            player = null

            vibrator = null
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val command = intent?.getStringExtra("command")
        if (command == "start") {
            startAlarm(context)
        } else if (command == "stop") {
            stopAlarm()
        }
    }
}