package com.will.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.will.criminalintent.data.Crime
import java.util.UUID

@Dao
interface CrimeDao {
    // 返回 liveData 实例，就是告诉 room 在后台线程上执行数据库查询，查询到数据后，LiveData 对象会把结果
    // 发到主线程并通知 Ui 观察者
    @Query("select * from Crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("select * from Crime where id = (:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    // update 和 insert 注解不需要任何参数，room 会使用它们产生合适的 sql 操作命令
    @Update
    fun updateCrime(crime: Crime)

    @Insert
    fun addCrime(crime: Crime)
}