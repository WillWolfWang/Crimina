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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.will.criminalintent.R
import com.will.criminalintent.data.Crime
import com.will.criminalintent.viewmodel.CrimeDetailViewModel
import java.util.UUID

public const val ARG_GRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
class CrimeFragment: Fragment() {
    private lateinit var crime: Crime
    private lateinit var etTitle: EditText
    private lateinit var btnDate: Button
    private lateinit var cbSolved: CheckBox

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    // fragment 的 onCreate 函数是 public 的
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_GRIME_ID) as UUID
        Log.e("WillWolf", "args bundle crime ID: $crimeId")
        crimeDetailViewModel.loadCrime(crimeId)
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
//        btnDate.apply {
//            text = crime.date.toString()
//            isEnabled = false
//        }

        cbSolved = view.findViewById(R.id.cb_crime_solved)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, Observer { crime ->
            crime?.let {
                this.crime = crime
                // 更新 UI 时，会看到勾选框被勾选的动画，这是因为勾选框被勾选是一个异步
                // 操作的结果，首次启动时，数据库也正在查询，查询结束时，才对勾选框进行操作
                updateUI()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // 匿名内部类
        // 如果在 onCreate 方法中监听，屏幕旋转时，会执行一次回调
        val titleWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                Log.e("WillWolf", "beforeTextChanged-->")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
//                Log.e("WillWolf", "onTextChanged-->")
            }

            override fun afterTextChanged(s: Editable?) {
//                Log.e("WillWolf", "afterTextChanged-->")
            }
        }
        // 视图监听器写在 onStart 中，可以避免因设备旋转，视图恢复后数据重置时 触发监听器函数
        etTitle.addTextChangedListener(titleWatcher)

        cbSolved.apply {
            // 这个 _ 是 view 参数，因为 view 未被用到，使用 _ 代替
            setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
        }

        btnDate.setOnClickListener(){
            // 创建 DatePickerFragment 对象，调用 apply 扩展函数
            DatePickerFragment().apply {
                // 这里需要从 CrimeFragment 中调用 childFragmentManager
                // 如果不加 this 标签，会使用 DatePickerFragment 的 FragmentManager，会产生错误
                show(this@CrimeFragment.childFragmentManager, DIALOG_DATE)
            }
        }
    }

    // 用户离开 crime 明细界面，或者切换任务，比如按 home 键，或者使用 概览屏， home 旁边的按键，
    //甚至内存不够进程被杀，在 onStop 函数中保存数据都能保证用户编辑数据不会丢失
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    private fun updateUI() {
        etTitle.setText(crime.title)
        btnDate.setText(crime.date.toString())
//        cbSolved.isChecked = crime.isSolved
        cbSolved.apply {
            isChecked = crime.isSolved
            // 跳过 checkbox 动画，直接显示勾选结果状态
            jumpDrawablesToCurrentState()
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