package sam.notificationamp.model

import android.graphics.drawable.Drawable

class App(val packageName: String, val name: String, val icon: Drawable) {
    var exists: Boolean = false
    var enabled: Boolean = false
}