package com.will.criminalintent.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

// 使用 DialogFragment 包装一下，这样在设备发生旋转时，Dialog 对话框会重建

private const val ARG_DATE = "date"
class DatePickerFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var date: Date = arguments?.getSerializable(ARG_DATE) as Date

        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        var initialMonth = calendar.get(Calendar.MONTH)
        var initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dateListener = DatePickerDialog.OnDateSetListener {
            v:DatePicker, year: Int, month: Int, day:Int ->

            var resultDate: Date = GregorianCalendar(year, month, day).time
//            targetFragment?.let {
//                fragment: Fragment -> (fragment as Callbacks).onDateSelected(resultDate)
//            }
            var bundle = Bundle()
            bundle.putSerializable("WillWolf", resultDate)
            parentFragmentManager.setFragmentResult("WillWolf", bundle)
        }
        return DatePickerDialog(requireContext(), dateListener, initialYear, initialMonth, initialDay)
    }

    // 使用伴生对象创建单例模式
    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            var args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }

    interface Callbacks {
        fun onDateSelected(date: Date)
    }
}