package com.raywenderlich.rw_sec3_ot_phonebook.viewmodel

import android.app.Application
import android.os.Build
import android.text.Html
import android.text.Spanned
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
        //mapPhoneListTempToPhoneView()
            mapPhoneListToPhoneView()
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

    private fun phoneToPhoneView(phone: Phone): PhoneView {
        val info = "<p><h5>${phone.name}</h5><br>" +
                "<b>Another Info:</b><br> <b>MemorySizes: </b>${phone.memorySizes}.<br> " +
                "<b>Colors: </b>${phone.colors}.<br> " +
                "<b>Prices: </b>${phone.prices}.<br> " +
                "<b>Brand: </b>${phone.brand}</p>"
        return PhoneView(
            id = phone.id,
            info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(info, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(info)
            }
        )
    }

    data class PhoneView(
        var id: String = "",
        var info: Spanned? = null
    )

    //MutableLiveData with initial value
    private fun <T : Any?> MutableLiveData<T>.default(initialValue: T) =
        apply { setValue(initialValue) }

    private fun initPhoneViewList(): List<PhoneView> {
        val phoneViewList = mutableListOf<PhoneView>()
        var phone: Phone
        for (i in 1..5) {
            phone = Phone(
                id = "phoneID$i",
                name = "Phone $i",
                memorySizes = "128GB,256GB,512GB",
                colors = "Red,Green,Blue",
                prices = "${i}00$,${i}50$,${i}90$",
                brand = "Brand $i"
            )
            phoneViewList.add(phoneToPhoneView(phone))
        }
        return phoneViewList
    }
}