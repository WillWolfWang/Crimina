package com.will.criminalintent.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.will.criminalintent.R
import com.will.criminalintent.data.Crime
import com.will.criminalintent.viewmodel.CrimeListViewModel

class CrimeListFragment: Fragment() {
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private lateinit var rvCrimeList : RecyclerView

    private var adapter: CrimeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("WillWolf", "Total crimes: ${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        rvCrimeList = view.findViewById(R.id.rv_crime_list)
        rvCrimeList.layoutManager = LinearLayoutManager(context)

        updateUI()
        return view
    }

    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        rvCrimeList.adapter = adapter
    }

    companion object {
        fun newInstance() : CrimeListFragment {
            return CrimeListFragment()
        }
    }

    // CrimeHolder 的 构造函数先接收 view 参数，然后将其作为值传递给 RecyclerView.ViewHolder
    // 这样 ViewHolder 基类的 itemView 属性就可以引用 view 值
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_crimeTitle)
        val tvData: TextView = view.findViewById(R.id.tv_crimeDate)
    }


    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.item_list_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes.get(position)
            holder.apply {
                tvTitle.text = crime.title
                tvData.text = crime.date.toString()
            }
        }

    }
}