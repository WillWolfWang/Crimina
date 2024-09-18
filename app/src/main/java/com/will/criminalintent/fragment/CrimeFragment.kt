package com.will.criminalintent.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.will.criminalintent.R
import com.will.criminalintent.data.Crime
import java.util.UUID

private const val ARG_GRIME_ID = "crime_id"
class CrimeFragment: Fragment() {
    private lateinit var crime: Crime
    private lateinit var etTitle: EditText
    private lateinit var btnDate: Button
    private lateinit var cbSolved: CheckBox
    // fragment 的 onCreate 函数是 public 的
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_GRIME_ID) as UUID
        Log.e("WillWolf", "args bundle crime ID: $crimeId")
    }

    // 该函数会实例化 fragment 视图的布局，然后将实例化的 View 返回给托管的 activity
    override fun onCreateView(
        inflater: LayoutInflater,//
        container: ViewGroup?, // LayoutInflater 和 ViewGroup 是实例化布局的必要参数
        savedInstanceState: Bundle? // 用来存储恢复数据
    ): View? {
        // 第三个参数告诉布局生成器是否立即将生成的视图添加到父视图。因为 fragment 的视图由 activity 的容器
        // 视图托管，所以这里传入 false
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        etTitle = view.findViewById<EditText>(R.id.et_crime_title)
        btnDate = view.findViewById(R.id.btn_crime_date)
        btnDate.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        cbSolved = view.findViewById(R.id.cb_crime_solved)
        return view
    }

    override fun onStart() {
        super.onStart()
        // 匿名内部类
        // 如果在 onCreate 方法中监听，屏幕旋转时，会执行一次回调
        val titleWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.e("WillWolf", "beforeTextChanged-->")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
                Log.e("WillWolf", "onTextChanged-->")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.e("WillWolf", "afterTextChanged-->")
            }
        }
        // 视图监听器写在 onStart 中，可以避免因设备旋转，视图恢复后数据重置时 触发监听器函数
        etTitle.addTextChangedListener(titleWatcher)

        cbSolved.apply {
            setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
        }
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_GRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}