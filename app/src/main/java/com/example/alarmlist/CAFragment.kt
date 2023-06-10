package com.example.alarmlist

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStream

class CAFragment : Fragment() {

    var saveFile : File? = null
    private var fileList : MutableList<MutableList<String>> = ArrayList()
    private lateinit var rvGrid : RecyclerView
    private var recycleAdapter : RecycleAdapter? = null
    private var checkBoxCounter = 0
    private var checkBoxPos : MutableList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saveFile = getCaFile()
        val caList : MutableList<MutableList<String>> = ArrayList()
        val view = inflater.inflate(R.layout.fragment_c_a_, container, false)

        try {
            val br = BufferedReader(FileReader(saveFile))
            if (br.readLine() == null) {
                Log.i("br", "No alarms found")
                view.findViewById<EditText>(R.id.ca_list_name).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.ca_text).text = "No alarms found"
                view.findViewById<LinearLayout>(R.id.ll_text).bringToFront()
                view.findViewById<ConstraintLayout>(R.id.cl_new_list).bringToFront()
            } else {
                Log.i("br", "alarms found")
                val inputStream: InputStream = saveFile!!.inputStream()
                inputStream.bufferedReader().forEachLine {
                    caList.add(it.split(", ").toMutableList())
                }
                fileList = caList
                view.findViewById<LinearLayout>(R.id.ll_grid).bringToFront()
            }
        }
        catch (e: Exception) {
            Log.i("brError", e.stackTraceToString())
        }

        recycleAdapter = RecycleAdapter(requireContext(), fileList)
        rvGrid = view.findViewById(R.id.rv_grid)
        rvGrid.adapter = recycleAdapter
        rvGrid.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<AppCompatButton>(R.id.clnl_new_list_btn).setOnClickListener{
            startGrid(view)
        }

        view.findViewById<AppCompatButton>(R.id.clad_add_btn).setOnClickListener{
            addRow()
        }

        view.findViewById<AppCompatButton>(R.id.clad_del_btn).setOnClickListener{
            getCheckBoxes(view)
        }

        view.findViewById<AppCompatButton>(R.id.cldr_cancel_btn).setOnClickListener{
            cancelRowDel(view)
        }

        view.findViewById<AppCompatButton>(R.id.cldr_del_rows_btn).setOnClickListener{
            delRows(view)
        }

        view.findViewById<AppCompatButton>(R.id.clad_add_list_btn).setOnClickListener{
            addListToFile(view)
        }

        view.findViewById<AppCompatButton>(R.id.clad_reset_btn).setOnClickListener{
            //resetList(view)
        }

        return view
    }

    private fun getCaFile(): File? {
        if (saveFile == null) {
            val path = context?.filesDir
            val caFile = File(path, "caFile.txt")
            val fileCreated = caFile.createNewFile()

            if (fileCreated) {
                Log.i("File creation", "caFile.txt generated")
            } else {
                Log.i("File creation", "caFile.txt already generated")
            }

            return caFile
        }

        return saveFile
    }

    private fun startGrid(view: View) {
        recycleAdapter?.addRow()
        recycleAdapter?.notifyItemInserted(recycleAdapter!!.itemCount - 1)

        view.findViewById<ConstraintLayout>(R.id.ca_alarm_view).background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)
        view.findViewById<EditText>(R.id.ca_list_name).visibility = View.VISIBLE
        view.findViewById<LinearLayout>(R.id.ll_grid).bringToFront()
        view.findViewById<ConstraintLayout>(R.id.cl_add_del).bringToFront()

        setViewSize()
    }

    private fun addRow() {
        recycleAdapter?.addRow()
        recycleAdapter?.notifyItemInserted(recycleAdapter!!.itemCount - 1)
        Log.i("addRow", "children: ${rvGrid.childCount}")
        setViewSize()
    }

    private fun setViewSize() {
        rvGrid.setItemViewCacheSize(rvGrid.childCount)
        rvGrid.recycledViewPool.setMaxRecycledViews(0, rvGrid.childCount)
    }

    private fun getCheckBoxes(view: View) {
        recycleAdapter?.showCheckBoxes()
        view.findViewById<ConstraintLayout>(R.id.cl_del_rows).bringToFront()
        view.findViewById<AppCompatButton>(R.id.cldr_del_rows_btn).text = "Delete $checkBoxCounter rows"

        val vhList = recycleAdapter?.getViewHolders()
        if (vhList != null) {
            for (vh in vhList) {
                vh.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        checkBoxPos.add(vh.adapterPosition)
                        checkBoxCounter++
                        view.findViewById<AppCompatButton>(R.id.cldr_del_rows_btn).text =
                            "Delete $checkBoxCounter rows"
                    } else {
                        checkBoxPos.remove(vh.adapterPosition)
                        checkBoxCounter--
                        view.findViewById<AppCompatButton>(R.id.cldr_del_rows_btn).text =
                            "Delete $checkBoxCounter rows"
                    }
                }
            }
        }
    }

    private fun cancelRowDel(view: View) {
        recycleAdapter?.hideCheckBoxes()
        recycleAdapter?.uncheckBoxes()
        view.findViewById<ConstraintLayout>(R.id.cl_add_del).bringToFront()
        checkBoxPos.clear()
        checkBoxCounter = 0
    }

    private fun delRows(view: View) {
        recycleAdapter?.delGridRows(checkBoxPos, rvGrid)

        if(recycleAdapter?.itemCount == 0) {
            view.findViewById<EditText>(R.id.ca_list_name).visibility = View.INVISIBLE
            view.findViewById<LinearLayout>(R.id.ll_text).bringToFront()
            view.findViewById<ConstraintLayout>(R.id.cl_new_list).bringToFront()
            view.findViewById<ConstraintLayout>(R.id.ca_alarm_view).setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.black))
        }
        else
            view.findViewById<ConstraintLayout>(R.id.cl_add_del).bringToFront()

        checkBoxPos.clear()
        checkBoxCounter = 0
    }

    private fun addListToFile(view: View) {
        val widgetValues = recycleAdapter!!.getWidgetValues()

        if(widgetValues.isEmpty()) {
            val popupView =
                LayoutInflater.from(requireContext()).inflate(R.layout.add_list_popup, null)

            val popupWindow = PopupWindow(
                popupView,
                DisplayMetrics().widthPixels,
                DisplayMetrics().heightPixels,
                true
            )
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            popupView.findViewById<TextView>(R.id.alp_cl_text).text = getClText(widgetValues)

            popupView.findViewById<AppCompatButton>(R.id.alp_cl_add_btn).setOnClickListener {

            }
        }
    }

    private fun getClText(wvList : MutableList<MutableList<String>>) : String {
        return ""
    }


}