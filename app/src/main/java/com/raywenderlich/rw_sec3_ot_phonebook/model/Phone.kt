package com.raywenderlich.rw_sec3_ot_phonebook.model

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raywenderlich.rw_sec3_ot_phonebook.util.FileUtils
import com.raywenderlich.rw_sec3_ot_phonebook.util.ImageUtils

@Entity
data class Phone(
    @PrimaryKey var id: String,
    var name: String = "",
    var memorySizes: String = "",
    var colors: String = "",
    var prices: String = "",
    var brand: String = ""
) {
    fun setImage(image: Bitmap, context: Context) {
        id?.let { id ->
            ImageUtils.saveBitmapToFile(
                context, image, generateImageFilename(id)
            )
        }
    }

    fun deleteImage(context: Context) {
        id?.let {
            FileUtils.deleteFile(context, generateImageFilename(it))
        }
    }

    companion object {
        fun generateImageFilename(id: String): String {
            return "phone$id.png"
        }
    }
}



