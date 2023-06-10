package com.example.alarmlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class ExpandableListViewAdapter(var context: Context, var names : MutableList<String>,
                                var lists: MutableList<MutableList<String>>) :
    BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return names.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return return lists[groupPosition].size
    }

    override fun getGroup(groupPosition: Int): String {
        return names[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return lists[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val nameList = getGroup(groupPosition) as String

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.alarm_listview, null)
        }

        val nameListview = convertView!!.findViewById<TextView>(R.id.alarm_subview)
        nameListview.text = nameList
        nameListview.textSize = 24F

        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val savedList = getChild(groupPosition, childPosition) as String

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.alarm_listview, null)
        }

        val savedListview = convertView!!.findViewById<TextView>(R.id.alarm_subview)
        savedListview.text = savedList
        savedListview.textSize = 18F

        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}