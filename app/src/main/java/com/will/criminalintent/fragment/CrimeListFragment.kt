package com.will.criminalintent.fragment

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.will.criminalintent.R
import com.will.criminalintent.data.Crime
import com.will.criminalintent.utils.MyUtils
import com.will.criminalintent.viewmodel.CrimeListViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class CrimeListFragment: Fragment(), MenuProvider {
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private lateinit var rvCrimeList : RecyclerView

    private lateinit var tvCrimeHint: TextView
    private lateinit var btnAddCrime: Button

    private var adapter: CrimeAdapter? = CrimeAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        setHasOptionsMenu(true)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        rvCrimeList = view.findViewById(R.id.rv_crime_list)
        rvCrimeList.layoutManager = LinearLayoutManager(context)
        rvCrimeList.adapter = adapter

        tvCrimeHint = view.findViewById(R.id.tv_emptyMsg)
        btnAddCrime = view.findViewById(R.id.btn_addCrime)
        btnAddCrime.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            // 跳到详情页
            val bundle = Bundle()
            bundle.putSerializable(ARG_GRIME_ID, crime.id)
            // 传递 bundle 值给 fragment
            findNavController().navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
        }

//        MediaStore.getExternalVolumeNames(context!!).forEach { volumeName ->
//            Log.e("WillWolf", "getExternalVolumeNames-->" + volumeName)
//            val mediaDir = context!!.getExternalFilesDir(volumeName)
//            // 使用 mediaDir
//            Log.e("WillWolf", "mediaDir-->" + mediaDir)
//        }

//        MyUtils.MyTest(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("WillWolf", "onViewCreated-->")
        // 这里会回调两次，因为返回界面后，onViewCreated 又重xin注册了一次
        crimeListViewModel.crimeListLiveData.observe(viewLifecycleOwner, Observer<List<Crime>> {
                        crimes -> crimes?.let {
                Log.e("WillWolf", "get crimes ${crimes.size}")
                updateUI(crimes)
            }
//            override fun onChanged(value: List<Crime>) {
//                Log.e("WillWolf", "get crimes ${value.size}")
//                updateUI(value)
//            }
        })
        // 如果需要修改菜单内容，调用该函数可以触发 onCreateOptionsMenu 回调函数来达到目的
        // 在 onCreateOptionsMenu 回调函数里，修改菜单内容后，回调一结束，修改就立刻生效
//        activity?.invalidateOptionsMenu()

        // 需要添加 Lifecycle 状态监听，否则每次返回来，就会添加一次 menu
        val menuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    // onCreateOptionsMenu 函数由 FragmentManager 负责调用的。
    // 所以当 activity 接收到操作系统的 onCreateOptionsMenu 函数回调请求时，
    // 我们必须明确告诉 FragmentManager，其管理的 fragment 应接收 onCreateOptionsMenu
    // 函数的调用指令。需要在 onCreate 方法中，调用 setHasOptionsMenu 方法
//     override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.fragment_crime_list, menu)
//    }

    private fun updateUI(crimes: List<Crime>) {
//        val newList = mutableListOf<Crime>()
//        newList.addAll(crimes)
        if (crimes.isEmpty()) {
            tvCrimeHint.visibility = View.VISIBLE
            btnAddCrime.visibility = View.VISIBLE
            rvCrimeList.visibility = View.INVISIBLE
        } else {
            tvCrimeHint.visibility = View.GONE
            btnAddCrime.visibility = View.GONE
            rvCrimeList.visibility = View.VISIBLE
        }
        adapter?.submitList(crimes)
    }

    companion object {
        fun newInstance() : CrimeListFragment {
            return CrimeListFragment()
        }
    }

    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }


    // CrimeHolder 的 构造函数先接收 view 参数，然后将其作为值传递给 RecyclerView.ViewHolder
    // 这样 ViewHolder 基类的 itemView 属性就可以引用 view 值
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime: Crime

        val tvTitle: TextView = itemView.findViewById(R.id.tv_crimeTitle)
        val tvData: TextView = view.findViewById(R.id.tv_crimeDate)
        val ivSolved: ImageView = itemView.findViewById(R.id.iv_crimeSolved)
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd EEE", Locale.getDefault())
        init {
            itemView.setOnClickListener(this)
        }

        // 数据和视图的绑定工作都放在 CrimeHolder 里处理
        fun bind(crime: Crime) {
            this.crime = crime
            tvTitle.text = crime.title
//            tvData.text = crime.date.toString()
            tvData.text = dateFormat.format(crime.date)
            ivSolved.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else{
                View.GONE
            }
        }

        override fun onClick(v: View) {
//            Toast.makeText(context, "${crime.title}pressed!", Toast.LENGTH_SHORT).show()
            callbacks?.onCrimeSelected(crime.id)
            val bundle = Bundle()
            bundle.putSerializable(ARG_GRIME_ID, crime.id)
            // 传递 bundle 值给 fragment
            findNavController().navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
        }
    }

    private inner class CrimeHolderRequirePolice(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var crime: Crime

        val tvTitle: TextView = itemView.findViewById(R.id.tv_crimeTitle)
        val tvData: TextView = view.findViewById(R.id.tv_crimeDate)

        fun bind(crime: Crime) {
            this.crime = crime
            tvTitle.text = crime.title
            tvData.text = crime.date.toString()
        }
    }

    object itemCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.date == newItem.date
                    && oldItem.isSolved == newItem.isSolved
        }

    }

    private inner class CrimeAdapter(): ListAdapter<Crime, ViewHolder>(itemCallback) {
        private val typeNormal = 1
        private val typeRequirePolice = 2
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            if (viewType == typeNormal) {
//                val view = layoutInflater.inflate(R.layout.item_list_crime, parent, false)
//                return CrimeHolder(view)
//            }
//            val view = layoutInflater.inflate(R.layout.item_list_crime_require_police, parent, false)
//            return CrimeHolderRequirePolice(view)
            val view = layoutInflater.inflate(R.layout.item_list_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//            val crime = crimes.get(position)
            val crime = getItem(position)
//            holder.apply {
//                tvTitle.text = crime.title
//                tvData.text = crime.date.toString()
//            }
            if (holder is CrimeHolder) {
                // 将职责进一步分开，adapter 不插手 ViewHolder 的内部工作和细节
                holder.bind(crime)
            } else if (holder is CrimeHolderRequirePolice) {
                holder.bind(crime)
            }
        }

        override fun getItemViewType(position: Int): Int {
            val crime = getItem(position)
//            if (crime.requiresPolice) {
//                return typeRequirePolice
//            }
            return typeNormal
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                // 跳到详情页
                val bundle = Bundle()
                bundle.putSerializable(ARG_GRIME_ID, crime.id)
                // 传递 bundle 值给 fragment
                findNavController().navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
            }
        }
        return true
    }
}