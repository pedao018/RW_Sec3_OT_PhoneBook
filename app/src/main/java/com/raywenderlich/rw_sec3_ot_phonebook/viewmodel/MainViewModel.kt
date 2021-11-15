package com.raywenderlich.rw_sec3_ot_phonebook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.raywenderlich.rw_sec3_ot_phonebook.model.Phone
import com.raywenderlich.rw_sec3_ot_phonebook.repository.PhoneRepo

class MainViewModel(application: Application) :
    AndroidViewModel(application) {
    private val phoneRepo: PhoneRepo = PhoneRepo(getApplication())
    private var phoneList: LiveData<List<PhoneView>>? = null
    private val liveDataTemp = MutableLiveData<List<PhoneView>>().default(initPhoneViewList())

    fun getListPhoneView(): LiveData<List<PhoneView>>? {
        if (phoneList == null)
            mapPhoneListTempToPhoneView()
        //mapPhoneListToPhoneView()
        return phoneList
    }

    private fun mapPhoneListTempToPhoneView() {
        phoneList = liveDataTemp
    }

    private fun mapPhoneListToPhoneView() {
        phoneList = Transformations.map(phoneRepo.livePhoneList) { repoPhoneList ->
            repoPhoneList.map { phoneItem -> phoneToPhoneView(phoneItem) }
        }
    }

    private fun phoneToPhoneView(phone: Phone) =
        PhoneView(
            id = phone.id,
            info = phone.name
        )

    data class PhoneView(
        var id: String = "",
        var info: String = ""
    )

    //MutableLiveData with initial value
    private fun <T : Any?> MutableLiveData<T>.default(initialValue: T) =
        apply { setValue(initialValue) }

    private fun initPhoneViewList(): List<PhoneView> {
        val phoneViewList = mutableListOf<PhoneView>()

        for (i in 1..5) {
            val phoneView = PhoneView("id$i", "name$i")
            phoneViewList.add(phoneView)
        }
        return phoneViewList
    }
}