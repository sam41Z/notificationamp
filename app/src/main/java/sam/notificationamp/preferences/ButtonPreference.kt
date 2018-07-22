package sam.notificationamp.preferences

import android.content.Context
import android.graphics.drawable.Drawable
import android.preference.Preference
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.preferences_button.view.*
import sam.notificationamp.R


class ButtonPreference : Preference {

    constructor(context: Context) : super(context) {
        createPreference(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        createPreference(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        createPreference(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        createPreference(context, attrs)
    }

    var buttonText: String? = null
    var buttonDrawableStart: Drawable? = null
    var buttonDrawableTop: Drawable? = null
    var buttonDrawableEnd: Drawable? = null
    var buttonDrawableBottom: Drawable? = null

    private fun createPreference(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        widgetLayoutResource = R.layout.preferences_button
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ButtonPreference, defStyleAttr, 0)

            buttonText = a.getString(R.styleable.ButtonPreference_buttonText)
            buttonDrawableStart = a.getDrawable(R.styleable.ButtonPreference_buttonDrawableStart)
            buttonDrawableTop = a.getDrawable(R.styleable.ButtonPreference_buttonDrawableTop)
            buttonDrawableEnd = a.getDrawable(R.styleable.ButtonPreference_buttonDrawableEnd)
            buttonDrawableBottom = a.getDrawable(R.styleable.ButtonPreference_buttonDrawableBottom)

            a.recycle()
        }
    }

    override fun onCreateView(parent: ViewGroup?): View {
        val view = super.onCreateView(parent)
        view.preferenceButton.text = buttonText
        view.preferenceButton.setCompoundDrawablesWithIntrinsicBounds(buttonDrawableStart, buttonDrawableTop, buttonDrawableEnd, buttonDrawableBottom)
        view.preferenceButton.setOnClickListener {
            onPreferenceClickListener.onPreferenceClick(this)
        }
        return view
    }

    override fun onClick() {
        // do nothing on purpose ;)
    }
}