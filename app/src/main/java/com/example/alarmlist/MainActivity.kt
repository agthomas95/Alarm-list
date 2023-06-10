package com.example.alarmlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ExpandableListView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    val alarmDropdownNames : MutableList<String> = ArrayList()
    val alarmLists : MutableList<MutableList<String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val caFragment = CAFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.alarm_fragment, caFragment)
            commit()
        }

        val currentAlarms : MutableList<String> = ArrayList()
        currentAlarms.add("No current alarms yet")

        val savedAlarms : MutableList<String> = ArrayList()
        savedAlarms.add("No saved alarms yet")

        alarmDropdownNames.add("Current alarms")
        alarmDropdownNames.add("Saved alarms")

        alarmLists.add(currentAlarms)
        alarmLists.add(savedAlarms)

        var alarmListAdapter = ExpandableListViewAdapter(this, alarmDropdownNames, alarmLists)
        findViewById<ExpandableListView>(R.id.alarm_dropdown).setAdapter(alarmListAdapter)
    }

    fun openCloseDrawer(view: View) {
        val drawerLayout = findViewById<DrawerLayout>(R.id.alarm_drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

}