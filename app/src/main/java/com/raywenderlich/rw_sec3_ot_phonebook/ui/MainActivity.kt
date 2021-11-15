package com.raywenderlich.rw_sec3_ot_phonebook.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        createPhoneObserver()
    }

    private fun setupView() {
        binding.mainListAddBtn.setOnClickListener{
            Toast.makeText(this, "Click Add Btn", Toast.LENGTH_SHORT).show()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.mainListPhoneRecycle.layoutManager = layoutManager
        phoneListAdapter = PhoneListAdapter(null, this)
        binding.mainListPhoneRecycle.adapter = phoneListAdapter
    }

    private fun createPhoneObserver() {
        viewModel.getListPhoneView()?.observe(this,
            {
                phoneListAdapter.setListData(it)
            })
    }
}