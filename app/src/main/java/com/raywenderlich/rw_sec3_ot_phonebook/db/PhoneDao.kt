package com.raywenderlich.rw_sec3_ot_phonebook.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.raywenderlich.rw_sec3_ot_phonebook.model.Phone

@Dao
interface PhoneDao {
    @Query("SELECT * FROM Phone ORDER BY brand,name")
    fun getLivePhoneList(): LiveData<List<Phone>>

    @Query("SELECT * FROM Phone WHERE id =:phoneId")
    fun getPhone(phoneId: String): Phone

    @Query("SELECT * FROM Phone WHERE id =:phoneId")
    fun getLivePhone(phoneId: String): LiveData<Phone>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPhone(phone: Phone): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePhone(phone: Phone)

    @Delete
    fun deletePhone(phone: Phone)
}