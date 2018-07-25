package sam.notificationamp.activities

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add_app.*
import sam.notificationamp.R
import sam.notificationamp.adapters.AppAdapter
import sam.notificationamp.model.App


class AddAppActivity : Activity() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appAdapter: AppAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)

        layoutManager = GridLayoutManager(this, 2)
        appView.layoutManager = layoutManager

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val packageManager = packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val apps: ArrayList<App> = ArrayList()
        for (applicationInfo in packages) {
            val packageName = applicationInfo.packageName
            val name = packageManager.getApplicationLabel(applicationInfo)
            val icon = packageManager.getApplicationIcon(applicationInfo)
            apps.add(App(packageName = packageName, name = name, icon = icon))
        }
        appAdapter = AppAdapter(apps, prefs)
        appView.adapter = appAdapter
    }

    override fun onResume() {
        super.onResume()
        appAdapter.notifyDataSetChanged()
    }
}
