package com.will.criminalintent.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar

// 使用 DialogFragment 包装一下，这样在设备发生旋转时，Dialog 对话框会重建
class DatePickerFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        var initialMonth = calendar.get(Calendar.MONTH)
        var initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), null, initialYear, initialMonth, initialDay)
    }
}