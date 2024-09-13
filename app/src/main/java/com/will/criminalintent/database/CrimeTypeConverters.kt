package com.will.criminalintent.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.will.criminalintent.data.Crime
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {
    // 注意注解，有个类似的 TypeConverters，用错了很难发现错误
    @TypeConverter
    fun fromData(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(mills: Long): Date {
        return mills.let {
            Date(it)
        }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }
}