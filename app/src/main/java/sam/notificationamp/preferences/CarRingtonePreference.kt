package sam.notificationamp.preferences

import android.content.Context
import android.preference.RingtonePreference
import android.util.AttributeSet


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
        if(clickCount > 1) {
            val value = "car_alarm"
            persistString(value)
            onPreferenceChangeListener.onPreferenceChange(this, value)
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
}