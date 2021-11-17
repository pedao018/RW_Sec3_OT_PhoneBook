package com.raywenderlich.rw_sec3_ot_phonebook.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.raywenderlich.rw_sec3_ot_phonebook.db.PhoneBookDatabase
import com.raywenderlich.rw_sec3_ot_phonebook.db.PhoneDao
import com.raywenderlich.rw_sec3_ot_phonebook.model.Phone

class PhoneRepo(private val context: Context) {
    private val db = PhoneBookDatabase.getInstance(context)
    private val phoneDao: PhoneDao = db.phoneDao()

    fun insertPhone(phone: Phone): Long {
        return phoneDao.insertPhone(phone)
    }

    fun updatePhone(phone: Phone) {
        phoneDao.updatePhone(phone)
    }

    fun deletePhone(phone: Phone) {
        phone.deleteImage(context)
        phoneDao.deletePhone(phone)
    }

    fun getLivePhone(phoneId: String): LiveData<Phone> =
        phoneDao.getLivePhone(phoneId = phoneId)

    fun getPhone(phoneId: String): Phone =
        phoneDao.getPhone(phoneId = phoneId)

    fun createPhoneEmpty(): Phone = Phone(id = "")

    val livePhoneList: LiveData<List<Phone>>
        get() {
            return phoneDao.getLivePhoneList()
        }
}