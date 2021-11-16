package com.raywenderlich.rw_sec3_ot_phonebook.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.raywenderlich.rw_sec3_ot_phonebook.model.Phone
import com.raywenderlich.rw_sec3_ot_phonebook.repository.PhoneRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(application: Application) :
    AndroidViewModel(application) {
    private val phoneRepo: PhoneRepo = PhoneRepo(getApplication())
    private var phoneDetailView: LiveData<PhoneDetailView>? = null
    private var onEventSuccess: ((event: String) -> Unit) = { event ->
        when (event) {
            EVENT_ADD -> Toast.makeText(getApplication(), "Save Success", Toast.LENGTH_SHORT).show()
            EVENT_DELETE -> Toast.makeText(getApplication(), "Delete Success", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private const val EVENT_ADD = "EVENT_ADD"
        private const val EVENT_DELETE = "EVENT_DELETE"
    }

    fun getPhoneDetailView(phoneId: String): LiveData<PhoneDetailView>? {
        if (phoneDetailView == null)
            mapPhoneToPhoneDetailView(phoneId)
        return phoneDetailView
    }

    fun getListPhoneSize() =
        Transformations.map(phoneRepo.livePhoneList) { repoPhoneList ->
            repoPhoneList?.size ?: 0
        }

    fun saveData(phoneDetailView: PhoneDetailView) {
        GlobalScope.launch {
            // task, do smth in io thread
            var phone = getPhoneRepo(phoneDetailView)
            if (phone == null) {
                phone = phoneRepo.createPhoneEmpty()
                phoneDetailViewToPhone(phoneDetailView, phone)
                phoneRepo.insertPhone(phone)
            } else {
                phoneDetailViewToPhone(phoneDetailView, phone)
                phoneRepo.updatePhone(phone)
            }
            withContext(Dispatchers.Main) {
                // do smth in main thread after task is finished
                onEventSuccess.invoke(EVENT_ADD)
            }
        }
    }

    fun deleteBookmark(phoneDetailView: PhoneDetailView) {
        GlobalScope.launch {
            // task, do smth in io thread
            var phone = getPhoneRepo(phoneDetailView)
            phone?.let {
                phoneRepo.deletePhone(it)
            }
            withContext(Dispatchers.Main) {
                // do smth in main thread after task is finished
                onEventSuccess.invoke(EVENT_DELETE)
            }
        }
    }

    private fun getPhoneRepo(phoneDetailView: PhoneDetailView): Phone? {
        val phone = phoneDetailView.id?.let { id ->
            phoneRepo.getPhone(id)
        }
        return phone
    }

    private fun phoneDetailViewToPhone(phoneDetailView: PhoneDetailView, phone: Phone) {
        phone.id = phoneDetailView.id
        phone.name = phoneDetailView.name
        phone.memorySizes = phoneDetailView.memorySizes
        phone.colors = phoneDetailView.colors
        phone.prices = phoneDetailView.prices
        phone.brand = phoneDetailView.brand
    }

    private fun mapPhoneToPhoneDetailView(phoneId: String) {
        val phone = phoneRepo.getLivePhone(phoneId)
        phoneDetailView = Transformations.map(phone) { repoPhone ->
            repoPhone?.let {
                phoneToPhoneDetailView(repoPhone)
            }
        }
    }

    private fun phoneToPhoneDetailView(phone: Phone): PhoneDetailView {
        val phoneDetailView = PhoneDetailView.newInstance(phone.id)
        phoneDetailView.name = phone.name
        phoneDetailView.memorySizes = phone.memorySizes
        phoneDetailView.colors = phone.colors
        phoneDetailView.prices = phone.prices
        phoneDetailView.brand = phone.brand
        return phoneDetailView
    }

    data class PhoneDetailView private constructor(
        var id: String,
        var name: String = "",
        var memorySizes: String = "",
        var colors: String = "",
        var prices: String = "",
        var brand: String = ""
    ) {
        companion object {
            fun newInstance(phoneId: String) = PhoneDetailView(phoneId)
        }
    }

}