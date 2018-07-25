package sam.notificationamp.preferences

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.preference.PreferenceManager
import android.preference.RingtonePreference
import android.text.TextUtils
import android.util.AttributeSet
import sam.notificationamp.R


class CarRingtonePreference : RingtonePreference {

    var lastClick: Long = 0
    var clickCount: Int = 0

    override fun onClick() {
        super.onClick()
        var now = System.currentTimeMillis()
        if (now - lastClick > 200) {
            clickCount = 0
        }
        clickCount++
        lastClick = now
        if (clickCount > 1) {
            val value = "car_alarm"
            persistString(value)
            onPreferenceChangeListener.onPreferenceChange(this, value)
        }
    }

    fun registerSummaryUpdater() {
        setOnPreferenceChangeListener { _, newValue ->
            setSummary(newValue)
            true
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val value = prefs.getString(key, "")
        onPreferenceChangeListener.onPreferenceChange(this, value)
    }

    private fun setSummary(value: Any) {
        val stringValue = value.toString()
        if (TextUtils.isEmpty(stringValue)) {
            setSummary(R.string.pref_ringtone_silent)
        } else if (stringValue == "car_alarm") {
            summary = "\uD83D\uDE97\uD83D\uDE97\uD83D\uDE97\uD83D\uDE97\uD83D\uDE97"
        } else {
            val ringtone = RingtoneManager.getRingtone(context, Uri.parse(stringValue))
            if (ringtone == null) {
                summary = null
            } else {
                summary = ringtone.getTitle(context)
            }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
}