package com.example.testapp.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN ="MMM d, yyy"

fun saveBitmapToFile(bmp: Bitmap, filePath: String) {
    val fos = FileOutputStream(filePath)
    FileOutputStream(filePath).use {
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos)
    }
}

fun rotateImage(img: Bitmap, degree: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree)
    val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    img.recycle()
    return rotatedImg
}

fun dateToStr(dt: Date): String {
    val simpleFormat = SimpleDateFormat(DATE_PATTERN)
    return simpleFormat.format(dt)
}

fun hideKeyboardFrom(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

