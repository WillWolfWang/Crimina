package com.will.criminalintent.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.will.criminalintent.database.CrimeTypeConverters
import java.util.Date
import java.util.UUID

@Entity
data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(), var title: String = "",
                 var date:Date = Date(), var isSolved:Boolean = false, var suspect: String = ""/* var requiresPolice: Boolean = false*/) {

    val photoFileName
        get() = "IMG_$id.jpg"


}