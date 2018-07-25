package sam.notificationamp.activities

import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add_app.*
import sam.notificationamp.R
import sam.notificationamp.adapters.AppAdapter
import sam.notificationamp.model.App
import sam.notificationamp.utils.SharedPreferencesUtil


class AddAppActivity : Activity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var appAdapter: AppAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)

        linearLayoutManager = LinearLayoutManager(this)
        appView.layoutManager = linearLayoutManager

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val packageManager = packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val apps: ArrayList<App> = ArrayList()
        for (applicationInfo in packages) {
            val packageName = applicationInfo.packageName
            val name = packageManager.getApplicationLabel(applicationInfo)
            val icon = packageManager.getApplicationIcon(applicationInfo)
            val enabled = SharedPreferencesUtil.isEnabled(packageName, prefs)
            apps.add(App(packageName = packageName, name = name, icon = icon, enabled = enabled))
        }
        appAdapter = AppAdapter(apps)
        appView.adapter = appAdapter
    }
}
