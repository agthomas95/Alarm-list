package com.example.alarmlist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout.LayoutParams
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CAGridAdapter(var context: Context, var rowData: MutableList<MutableList<String>>) : BaseAdapter() {
    private var widgetList : MutableList<List<EditText>> = ArrayList()
    private var checkList : MutableList<CheckBox> = ArrayList()
    private var calendar : Calendar = Calendar.getInstance()

    override fun getCount(): Int {
        return rowData.size
    }

    override fun getItem(position: Int): List<String> {
        return rowData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val data = getItem(position)

        Log.i("getView", "convertView: $convertView")

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.alarm_grid_row, null)

            val checkBox = convertView!!.findViewById<CheckBox>(R.id.row_check_box)
            checkBox.tag = position
            checkList.add(checkBox)

            val dateInputField = convertView.findViewById<EditText>(R.id.date_input_field)
            dateInputField.hint = data[0]

            dateInputField.setOnClickListener{
                changeDateField(dateInputField)
            }

            val timeInputField = convertView.findViewById<EditText>(R.id.time_input_field)
            timeInputField.hint = data[1]

            timeInputField.setOnClickListener{
                changeTimeField(timeInputField)
            }

            val nameInputField = convertView.findViewById<EditText>(R.id.alarm_input_field)
            nameInputField.hint = data[2]

            widgetList.add(listOf(dateInputField, timeInputField, nameInputField))
        }

        return convertView
    }

    fun getWidgetValues() : MutableList<MutableList<String>> {
        var valueList: MutableList<MutableList<String>> = ArrayList()

        for (widgets in widgetList) {
            valueList.add(mutableListOf(widgets[0].text.toString(), widgets[1].text.toString(),
                widgets[2].text.toString()))
        }

        return valueList
    }

    fun addRow() {
        rowData.add(mutableListOf("Set date", "Set time", "Enter alarm name"))
    }

    fun showCheckBoxes() {
        for(pos in 0 until checkList.size) {
            checkList[pos].layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 10F)
            widgetList[pos][2].layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 50F)
        }
    }

    fun hideCheckBoxes() {
        for(pos in 0 until checkList.size) {
            checkList[pos].layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 0F)
            widgetList[pos][2].layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 60F)
        }
    }

    fun uncheckBoxes() {
        if(checkList != null) {
            for(checkbox in checkList)
                checkbox.isChecked = false
        }
    }

    fun getCheckList(): MutableList<CheckBox> {
        return checkList
    }

    fun delGridRows(posList : MutableList<Int>) : Int {
        for(pos in posList.indices.reversed()) {
            rowData.removeAt(pos)
        }

        if(rowData.size == 0)
            return 0
        return rowData.size
    }

    private fun changeDateField(editText: EditText) {
        val date = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDate(editText)
        }

        val dpd = DatePickerDialog(context, date, calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun setDate(editText : EditText) {
        val dateFormat = "MM/dd/yy"
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.US)
        editText.setText(dateFormatter.format(calendar.time))
    }

    private fun changeTimeField(editText: EditText) {
        val time = TimePickerDialog.OnTimeSetListener{
            view, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            setTime(editText)
        }

        val tpd = TimePickerDialog(context, time, calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), false)
        tpd.show()
    }

    private fun setTime(editText: EditText) {
        Log.i("hour", calendar.get(Calendar.HOUR_OF_DAY).toString())
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        var amOrPm = "am"
        var minuteStr : String = if(minute < 10)
            "0$minute"
        else
            minute.toString()

        when{
            hour == 0 -> hour = 12          // if hour = 0, time is 12:xx am
            hour == 12 -> amOrPm = "pm"     // if hour = 12, time is 12:xx pm
            hour > 12 -> {
                hour -= 12
                amOrPm = "pm"
            }
        }

        editText.setText("$hour:$minuteStr $amOrPm")
    }
}