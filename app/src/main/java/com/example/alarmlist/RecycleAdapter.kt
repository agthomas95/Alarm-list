package com.example.alarmlist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecycleAdapter(var context: Context, var rowData: MutableList<MutableList<String>>) :
    RecyclerView.Adapter<RecycleAdapter.ViewHolder>() {
    private var vhList : MutableList<ViewHolder> = ArrayList()
    private var calendar : Calendar = Calendar.getInstance()
    private var defaultRow = mutableListOf("Set date", "Set time", "Set alarm name")

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox : CheckBox
        val dateInputField: EditText
        val timeInputField: EditText
        val nameInputField: EditText

        init {
            checkBox = view.findViewById(R.id.row_check_box)
            dateInputField = view.findViewById(R.id.date_input_field)
            timeInputField = view.findViewById(R.id.time_input_field)
            nameInputField = view.findViewById(R.id.alarm_input_field)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.alarm_grid_row,
            viewGroup, false
        )

        val viewHolder = ViewHolder(view)

        viewHolder.dateInputField.setOnClickListener {
            changeDateField(viewHolder.dateInputField)
        }

        viewHolder.timeInputField.setOnClickListener {
            changeTimeField(viewHolder.timeInputField)
        }

        vhList.add(viewHolder)

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.i(
            "onBind", "current text: ${viewHolder.dateInputField.text}, " +
                    "${viewHolder.timeInputField.text}, ${viewHolder.nameInputField.text}\n" +
                    "position: $position, adapter position: ${viewHolder.adapterPosition}\n" +
                    "rowData: ${rowData[position][0]}, ${rowData[position][1]}, ${rowData[position][2]}\n"
        )
        //if rowData isn't empty and input field isn't empty or has default row, set text
        if (rowData[position][0] != "")
            viewHolder.dateInputField.setText(rowData[position][0])
        else {
            viewHolder.dateInputField.setText("")
            viewHolder.dateInputField.hint = defaultRow[0]
        }

        if (rowData[position][1] != "")
            viewHolder.timeInputField.setText(rowData[position][1])
        else {
            viewHolder.timeInputField.setText("")
            viewHolder.timeInputField.hint = defaultRow[1]
        }

        if (rowData[position][2] != "")
            viewHolder.nameInputField.setText(rowData[position][2])
        else {
            viewHolder.nameInputField.setText("")
            viewHolder.nameInputField.hint = defaultRow[2]
        }

        if(viewHolder !in vhList)
            vhList.add(viewHolder)
        Log.i("onBind", "widget values: ${getWidgetValues(vhList.size)}\n" +
                "row values: $rowData")
    }

    override fun getItemCount() = rowData.size

    fun addRow() {
        try {
            rowData.add(mutableListOf("", "", ""))
        }
        catch(e: IndexOutOfBoundsException) {
            Log.i("addRow", "IOOBE exception")
        }
    }

    fun getData() = rowData

    fun getWidgetValues(size: Int = vhList.size) : MutableList<MutableList<String>> {
        var valueList: MutableList<MutableList<String>> = ArrayList()

        for(vh in vhList.slice(0 until size)) {
            valueList.add(mutableListOf(vh.dateInputField.text.toString(),
                vh.timeInputField.text.toString(), vh.nameInputField.text.toString()))
        }

        return valueList
    }

    private fun changeDateField(editText: EditText) {
        val date = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
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

    private fun setDate(editText: EditText) {
        val dateFormat = "MM/dd/yy"
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.US)
        editText.setText(dateFormatter.format(calendar.time))
    }

    private fun changeTimeField(editText: EditText) {
        val time = TimePickerDialog.OnTimeSetListener{
                _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            setTime(editText)
        }

        val tpd = TimePickerDialog(context, time, calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), false)
        tpd.show()
    }

    private fun setTime(editText: EditText) {
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

    fun showCheckBoxes() {
        for(vh in vhList) {
            vh.checkBox.layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 10F)
            vh.nameInputField.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 50F)
        }
    }

    fun hideCheckBoxes() {
        for(vh in vhList) {
            vh.checkBox.layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0F)
            vh.nameInputField.layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 60F)
        }
    }

    fun uncheckBoxes() {
        for(vh in vhList)
            vh.checkBox.isChecked = false
    }

    fun getViewHolders() = vhList

    fun delGridRow(adapterPosition: Int) {
        Log.i("delGridRow", "rowData: $rowData,\nvhList: ${getWidgetValues(rowData.size)}")
        rowData.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
        notifyItemRangeChanged(adapterPosition, rowData.size)

        for(vh in vhList) {
            Log.i("delGridRow", "adapter positions: ${vh.adapterPosition}")
        }

    }

    fun delGridRows(posList: List<Int>, view: View) : Int {
        hideCheckBoxes()
        Log.i("delGrid", "vhList before row deletion: ${getWidgetValues(vhList.size)}\n" +
                "rowData before deletion: $rowData")
        updateRowData(rowData.size)

        if (posList.isNotEmpty()) {
            for (pos in posList.sorted().reversed()) {
                rowData.removeAt(pos)
                notifyItemRemoved(pos)
                vhList[pos].checkBox.isChecked = false
            }

            vhList.subList(posList.min(), vhList.size).clear()
            notifyItemRangeRemoved(posList.min(), rowData.size)
        }

        uncheckBoxes()
        Log.i("delGrid", "vhList after row deletion: ${getWidgetValues(vhList.size)}\n" +
                "rowData after deletion: $rowData")
        if(rowData.size == 0)
            return 0
        return rowData.size
    }

    private fun updateRowData(size: Int) {
        rowData.clear()
        rowData.addAll(getWidgetValues(size))
    }
}