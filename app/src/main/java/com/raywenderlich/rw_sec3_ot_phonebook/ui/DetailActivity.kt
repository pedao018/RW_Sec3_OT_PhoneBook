package com.raywenderlich.rw_sec3_ot_phonebook.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.raywenderlich.rw_sec3_ot_phonebook.R
import com.raywenderlich.rw_sec3_ot_phonebook.databinding.ActivityDetailBinding
import com.raywenderlich.rw_sec3_ot_phonebook.util.ImageUtils
import com.raywenderlich.rw_sec3_ot_phonebook.viewmodel.DetailViewModel
import java.io.File

class DetailActivity : AppCompatActivity(),
    PhotoOptionDialogFragment.PhotoOptionDialogListener {
    private lateinit var databinding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel>()
    private var phoneDetailView: DetailViewModel.PhoneDetailView? = null
    private var photoFile: File? = null
    private lateinit var startCaptureForResult: ActivityResultLauncher<Intent>
    private lateinit var startSelectImageForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setupToolbar()
        getIntentData()

        startCaptureForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    //You return early from the method if there is no photoFile defined.
                    val photoFile = photoFile ?: return@registerForActivityResult

                    //Thu hồi lại permission: The permissions you set before are now revoked since they’re no longer needed.
                    val uri = FileProvider.getUriForFile(
                        this,
                        ImageUtils.AUTHORITY_IMAGE,
                        photoFile
                    )
                    revokeUriPermission(
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    //getImageWithPath() is called to get the image from the new photo path, and updateImage() is called to update the bookmark image
                    val image = getImageWithPath(photoFile.absolutePath)
                    val bitmap = ImageUtils.rotateImageIfRequired(this, image, uri)
                    updateImage(bitmap)
                }
            }
        startSelectImageForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data //Intent
                    if (data != null && data.data != null) {
                        val imageUri = data.data as Uri
                        val image = getImageWithAuthority(imageUri)
                        image?.let {
                            val bitmap = ImageUtils.rotateImageIfRequired(this, it, imageUri)
                            updateImage(bitmap)
                        }
                    }
                }
            }
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
            deletePhone()
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
            populateImageView()
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

    private fun deletePhone() {
        val phoneView = phoneDetailView ?: return
        AlertDialog.Builder(this)
            .setMessage("Delete?")
            .setPositiveButton("Ok") { _, _ ->
                viewModel.deletePhone(phoneView)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .create().show()
    }

    private fun populateImageView() {
        phoneDetailView?.let { phoneView ->
            val phoneImage = phoneView.getImage(this)
            phoneImage?.let {
                databinding.imageViewPlace.setImageBitmap(phoneImage)
            }
        }
        databinding.imageViewPlace.setOnClickListener { replaceImage() }
    }

    private fun replaceImage() {
        val newFragment = PhotoOptionDialogFragment.newInstance(this)
        newFragment?.show(supportFragmentManager, "photoOptionDialog")
    }

    //This method uses the new decodeFileSize method to load the downsampled image and return it.
    private fun getImageWithPath(filePath: String) = ImageUtils.decodeFileToSize(
        filePath,
        resources.getDimensionPixelSize(R.dimen.default_image_width),
        resources.getDimensionPixelSize(R.dimen.default_image_height)
    )

    private fun getImageWithAuthority(uri: Uri) = ImageUtils.decodeUriStreamToSize(
        uri,
        resources.getDimensionPixelSize(R.dimen.default_image_width),
        resources.getDimensionPixelSize(R.dimen.default_image_height),
        this
    )

    private fun updateImage(image: Bitmap) {
        phoneDetailView?.let {
            databinding.imageViewPlace.setImageBitmap(image)
            it.setImage(this, image)
        }
    }

    override fun onCaptureClick() {
        photoFile = null
        try {
            photoFile = ImageUtils.createUniqueImageFile(this)
        } catch (ex: java.io.IOException) {
            return
        }

        photoFile?.let { photoFile ->
            //FileProvider.getUriForFile() is called to get a Uri for the temporary photo file.
            val photoUri = FileProvider.getUriForFile(
                this,
                ImageUtils.AUTHORITY_IMAGE,
                photoFile
            )

            //A new Intent is created with the ACTION_IMAGE_CAPTURE action.
            // This Intent is used to display the camera viewfinder and allow the user to snap a new photo.
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //The photoUri is added as an extra on the Intent, so the Intent knows where to save the full-size image captured by the user.
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

            //Temporary write permissions on the photoUri are given to the Intent
            val intentActivities = packageManager.queryIntentActivities(
                captureIntent, PackageManager.MATCH_DEFAULT_ONLY
            )
            intentActivities.map { it.activityInfo.packageName }
                .forEach {
                    grantUriPermission(
                        it, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
            startCaptureForResult.launch(captureIntent)
        }

    }

    override fun onPickClick() {
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startSelectImageForResult.launch(pickIntent)
    }
}