package com.will.criminalintent.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.will.criminalintent.data.Crime
import com.will.criminalintent.database.CrimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class CrimeDetailViewModel(): ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    // 为什么要把 crimeId 封装在 LiveData 里，因为 【LiveData 数据转换】是设置两个 LiveData 对象
    // 之间触发和反馈关系的一个解决办法
    // 一个数据转换函数需要两个参数：一个用作触发器的 LiveData 对象，一个返回 LiveData 对象
    // 的映射函数，
    // 数据转换函数会返回一个数据转换结果 其实 就是一个新 LiveData 对象。每次只要触发其 LiveData
    // 有新值设置，数据转换函数返回的新 LiveData 对象的值就会得到更新。
    // 这样意味着 CrimeFragment 只需要观察 CrimeDetailViewModel.crimeLiveData 一次。当 CrimeFragment
    // 更改了要显示 crime 记录的 ID，CrimeDetailViewModel 就会把新的 crime 数据发布给 LiveData 数据流

    var crimeLiveData: LiveData<Crime?> = crimeIdLiveData.switchMap {crimeId ->
        // 这个 map 函数会自动包装成 LiveData，不再使用 Transformations.switchMap 方法了
        crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    fun getPhotoFile(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }

    fun launchTest() {
        viewModelScope.launch {
            // 切到后台线程，执行任务
            val result = withContext(Dispatchers.IO) {
                "my value"
            }
            // 主线程显示结果
            println(result)
        }
    }

    var num: LiveData<Int> = liveData{
        val result = withContext(Dispatchers.IO) {
            24
        }
        println(result.toString() + ", " + Thread.currentThread().name)
        emit(result)
    }
}