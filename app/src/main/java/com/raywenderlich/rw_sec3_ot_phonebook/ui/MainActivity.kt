package com.raywenderlich.rw_sec3_ot_phonebook.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.rw_sec3_ot_phonebook.adapter.PhoneListAdapter
import com.raywenderlich.rw_sec3_ot_phonebook.databinding.ActivityMainBinding
import com.raywenderlich.rw_sec3_ot_phonebook.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var phoneListAdapter: PhoneListAdapter

    companion object {
        const val EXTRA_PHONE_ID = "EXTRA_PHONE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        createPhoneObserver()
    }

    private fun setupView() {
        binding.mainListAddBtn.setOnClickListener {
            startBookmarkDetails("")
        }

        val layoutManager = LinearLayoutManager(this)
        binding.mainListPhoneRecycle.layoutManager = layoutManager
        phoneListAdapter = PhoneListAdapter(
            null,
            this,
            onItemClick = { itemObj -> startBookmarkDetails(itemObj.id) })
        binding.mainListPhoneRecycle.adapter = phoneListAdapter
    }

    private fun createPhoneObserver() {
        viewModel.getListPhoneView()?.observe(this,
            {
                phoneListAdapter.setListData(it)
            })
    }

    private fun startBookmarkDetails(phoneId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(EXTRA_PHONE_ID, phoneId)
        startActivity(intent)
    }
}