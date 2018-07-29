package sam.notificationamp.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.app_list_item.view.*
import sam.notificationamp.R
import sam.notificationamp.activities.AppSettingsActivity
import sam.notificationamp.model.App
import android.view.MotionEvent

class AppAdapter(private val apps: ArrayList<App>, private val itemLayoutId: Int) : Adapter<AppAdapter.AppViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(parent.context, LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false))
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bindApp(apps[position], position == 0, position == apps.size - 1)
    }

    class AppViewHolder(private var context: Context, itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var view: View = itemView
        private var app: App? = null

        init {
            view.setOnClickListener(this)
            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    view.appIcon.setColorFilter(R.color.appIconTouch, PorterDuff.Mode.SRC_ATOP)
                } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    view.appIcon.setColorFilter(Color.TRANSPARENT)
                }
                false
            }
        }

        fun bindApp(app: App, isStart: Boolean, isEnd: Boolean) {
            this.app = app
            view.appIcon.setImageDrawable(app.icon)
            view.appName.text = app.name
            if (app.enabled) {
                view.appStatus.setImageResource(R.drawable.ic_alarm_filled)
            } else if (app.exists) {
                view.appStatus.setImageResource(R.drawable.ic_alarm_outlined)
            }
            view.appStatus.visibility = if (app.exists) View.VISIBLE else View.INVISIBLE
            view.divider.visibility = if (isEnd) View.INVISIBLE else View.VISIBLE
        }

        override fun onClick(v: View?) {
            val intent = Intent(context, AppSettingsActivity::class.java).apply {
                putExtra("package", app?.packageName)
                putExtra("name", app?.name)
            }
            context.startActivity(intent)
        }
    }
}