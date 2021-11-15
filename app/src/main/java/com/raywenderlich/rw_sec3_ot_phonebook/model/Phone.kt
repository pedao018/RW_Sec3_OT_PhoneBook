package com.raywenderlich.rw_sec3_ot_phonebook.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Phone(
    @PrimaryKey var id: String,
    var name: String = "",
    var memorySizes: String = "",
    var colors: String = "",
    var prices: String = "",
    var brand: String = ""
)



