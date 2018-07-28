package sam.notificationamp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.app_activity.*
import sam.notificationamp.R
import sam.notificationamp.adapters.AppAdapter
import sam.notificationamp.model.App
import sam.notificationamp.utils.SharedPreferencesUtil


class AppActivity : Activity() {

    private enum class AppLayout { GRID, LIST }

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appAdapter: AppAdapter
    private val apps: ArrayList<App> = ArrayList()
    private val listener = ViewTreeObserver.OnGlobalLayoutListener {
        removeOnGlobalLayoutListener()
        val cardViewWidth = resources.getDimension(R.dimen.appGridItemWidth).toDouble()
        val newSpanCount = Math.floor((appView.measuredWidth) / cardViewWidth).toInt()
        (layoutManager as GridLayoutManager).spanCount = newSpanCount
        layoutManager.requestLayout()
    }

    private var appLayout: AppLayout = AppLayout.GRID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity)

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
        appLayout = AppLayout.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("appLayout", AppLayout.GRID.toString()))

        when (appLayout) {
            AppLayout.GRID -> setGridLayout()
            AppLayout.LIST -> setListLayout()
        }
    }

    private fun setGridLayout() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("appLayout", AppLayout.GRID.toString()).apply()
        appLayout = AppLayout.GRID
        layoutManager = GridLayoutManager(this, 1)
        appView.layoutManager = layoutManager
        appAdapter = AppAdapter(apps, R.layout.app_grid_item)
        appView.adapter = appAdapter
        appView.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    private fun setListLayout() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("appLayout", AppLayout.LIST.toString()).apply()
        appLayout = AppLayout.LIST
        layoutManager = LinearLayoutManager(this)
        appView.layoutManager = layoutManager
        appAdapter = AppAdapter(apps, R.layout.app_list_item)
        appView.adapter = appAdapter
    }

    private fun removeOnGlobalLayoutListener() {
        appView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    override fun onResume() {
        super.onResume()
        sortApps()
        appAdapter.notifyDataSetChanged()
    }

    private fun sortApps() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        for (app in apps) {
            app.exists = SharedPreferencesUtil.wasEnabled(app.packageName, prefs)
            app.enabled = SharedPreferencesUtil.isEnabled(app.packageName, prefs)
        }
        apps.sortWith(compareBy({ !it.enabled }, { !it.exists }, { it.name }))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.settingsList).isVisible = appLayout == AppLayout.GRID
        menu.findItem(R.id.settingsGrid).isVisible = appLayout == AppLayout.LIST
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, MainSettingsActivity::class.java))
                true
            }
            R.id.settingsList -> {
                setListLayout()
                appView.requestLayout()
                invalidateOptionsMenu()
                true
            }
            R.id.settingsGrid -> {
                setGridLayout()
                appView.requestLayout()
                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
