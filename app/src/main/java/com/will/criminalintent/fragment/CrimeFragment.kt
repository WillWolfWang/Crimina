package com.will.criminalintent.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.will.criminalintent.R
import com.will.criminalintent.data.Crime
import com.will.criminalintent.viewmodel.CrimeDetailViewModel
import java.io.File
import java.util.Date
import java.util.UUID

public const val ARG_GRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"

private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE,MMM,dd"
class CrimeFragment: Fragment(), DatePickerFragment.Callbacks {
    private lateinit var crime: Crime
    private lateinit var etTitle: EditText
    private lateinit var btnDate: Button
    private lateinit var btnTime: Button
    private lateinit var cbSolved: CheckBox
    private lateinit var btnReport: Button
    private lateinit var btnSuspect: Button
    private lateinit var btnCall: Button
    private lateinit var btnPhoto: ImageButton
    private lateinit var ivPhoto: ImageView
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }
    // 新方式的 startActivityForLauncher
    private val requestDataLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object : ActivityResultCallback<ActivityResult?> {
        override fun onActivityResult(result: ActivityResult?) {
            when {
                result?.resultCode != Activity.RESULT_OK -> return

                result.data != null -> {
                    val contactUri: Uri? = result.data!!.data
                    val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)
                    val cursor = requireActivity().contentResolver.query(contactUri!!,
                        queryFields, null, null, null)
                    cursor?.use {
                        if (it.count == 0) {
                            return
                        }
                        it.moveToFirst()
                        val suspect = it.getString(0)
                        crime.suspect = suspect
                        crimeDetailViewModel.saveCrime(crime)
                        btnSuspect.text = suspect

                        val contactId: String = it.getString(1)
                        Log.e("WillWolf", "contactId-->" + contactId)

                        var cursorPhone = requireActivity().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)

                        if (cursorPhone?.moveToNext() == true) {
                            var index = cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            if (index > 0) {
                                val mPhone: String = cursorPhone.getString(index)
                                btnCall.setText("call:" + mPhone);
                            }
                        }

                    }
                }
            }
        }
    })

    // 请求获取访问联系人权限
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission(), object : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean) {
            if (result) {
                // 获取联系人电话
            }
        }
    })

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
        btnTime = view.findViewById(R.id.btn_crime_time)
//        btnDate.apply {
//            text = crime.date.toString()
//            isEnabled = false
//        }

        cbSolved = view.findViewById(R.id.cb_crime_solved)

        // 两边的 key 需要保持一致，否则会收不到消息
        childFragmentManager.setFragmentResultListener("Date", this, object : FragmentResultListener {
            override fun onFragmentResult(requestKey: String, result: Bundle) {
                Log.e("WillWolf", "requestKey: $requestKey " + result.getSerializable(requestKey))
                crime.date = result.getSerializable(requestKey) as Date
                updateUI()
            }
        })
        childFragmentManager.setFragmentResultListener("Time", this, object : FragmentResultListener {
            override fun onFragmentResult(requestKey: String, result: Bundle) {
                btnTime.text = result.getString(requestKey)
            }
        })

        btnReport = view.findViewById(R.id.btn_crimeReport)
        btnSuspect = view.findViewById(R.id.btn_crimeSuspect)
        btnCall = view.findViewById(R.id.btn_crimeReportCall)
        btnPhoto = view.findViewById(R.id.btn_photo)
        ivPhoto = view.findViewById(R.id.iv_photo)
        btnPhoto.apply {
//            val file = requireActivity().getExternalFilesDir("")
//            Log.e("WillWolf", "file-->" + file?.absolutePath)
            // 拍照按钮触发拍照，相机 Intent 定义在 MediaStore 里，这个类负责处理
            // 所有与多媒体相关的任务，
            val packageManager = requireActivity().packageManager
            // 发送带 MediaStore.ACTION_IMAGE_CAPTURE 操作的 intent，Android 会启动相机
            // activity 拍照
            // 默认只能拍摄缩略图这样的低分辨率照片，照片会保存在 onActivityResult() 返回的
            // Intent 对象里
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            Log.e("WillWolf", "resolvedActivity-->" + resolvedActivity)
            if (resolvedActivity == null) {
                isEnabled = false
            }
            setOnClickListener{
                Log.e("WillWolf", "photo click-->")
                // 要想获得全尺寸照片，就要让它使用文件系统存储照片，可以通过
                // 传入保存在 MediaStore.EXTRA_OUTPUT 中指向存储路径的 Uri 来完成
                // 这个 Uri 会指向 FileProvider 提供的位置
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val  cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    // 要写入文件，需要给相机应用权限，需要授予 FLAG_GRANT_WRITE_URI_PERMISSION
                    // 给所有 camera-image intent 的目标 activity，以此允许它们在 Uri 指定的位置写文件
                    // 前提条件是，声明 FileProvider 的时候添加过 android:grantUriPermission 属性
                    requireActivity().grantUriPermission(cameraActivity.activityInfo.packageName,
                        photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, Observer { crime ->
            crime?.let {
                this.crime = crime
                photoFile = crimeDetailViewModel.getPhotoFile(crime)
                // FileProvider.getUriForFile 会把本地文件路径转换为相机能使用的 Uri 形式
                // 三个参数分别是 context，provider 授权，图片文件路径
                // 授权要和  manifest 文件里的相匹配
                photoUri = FileProvider.getUriForFile(requireActivity(),
                    "com.will.criminalintent.fileprovider", photoFile)

                // 更新 UI 时，会看到勾选框被勾选的动画，这是因为勾选框被勾选是一个异步
                // 操作的结果，首次启动时，数据库也正在查询，查询结束时，才对勾选框进行操作
                updateUI()
            }
        })
        Log.e("WillWolf", "onViewCreate-->")
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
            DatePickerFragment.newInstance(crime.date).apply {
                // 建立 fragment 之间的联系，下面需要更换为 parentFragmentManager，
                // 因为需要用同一个 fragmentManager 进行管理，DatePickerFragment 是在
//                 setTargetFragment(this@CrimeFragment, REQUEST_DATE)

                // 这里需要从 CrimeFragment 中调用 childFragmentManager
                // 如果不加 this 标签，会使用 DatePickerFragment 的 FragmentManager，会产生错误
                show(this@CrimeFragment.childFragmentManager, DIALOG_DATE)
            }
        }

        btnTime.setOnClickListener() {
            TimePickerFragment().apply {
                show(this@CrimeFragment.childFragmentManager, DIALOG_TIME)
            }
        }

        btnReport.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                // 这个是 Intent 中定义的常量，所以响应 该 Intent 的 activity 也都知道这些常量
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also {intent ->
//                startActivity(intent)
                // 展示多个可选应用列表，从列表中选择想要的应用
               val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        btnSuspect.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener{
//                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
                requestDataLauncher.launch(pickContactIntent)
            }
            // 对要启动的 activity 做一个是否存在的判断
             val packageManger: PackageManager = requireActivity().packageManager
            // resolveActivity 可以找到匹配给定 Intent 任务的 activity，flag 标志 MATCH_DEFAULT_ONLY
            // 限定只搜索带 cate_gory_default 标志的 activity
            val resolvedActivity: ResolveInfo? = packageManger.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            // 找不到禁用按钮，否则应用崩溃
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }

        btnCall.apply {
            setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermission.launch(Manifest.permission.READ_CONTACTS)
                } else {
                    // Intent Action_dial 播好电话等待用户打出
                    // Intent action_call 直接打出
                    var intent = Intent(Intent.ACTION_DIAL)
                    var uri = Uri.parse("tel:" + 138)
                    intent.setData(uri)
                    startActivity(intent)
                }

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
        if (crime.suspect.isNotEmpty()) {
            btnSuspect.text = crime.suspect
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
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

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }
}