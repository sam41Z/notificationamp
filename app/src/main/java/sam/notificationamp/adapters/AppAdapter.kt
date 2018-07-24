package sam.notificationamp.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.app_list_item.view.*
import sam.notificationamp.R
import sam.notificationamp.model.App


public class AppAdapter(private val apps: ArrayList<App>) : Adapter<AppAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.app_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bindApp(apps[position])
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var view: View = itemView
        private var app: App? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindApp(app: App) {
            this.app = app
            view.appIcon.setImageDrawable(app.icon)
            view.appName.text = app.name
        }


        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}