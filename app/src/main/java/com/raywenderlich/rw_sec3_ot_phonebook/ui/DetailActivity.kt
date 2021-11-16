package com.raywenderlich.rw_sec3_ot_phonebook.ui

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.raywenderlich.rw_sec3_ot_phonebook.R
import com.raywenderlich.rw_sec3_ot_phonebook.databinding.ActivityDetailBinding
import com.raywenderlich.rw_sec3_ot_phonebook.viewmodel.DetailViewModel
import com.raywenderlich.rw_sec3_ot_phonebook.viewmodel.MainViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var databinding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel>()
    private var phoneDetailView: DetailViewModel.PhoneDetailView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setupToolbar()
        getIntentData()
    }

    private fun setupToolbar() {
        setSupportActionBar(databinding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> {
            validateData()
            true
        }
        R.id.action_delete -> {
            deleteBookmark()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun getIntentData() {
        val phoneId = intent.getStringExtra(MainActivity.EXTRA_PHONE_ID)
        createPhoneDetailObserver(phoneId ?: "")
    }

    private fun createPhoneDetailObserver(phoneId: String) {
        viewModel.getPhoneDetailView(phoneId)?.observe(this, {
            phoneDetailView = it
            databinding.phoneDetailView = it
            if (phoneDetailView == null)
                viewModel.getListPhoneSize().observe(this, {
                    phoneDetailView = DetailViewModel.PhoneDetailView.newInstance(it.toString())
                })
        })
    }

    private fun validateData() {
        val name = databinding.editTextName.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is Empty!", Toast.LENGTH_SHORT).show()
            return
        }
        phoneDetailView?.let { phoneDetailView ->
            phoneDetailView.name = name
            phoneDetailView.memorySizes = databinding.editTextMemorySizes.text.toString()
            phoneDetailView.colors = databinding.editTextColors.text.toString()
            phoneDetailView.prices = databinding.editTextPrices.text.toString()
            phoneDetailView.brand = databinding.editTextBrand.text.toString()
            viewModel.saveData(phoneDetailView)
        }
        //finish()
    }

    private fun deleteBookmark() {
        val bookmarkView = phoneDetailView ?: return
        AlertDialog.Builder(this)
            .setMessage("Delete?")
            .setPositiveButton("Ok") { _, _ ->
                viewModel.deleteBookmark(bookmarkView)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .create().show()
    }
}