package com.will.criminalintent.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.will.criminalintent.R
import com.will.criminalintent.data.Crime
import com.will.criminalintent.viewmodel.CrimeListViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class CrimeListFragment: Fragment() {
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private lateinit var rvCrimeList : RecyclerView

    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        rvCrimeList = view.findViewById(R.id.rv_crime_list)
        rvCrimeList.layoutManager = LinearLayoutManager(context)
        rvCrimeList.adapter = adapter

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
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        rvCrimeList.adapter = adapter
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


    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<ViewHolder>() {
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

        override fun getItemCount(): Int {
            return crimes.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val crime = crimes.get(position)
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
            val crime = crimes.get(position)
//            if (crime.requiresPolice) {
//                return typeRequirePolice
//            }
            return typeNormal
        }
    }
}