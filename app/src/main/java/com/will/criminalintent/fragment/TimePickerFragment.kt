package com.will.criminalintent.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val stringBuilder = StringBuilder()
                stringBuilder.apply {
                    append(hourOfDay).append(":").append(minute)
                }
                val bundle = Bundle()
                bundle.putString("Time", stringBuilder.toString())
                parentFragmentManager.setFragmentResult("Time", bundle)
            }

        }

        return TimePickerDialog(requireContext(), timePickerListener, hour, minute, true)
    }
}