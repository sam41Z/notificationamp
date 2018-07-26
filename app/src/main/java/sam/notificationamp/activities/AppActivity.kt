package sam.notificationamp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_add_app.*
import sam.notificationamp.R
import sam.notificationamp.adapters.AppAdapter
import sam.notificationamp.model.App
import sam.notificationamp.utils.SharedPreferencesUtil


class AppActivity : Activity() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appAdapter: AppAdapter
    private val apps: ArrayList<App> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)

        layoutManager = LinearLayoutManager(this)
        appView.layoutManager = layoutManager

        val packageManager = packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in packages) {
            if (packageName == applicationInfo.packageName) {
                continue
            }
            val packageName = applicationInfo.packageName
            val name = packageManager.getApplicationLabel(applicationInfo).toString()
            val icon = packageManager.getApplicationIcon(applicationInfo)
            apps.add(App(packageName = packageName, name = name, icon = icon))
        }
        sortApps()
        appAdapter = AppAdapter(apps)
        appView.adapter = appAdapter
    }

    override fun onResume() {
        super.onResume()
        sortApps()
        appAdapter.notifyDataSetChanged()
    }

    private fun sortApps() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        for (app in apps) {
            app.exists = SharedPreferencesUtil.exists(app.packageName, prefs)
            app.enabled = SharedPreferencesUtil.isEnabled(app.packageName, prefs)
        }
        apps.sortWith(compareBy({ !it.enabled }, { !it.exists }, { it.name }))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, MainSettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
